package com.tvsm.iqubeindia.presentation.ui.data

data class RemoteOperationStatus(
    var isRemoteOpEnabled: Boolean = true,
    var remoteOperationStatus: String? = "",
    var vehicleLockStatus: Boolean = false,
    var vehicleBootStatus: Boolean = false,
    var vehicleFindMeStatus: Boolean = false,
    var vehicleConnectionStatus: String = "",
)