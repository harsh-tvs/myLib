package com.tvsm.iqubeindia.presentation.ui.data

data class CodpAccessCredentials(
    var codpToken: String = "",
    var profileName: String = "",
    var vin: String = "",
    var vehicleModel: String = "",
    var vehicleColor: String = "",
    var subscriptionPlan: String = "",
    var subscriptionExpiry: String = "",
    var vehicleImageUrl: String=""
)