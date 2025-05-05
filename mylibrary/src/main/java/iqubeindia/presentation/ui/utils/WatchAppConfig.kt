package com.tvsm.iqubeindia.presentation.ui.utils

import android.content.Context
import android.content.Intent
import com.tvsm.iqubeindia.presentation.WearDashboardActivity

object WatchAppConfig {
    const val P360_PRODUCTION_URL = "https://p360.tvsmotor.com/"
    const val P360_WSS_PRODUCTION_URL = "wss://p360.tvsmotor.com/"

    const val P360_UAT_URL = "https://p360uat.tvsmotor.com/"
    const val P360_WSS_UAT_URL = "wss://p360uat.tvsmotor.com/"
}
object TvsSdkLauncher {
    fun launchWearDashboard(context: Context) {
        val intent = Intent(context, WearDashboardActivity::class.java)
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}