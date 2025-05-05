package com.tvsm.iqubeindia.presentation.ui.utils

object TvsElectricConstants {
    //SOC thresholds
    const val LowChargeThreshold = 20
    const val FullChargeThreshold = 100

    //TPMS thresholds; TODO get actual values from vehicle team and update here
    const val TpmsFrontMinValue = 18
    const val TpmsRearMinValue = 24
    const val TpmsFrontMaxValue = 29
    const val TpmsRearMaxValue = 40

    const val OfflineStateOpacity = 0.5f
}