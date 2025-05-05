package com.tvsm.iqubeindia.domain.usecase

import android.app.Activity
import com.tvsm.iqubeindia.domain.entities.PreConditionCheckResult
import com.tvsm.iqubeindia.domain.repository.PhoneConnectionCheckRepository
import javax.inject.Inject


class PhoneConnectionCheckUseCase @Inject constructor(
    private val phoneConnectionCheckRepository: PhoneConnectionCheckRepository
) {
    suspend fun combinedPreConditionCheck(): PreConditionCheckResult =
        phoneConnectionCheckRepository.combinedPreConditionCheck()

    suspend fun openAppInStoreOnPhone(componentActivityContext: Activity) =
        phoneConnectionCheckRepository.openAppInStoreOnPhone(componentActivityContext)
}
