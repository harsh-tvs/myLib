package com.tvsm.iqubeindia.domain.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.*
import com.tvsm.iqubeindia.presentation.WearDashboardActivity
import com.tvsm.iqubeindia.presentation.ui.data.CodpAccessCredentials
import com.tvsm.iqubeindia.presentation.ui.utils.RemoteOpState
import com.tvsm.iqubeindia.presentation.ui.utils.RemoteRequests
import com.tvsm.iqubeindia.presentation.ui.utils.WearableConstants
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WearableListenerImpl @Inject constructor(
    private val mActivity: Context,
    val mNodeClient: NodeClient,
    val mDataClient: DataClient,
    val mCapabilityClient: CapabilityClient,
    val mMessageClient: MessageClient,
    val vehicleDetailsRecordRepository: VehicleDetailsRecordRepository
) : WearableListenerRepository, DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    val TAG = WearableListenerImpl::class.java.simpleName

    private var androidPhoneNodeWithApp: Node? = null
    private var wearableDataLayerResponse : WearableDataLayerResponse? = null

    override fun registerWearableListeners() {
        mCapabilityClient.addListener(this, WearDashboardActivity.CAPABILITY_PHONE_APP)
        mDataClient.addListener(this)
        mMessageClient.addListener(this)
    }

    override fun unregisterWearableListeners() {
        mCapabilityClient.removeListener(this)
        mDataClient.removeListener(this)
        mMessageClient.removeListener(this)
    }

    override fun subScribeWearableDataLayerResponse(mResponse: WearableDataLayerResponse?) {
        wearableDataLayerResponse = mResponse
    }

    override suspend fun getLoginStateFromMobileApp(): Boolean {
        var loginStatus = false
        val dataItemTask = mDataClient
            .getDataItems(Uri.parse(WearableConstants.WearPathPrefix + WearableConstants.LoginStateResponseUri))
            .await()

        Log.d(TAG, "login dataItemCount: ${dataItemTask.count}")
        if(dataItemTask.count > 0) {
            val dataMapItem = DataMapItem.fromDataItem(dataItemTask.first())
            loginStatus = dataMapItem?.dataMap!!.getBoolean(WearableConstants.LoginStatusKey)
            Log.d(TAG, "get loginStatus: $loginStatus")
        }
        dataItemTask.release()
        return loginStatus
    }

    // for getting prePurchaseLoginStatus for Non tvs user
    override suspend fun getPrepPurchaseLoginStateFromMobileApp(): Boolean {
        var prePurchaseLoginStatus = false
        val dataItemTask = mDataClient
            .getDataItems(Uri.parse(WearableConstants.WearPathPrefix + WearableConstants.LoginStateResponseUri))
            .await()
        Log.d(TAG, "login dataItemCount: ${dataItemTask.count}")
        if(dataItemTask.count > 0) {
            val dataMapItem = DataMapItem.fromDataItem(dataItemTask.first())
            prePurchaseLoginStatus= dataMapItem?.dataMap!!.getBoolean(WearableConstants.PrePurchaseLoginStatusKey)
            Log.d(TAG, "get loginStatus: $prePurchaseLoginStatus")
        }
        dataItemTask.release()
        return prePurchaseLoginStatus
    }

    override suspend fun getAuthCredentialsFromPhone() {
        val dataItemTask = mDataClient
            .getDataItems(Uri.parse(WearableConstants.WearPathPrefix+ WearableConstants.AuthResponseUri))
            .await()

        Log.d(TAG, "auth dataItemCount: ${dataItemTask.count}")
        if(dataItemTask.count > 0) {
            val dataMapItem = DataMapItem.fromDataItem(dataItemTask.first())
            parseAuthData(dataMapItem.dataMap)
        }
        dataItemTask.release()
        Log.d(TAG, "get auth data")
    }

    override suspend fun sendRemoteOpsRequest(remoteOpRequest: RemoteRequests, remoteOp: Boolean) {
        lateinit var putDataRequest: PutDataRequest
        when(remoteOpRequest){
            RemoteRequests.RequestAuthData -> {
                putDataRequest = PutDataMapRequest.createWithAutoAppendedId(WearableConstants.AuthReqUri)
                    .run {
                    dataMap.putBoolean(WearableConstants.AuthReqKey, remoteOp)
                    asPutDataRequest()
                }
            }
            RemoteRequests.RemoteOpsAvailability -> {
                putDataRequest = PutDataMapRequest.createWithAutoAppendedId(WearableConstants.RemoteOpReqUri)
                    .run {
                    dataMap.putBoolean(WearableConstants.RemoteOpReqKey, remoteOp)
                    asPutDataRequest()
                }
            }
            RemoteRequests.LockUnlock -> {
                putDataRequest = PutDataMapRequest.createWithAutoAppendedId(WearableConstants.LockVehicleReqUri)
                    .run {
                    dataMap.putBoolean(WearableConstants.LockVehicleReqKey, remoteOp)
                    asPutDataRequest()
                }
            }
            RemoteRequests.TrunkUnlock -> {
                putDataRequest = PutDataMapRequest.createWithAutoAppendedId(WearableConstants.TrunkUnlockReqUri)
                    .run {
                    dataMap.putBoolean(WearableConstants.TrunkUnlockReqKey, remoteOp)
                    asPutDataRequest()
                }
            }
            RemoteRequests.FindMe -> {
                putDataRequest = PutDataMapRequest.createWithAutoAppendedId(WearableConstants.FindMeReqUri)
                    .run {
                    dataMap.putBoolean(WearableConstants.FindMeReqKey, remoteOp)
                    asPutDataRequest()
                }
            }
        }
        val putDataTask: Task<DataItem> = mDataClient.putDataItem(putDataRequest)
        Log.d(TAG, "Message sent successfully to ${putDataRequest.uri}")
    }

    fun parseAuthData(dataMap: DataMap){

        val codpToken = dataMap.getString(WearableConstants.CodpTokenKey)
        val profileName = dataMap.getString(WearableConstants.UserNameKey)
        val vin = dataMap.getString(WearableConstants.VinKey)
        val vehicleModel = dataMap.getString(WearableConstants.VehicleModelKey)
        val vehicleColor = dataMap.getString(WearableConstants.VehicleColorKey)
        val subscriptionPlan = dataMap.getString(WearableConstants.SubScriptionPlanKey)
        val subscriptionDate = dataMap.getString(WearableConstants.SubscriptionExpireyKey)
        val vehicleImageUrl=dataMap.getString(WearableConstants.VehicleImageUrlKey)

        Log.d(TAG, "Parse auth Data: $codpToken")

        val mCodpCredentials =
            if (codpToken != null && profileName != null && vin != null && vehicleModel != null &&
                vehicleColor != null && subscriptionPlan != null && subscriptionDate != null && vehicleImageUrl !=null
            ) {
                CodpAccessCredentials(codpToken = codpToken, profileName = profileName, vin = vin,
                    vehicleModel = vehicleModel, subscriptionPlan = subscriptionPlan,
                    vehicleColor = vehicleColor, subscriptionExpiry = subscriptionDate,vehicleImageUrl=vehicleImageUrl)

            } else null
        mCodpCredentials?.let {
            vehicleDetailsRecordRepository.putCodpAccessCredsPref(it)
        }
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "onDataChanged")
        dataEvents.forEach { dataEvent ->
            Log.d(TAG, "Data Event Received: Type : ${dataEvent.type} " +
                    "data uri: ${dataEvent.dataItem.uri}" +
                    "data: ${dataEvent.dataItem.data}")
            when(dataEvent.type){
                DataEvent.TYPE_CHANGED -> {
                    val dataItemPath = dataEvent.dataItem.uri.path ?: ""
                    with(dataItemPath) {
                        when {
                            startsWith(WearableConstants.LoginStateResponseUri) -> {
                                val loginStatus = DataMapItem.fromDataItem(dataEvent.dataItem)
                                    .dataMap.getBoolean(WearableConstants.LoginStatusKey)
                                wearableDataLayerResponse?.onLoginStateUpdated(loginStatus)
                            }
                            startsWith(WearableConstants.AuthResponseUri) -> {
                                parseAuthData(DataMapItem.fromDataItem(dataEvent.dataItem).dataMap)
                            }
                            startsWith(WearableConstants.LockVehicleResponseUri) -> {
                                //_vehicleLockOpStatus.value = RemoteOpState.CommandInit
                                val commandReceived = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getBoolean(WearableConstants.CommandReceivedKey)
                                val commandExecuted = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getBoolean(WearableConstants.CommandExecutedKey)
                                val lockStatus = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getBoolean(WearableConstants.LockStatusKey)
                                val errorMsg = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getString(WearableConstants.ErrorMessageKey)
                                Log.d(TAG, "commandReceived: $commandReceived ;" +
                                            "commandExecuted: $commandExecuted ;" +
                                            "lockStatus : $lockStatus ;" +
                                            "errorMsg : $errorMsg")

                                var mRemoteOperationStatus = vehicleDetailsRecordRepository.getRemoteOperationStatusPref()
                                mRemoteOperationStatus = mRemoteOperationStatus.copy(vehicleLockStatus = lockStatus, remoteOperationStatus = errorMsg)
                                vehicleDetailsRecordRepository.putRemoteOperationStatusPref(mRemoteOperationStatus)

                                /*TODO state is not defined for the status of commandReceived*/
                                /*if(commandReceived){
                                    _vehicleLockOpStatus.value = RemoteOpState.CommandSent
                                }
                                else*/ if(commandExecuted){
                                    wearableDataLayerResponse?.onVehicleLockOpStatusUpdated(
                                        RemoteOpState.CommandExecuted)
                                }
                                else {
                                    wearableDataLayerResponse?.onVehicleLockOpStatusUpdated(RemoteOpState.CommandError)
                                }
                            }
                            startsWith(WearableConstants.TrunkUnlockResponse) -> {
                                val commandReceived = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getBoolean(WearableConstants.CommandReceivedKey)
                                val commandExecuted = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getBoolean(WearableConstants.CommandExecutedKey)
                                val lockStatus = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getBoolean(WearableConstants.LockStatusKey)
                                val errorMsg = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getString(WearableConstants.ErrorMessageKey)
                                Log.d(
                                    TAG, "commandReceived: $commandReceived ;" +
                                            "commandExecuted: $commandExecuted ;" +
                                            "lockStatus : $lockStatus ;" +
                                            "errorMsg : $errorMsg"
                                )

                                var mRemoteOperationStatus = vehicleDetailsRecordRepository
                                    .getRemoteOperationStatusPref()
                                mRemoteOperationStatus = mRemoteOperationStatus
                                    .copy(vehicleBootStatus = lockStatus,
                                        remoteOperationStatus = errorMsg)
                                vehicleDetailsRecordRepository
                                    .putRemoteOperationStatusPref(mRemoteOperationStatus)

                                /*TODO state is not defined for the status of commandReceived*/
                                /*if(commandReceived){
                                    _trunkUnlockOpStatus.value = RemoteOpState.CommandSent
                                }
                                else*/ if(commandExecuted){
                                    wearableDataLayerResponse?.onTrunkUnlockOpStatusUpdated(RemoteOpState.CommandExecuted)
                                }
                                else {
                                    wearableDataLayerResponse?.onTrunkUnlockOpStatusUpdated(RemoteOpState.CommandError)
                                }
                            }
                            startsWith(WearableConstants.FindMeStatusUri) -> {
                                val commandReceived = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getBoolean(WearableConstants.CommandReceivedKey)
                                val commandExecuted = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getBoolean(WearableConstants.CommandExecutedKey)
                                val errorMsg = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getString(WearableConstants.ErrorMessageKey)
                                Log.d(
                                    TAG, "commandReceived: $commandReceived ;" +
                                            "commandExecuted: $commandExecuted ;" +
                                            "errorMsg : $errorMsg"
                                )
                                /*TODO state is not defined for the status of commandReceived*/
                                /*if(commandReceived){
                                    _findMeOpStatus.value = RemoteOpState.CommandSent
                                }
                                else*/ if(commandExecuted){
                                    wearableDataLayerResponse?.onFindMeOpStatusUpdated(RemoteOpState.CommandExecuted)
                                }
                                else{
                                    wearableDataLayerResponse?.onFindMeOpStatusUpdated(RemoteOpState.CommandError)
                                }
                            }
                            startsWith(WearableConstants.RemoteOpsStatusUri) -> {
                                val remoteOpsEnabled = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getBoolean(WearableConstants.RemoteOpsStatusKey)
                                val errorMsg = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                                        .getString(WearableConstants.ErrorMessageKey)
                                Log.d(TAG,"remoteOpsEnabled : $remoteOpsEnabled ;" + "errorMsg : $errorMsg")
                                wearableDataLayerResponse?.onIsRemoteOpsEnabledUpdated(remoteOpsEnabled)

                                var mRemoteOperationStatus = vehicleDetailsRecordRepository.getRemoteOperationStatusPref()
                                mRemoteOperationStatus = mRemoteOperationStatus.copy(isRemoteOpEnabled = remoteOpsEnabled, remoteOperationStatus = errorMsg)
                                vehicleDetailsRecordRepository.putRemoteOperationStatusPref(mRemoteOperationStatus)
                            }
                            else -> {
                                //TODO What happens when a unknown uri comes
                                Log.e(TAG, "unknown uri")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "onMessageReceived: $messageEvent")
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d(TAG, "onCapabilityChanged: $capabilityInfo")
        androidPhoneNodeWithApp = capabilityInfo.nodes.firstOrNull()
    }

    override suspend fun getRemoteOpsStatusFromDataLayer() {
        val dataItemTaskRMS = mDataClient.getDataItems(Uri.parse(WearableConstants.WearPathPrefix
                +WearableConstants.RemoteOpsStatusUri))
            .await()
        val dataItemTaskLockStatus = mDataClient.getDataItems(Uri.parse(WearableConstants.WearPathPrefix
                +WearableConstants.LockVehicleResponseUri))
            .await()
        val dataItemTaskTrunkStatus = mDataClient.getDataItems(Uri.parse(WearableConstants.WearPathPrefix
                +WearableConstants.TrunkUnlockResponse))
            .await()

        Log.d(TAG, "remoteOpsState dataItemCount: ${dataItemTaskRMS.count}")
        if(dataItemTaskRMS.count > 0) {
            val dataMapItem = DataMapItem.fromDataItem(dataItemTaskRMS.first())

            val remoteOpsState = dataMapItem?.dataMap!!.getBoolean(WearableConstants.RemoteOpsStatusKey)
            val errorMsg =dataMapItem?.dataMap!!.getString(WearableConstants.ErrorMessageKey)

            wearableDataLayerResponse?.onIsRemoteOpsEnabledUpdated(remoteOpsState)

            var mRemoteOperationStatus = vehicleDetailsRecordRepository.getRemoteOperationStatusPref()
            mRemoteOperationStatus = mRemoteOperationStatus
                .copy(isRemoteOpEnabled = remoteOpsState, remoteOperationStatus = errorMsg)
            vehicleDetailsRecordRepository.putRemoteOperationStatusPref(mRemoteOperationStatus)

            Log.d(TAG, "get RemoteOpsStatus: $remoteOpsState")
        }

        if(dataItemTaskLockStatus.count > 0){
            val dataMapItem = DataMapItem.fromDataItem(dataItemTaskLockStatus.first())

            val lockState = dataMapItem?.dataMap!!.getBoolean(WearableConstants.LockStatusKey)

            Log.e(TAG, "lockState: $lockState")
            var mRemoteOperationStatus = vehicleDetailsRecordRepository.getRemoteOperationStatusPref()
            mRemoteOperationStatus = mRemoteOperationStatus.copy(vehicleLockStatus = lockState)
            vehicleDetailsRecordRepository.putRemoteOperationStatusPref(mRemoteOperationStatus)
        }

        if(dataItemTaskTrunkStatus.count > 0){
            val dataMapItem = DataMapItem.fromDataItem(dataItemTaskTrunkStatus.first())

            val lockState = dataMapItem?.dataMap!!.getBoolean(WearableConstants.LockStatusKey)

            Log.e(TAG, "bootstate: $lockState")

            var mRemoteOperationStatus = vehicleDetailsRecordRepository.getRemoteOperationStatusPref()
            mRemoteOperationStatus = mRemoteOperationStatus.copy( vehicleBootStatus = lockState)
            vehicleDetailsRecordRepository.putRemoteOperationStatusPref(mRemoteOperationStatus)
        }

        dataItemTaskRMS.release()
        dataItemTaskLockStatus.release()
        dataItemTaskTrunkStatus.release()
    }

}