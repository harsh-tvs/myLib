package com.tvsm.iqubeindia.domain.repository

import android.app.Activity
import com.tvsm.iqubeindia.domain.entities.PreConditionCheckResult

interface PhoneConnectionCheckRepository {

    suspend fun combinedPreConditionCheck(): PreConditionCheckResult
    suspend fun openAppInStoreOnPhone(componentActivityContext: Activity)

    val ANDROID_MARKET_APP_URI: String
        get() = "market://details?id=com.tvsm.connect"
    val APPLE_MARKET_APP_URI: String
        get() = "https://apps.apple.com/in/app/tvs-iqube/id1493491587"
}