package com.tvsm.iqubeindia.domain.usecase

import com.tvsm.iqubeindia.domain.repository.WearableDataLayerResponse
import com.tvsm.iqubeindia.domain.repository.WearableListenerRepository
import com.tvsm.iqubeindia.presentation.ui.utils.RemoteRequests
import javax.inject.Inject


class WearableUseCase @Inject constructor(
    private val wearableListenerRepository: WearableListenerRepository
) {
    fun registerWearableListeners() = wearableListenerRepository.registerWearableListeners()
    fun unregisterWearableListeners() = wearableListenerRepository.unregisterWearableListeners()
    fun subScribeWearableDataLayerResponse(mResponse: WearableDataLayerResponse?) =
        wearableListenerRepository.subScribeWearableDataLayerResponse(mResponse)
    suspend fun getLoginStateFromMobileApp() : Boolean = wearableListenerRepository.getLoginStateFromMobileApp()

    suspend fun getPrepPurchaseLoginStateFromMobileApp() : Boolean = wearableListenerRepository.getPrepPurchaseLoginStateFromMobileApp()

    suspend fun getAuthCredentialsFromPhone() = wearableListenerRepository.getAuthCredentialsFromPhone()
    suspend fun sendRemoteOpsRequest(remoteOpRequest: RemoteRequests, remoteOp: Boolean = true) =
        wearableListenerRepository.sendRemoteOpsRequest(remoteOpRequest, remoteOp)
    suspend fun getRemoteOpsStatusFromDataLayer() = wearableListenerRepository.getRemoteOpsStatusFromDataLayer()
}
