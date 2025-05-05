package com.tvsm.iqubeindia.domain.repository

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.wear.phone.interactions.PhoneTypeHelper
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.wear.widget.ConfirmationOverlay
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.NodeClient
import com.tvsm.iqubeindia.domain.entities.PreConditionCheckResult
import com.tvsm.iqubeindia.presentation.WearDashboardActivity
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class PhoneConnectionCheckImpl @Inject constructor(
    private val mActivity: Context,
    val mNodeClient: NodeClient,
    val mCapabilityClient: CapabilityClient,
    val mRemoteActivityHelper: RemoteActivityHelper
) : PhoneConnectionCheckRepository {

    val TAG = PhoneConnectionCheckImpl::class.java.simpleName

    override suspend fun combinedPreConditionCheck(): PreConditionCheckResult {
        val preconditionCheckResult = if(checkIfPhoneIsConnected()){
            if(checkIfPhoneHasTvsElectricApp()){
                PreConditionCheckResult.PreConditionOk
            } else{
                PreConditionCheckResult.AppNotInstalled
            }
        } else{
            PreConditionCheckResult.PhoneNotConnected
        }
        return preconditionCheckResult
    }

    suspend fun checkIfPhoneIsConnected() : Boolean{
        var isPhoneConnected = false
        try{
            val connectedNodes = mNodeClient
                .connectedNodes
                .await()

            Log.d(TAG, "connectedNode succeeded : ${mNodeClient.connectedNodes.isSuccessful}")
            Log.d(TAG, "connectedNodes : $connectedNodes ${connectedNodes.size}" +
                    "${connectedNodes.filter { it.isNearby }} : " +
                    "${connectedNodes.filter { it.isNearby }.count()}")
            isPhoneConnected = if(connectedNodes.size > 0 &&
                connectedNodes.filter { it.isNearby }.count() != 0) {
                true
            } else{
                Log.e(TAG,"No Connected Nodes found! show dialog!")
                false
            }
        }catch (cancellationException: CancellationException) {
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            Log.d(TAG, "Capability request failed to return any results.")
        }

        return isPhoneConnected
    }

    suspend fun checkIfPhoneHasTvsElectricApp(): Boolean {
        Log.d(TAG, "checkIfPhoneHasTvsElectricApp()")
        var phoneHasTvsElectricApp = false

        try{
            val capabilityInfo = mCapabilityClient
                .getCapability(WearDashboardActivity.CAPABILITY_PHONE_APP, CapabilityClient.FILTER_ALL)
                .await()

            Log.d(TAG, "Capability request succeeded: ${mCapabilityClient
                .getCapability(WearDashboardActivity.CAPABILITY_PHONE_APP, CapabilityClient.FILTER_ALL)
                .isComplete}")

            Log.d(TAG, "capabilityInfo : $capabilityInfo ${capabilityInfo.nodes.size}")
            if(capabilityInfo.nodes.size > 0) {
                phoneHasTvsElectricApp = true
                capabilityInfo.nodes.firstOrNull()
                    ?.let {
                    /*TODO Find the usage of below api*/
                    //mDataLayerViewModel.setPhoneNodewithApp(it)
                    }
            }
            else{
                Log.e(TAG,"No mobile app found! show dialog!")
                phoneHasTvsElectricApp = false
            }
        }catch (cancellationException: CancellationException) {
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            Log.d(TAG, "Capability request failed to return any results.")
        }

        return phoneHasTvsElectricApp
    }

    override suspend fun openAppInStoreOnPhone(componentActivityContext: Activity) {
        val intent = when (PhoneTypeHelper.getPhoneDeviceType(mActivity.applicationContext)) {
            PhoneTypeHelper.DEVICE_TYPE_ANDROID -> {
                Log.d(TAG, "\tDEVICE_TYPE_ANDROID")
                // Create Remote Intent to open Play Store listing of app on remote device.
                Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.parse(ANDROID_MARKET_APP_URI))
            }

            PhoneTypeHelper.DEVICE_TYPE_IOS -> {
                Log.d(TAG, "\tDEVICE_TYPE_IOS")
                // Create Remote Intent to open App Store listing of app on iPhone.
                Intent(Intent.ACTION_VIEW)
                    .addCategory(Intent.CATEGORY_BROWSABLE)
                    .setData(Uri.parse(APPLE_MARKET_APP_URI))
            }

            else -> {
                Log.d(TAG, "\tDEVICE_TYPE_ERROR_UNKNOWN")
                return
            }
        }
        try {
            mRemoteActivityHelper.startRemoteActivity(intent).await()
            ConfirmationOverlay().showOn(componentActivityContext)
        } catch (e: Exception) {
            // Request was cancelled normally
            Log.e(TAG, "openAppInStoreOnPhone CancellationException: ${e.message}")
        } finally {
            componentActivityContext.finish()
        }
    }

}