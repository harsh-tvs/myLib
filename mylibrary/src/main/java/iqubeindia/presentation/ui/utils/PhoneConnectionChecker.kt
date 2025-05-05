package com.tvsm.iqubeindia.presentation.ui.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.wear.phone.interactions.PhoneTypeHelper
import androidx.wear.remote.interactions.RemoteActivityHelper
import androidx.wear.widget.ConfirmationOverlay
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.NodeClient
import com.tvsm.iqubeindia.presentation.WearDashboardActivity
import com.tvsm.iqubeindia.viewmodel.WearDataLayerViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.tasks.await

class WearablePhoneConnection(private val mNodeClient: NodeClient,
                             private val mCapabilityClient: CapabilityClient,
                             private val mDataLayerViewModel: WearDataLayerViewModel,
                             private val remoteActivityHelper: RemoteActivityHelper,
                             private val activity: Activity)
{

    val TAG = "PhoneConnectionChecker"

    suspend fun combinedPreConditionCheck() : PreconditionCheckResult{
        Log.d(TAG, "inside combinedPreConditionCheck")
        val preconditionCheckResult = if(checkIfPhoneIsConnected()){
            if(checkIfPhoneHasTvsElectricApp()){
                PreconditionCheckResult.PreConditionOk
            } else{
                PreconditionCheckResult.AppNotInstalled
            }
        } else{
            PreconditionCheckResult.PhoneNotConnected
        }

        Log.d(TAG, "preconditionCheckResult: $preconditionCheckResult")
        return preconditionCheckResult
    }

    suspend fun checkIfPhoneIsConnected() : Boolean{
        var isPhoneConnected = false
        try{
            val connectedNodes = mNodeClient
                .connectedNodes
                .await()

            Log.d(TAG, "connectedNode succeeded : ${mNodeClient.connectedNodes.isSuccessful}")

            //withContext(Dispatchers.Main){
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
            //}
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

            //withContext(Dispatchers.Main){
                Log.d(TAG, "capabilityInfo : $capabilityInfo ${capabilityInfo.nodes.size}")
                if(capabilityInfo.nodes.size > 0) {
                    phoneHasTvsElectricApp = true
                    capabilityInfo.nodes.firstOrNull()
                        ?.let { /*mDataLayerViewModel.setPhoneNodewithApp(it)*/ }
                }
                else{
                    Log.e(TAG,"No mobile app found! show dialog!")
                    phoneHasTvsElectricApp = false
                }
            //}
        }catch (cancellationException: CancellationException) {
            // Request was cancelled normally
        } catch (throwable: Throwable) {
            Log.d(TAG, "Capability request failed to return any results.")
        }

        return phoneHasTvsElectricApp
    }

    fun openAppInStoreOnPhone() {
        Log.d(TAG, "openAppInStoreOnPhone()")

        val intent = when (PhoneTypeHelper.getPhoneDeviceType(activity.applicationContext)) {
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

        CoroutineScope(Dispatchers.IO).launch{
            try {
                remoteActivityHelper.startRemoteActivity(intent).await()

                ConfirmationOverlay().showOn(activity)
            } catch (cancellationException: CancellationException) {
                // Request was cancelled normally
                throw cancellationException
            } catch (throwable: Throwable) {
                ConfirmationOverlay()
                    .setType(ConfirmationOverlay.FAILURE_ANIMATION)
                    .showOn(activity)
            }
        }
        activity.finish()
    }

    companion object{
        private const val ANDROID_MARKET_APP_URI =
            "market://details?id=com.tvsm.connect"
        private const val APPLE_MARKET_APP_URI =
            "https://apps.apple.com/in/app/tvs-iqube/id1493491587"

        enum class PreconditionCheckResult{
            PreConditionOk,
            PhoneNotConnected,
            AppNotInstalled,
            PreconditionFailed
        }
    }
}