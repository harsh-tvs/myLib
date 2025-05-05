package com.tvsm.iqubeindia.presentation.ui.utils

object WearableConstants {
    //Request URIs
    val AuthReqUri = "/req/Auth"
    val RemoteOpReqUri = "/req/RemoteOps"
    val LockVehicleReqUri = "/req/lockVehicle"
    val TrunkUnlockReqUri = "/req/trunkUnlock"
    val FindMeReqUri = "/req/findMe"

    //Request Keys
    val AuthReqKey = "authorizationRequired"
    val RemoteOpReqKey = "remoteOpsReq"
    val LockVehicleReqKey = "lockReq"
    val TrunkUnlockReqKey = "trunkUnlockReq"
    val FindMeReqKey = "findMeReq"

    //Response URIs
    val LoginStateResponseUri = "/loginState"
    val AuthResponseUri = "/auth"
    val LockVehicleResponseUri = "/lockVehicleStatus"
    val TrunkUnlockResponse = "/trunkUnlockStatus"
    val FindMeStatusUri = "/findMeStatus"
    val RemoteOpsStatusUri = "/remoteOpsStatus"

    //response keys
    val LoginStatusKey = "loginStatus"
    val RemoteOpsStatusKey = "enableRemoteOperation"
    val CommandReceivedKey = "commandReceived"
    val CommandExecutedKey = "commandExecuted"
    val LockStatusKey = "lockStatus"
    val PrePurchaseLoginStatusKey = "prePurchaseLoginStatus"
    val ErrorMessageKey = "errorMsg"

    //auth keys
    val CodpTokenKey = "codpToken"
    val UserNameKey = "userName"
    val VinKey = "vin"
    val VehicleModelKey = "vehicleModel"
    val VehicleColorKey = "color"
    val SubScriptionPlanKey = "subscriptionPlan"
    val SubscriptionExpireyKey = "subscriptionDate"
    val VehicleImageUrlKey="vehicleImageUrl"

    val WearPathPrefix = "wear://*"

    // Define constants for the weights
    const val WEIGHT_REMOTE_OPS = 0.73f
    const val WEIGHT_DEFAULT = 0.5f

    //vehicle name
    const val BIKE_U388 = "BIKE_U388"
    const val BIKE_IQube = "BIKE_IQube"
    const val NO_VEHICLE = "0"
    const val BIKE_U577_PREMIUM = "BIKE_U577_PREMIUM"

}