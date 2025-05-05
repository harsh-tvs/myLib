package com.tvsm.iqubeindia.domain.repository

import com.tvsm.iqubeindia.presentation.ui.utils.RemoteOpState
import com.tvsm.iqubeindia.presentation.ui.utils.RemoteRequests


interface WearableListenerRepository {
    fun registerWearableListeners()
    fun unregisterWearableListeners()
    fun subScribeWearableDataLayerResponse(mResponse : WearableDataLayerResponse?)
    suspend fun getLoginStateFromMobileApp(): Boolean
    suspend fun getAuthCredentialsFromPhone()
    suspend fun sendRemoteOpsRequest(remoteOpRequest: RemoteRequests, remoteOp: Boolean)
    suspend fun getRemoteOpsStatusFromDataLayer()
    suspend fun getPrepPurchaseLoginStateFromMobileApp(): Boolean

}

interface WearableDataLayerResponse {
    fun onLoginStateUpdated(loginState: Boolean)
    fun onVehicleLockOpStatusUpdated(vehicleLockOpStatus: RemoteOpState)
    fun onTrunkUnlockOpStatusUpdated(trunkUnlockOpStatus : RemoteOpState)
    fun onFindMeOpStatusUpdated(findMeOpStatus : RemoteOpState)
    fun onIsRemoteOpsEnabledUpdated(isRemoteOpsEnabled: Boolean)
}