package com.tvsm.connect.ui.data

data class TvsmLatestDeviceData(
    public val response: CodpResponse?,
    public val status: Int?,
    public val statusMessage: String?,
)
// change val to var to mock data for ut
data class CodpResponse(
    public val odometer: Double?,
    public val totalDistanceToday: Double?,
    public val totalTimeTakenToday: Double?,
    public val avgSpeedToday: Double?,
    public val heading: Double?,
    public var datetime_pkt: String?,
    public var soc: Int?,
    public val odometer_can: Double?,
    public var speed_can: Double?,
    public var charging_status: Int?,
    public var ignition_status: Int?,
    public val tyre_pressure: Tyre_pressure?,
    public val driving_mode: Double?,
    public var time_to_charge_completion: Int?,
    public val incognito_mode: String?,
    public val isNoGps: Boolean?,
    public val co2Saved: Double?,
    public val package_version: String?,
    public val ev_range: Ev_range?
)

public data class Tyre_pressure(
    public val front: String?,
    public val rear: String?,
)

public data class Ev_range(
    public val dte_eco: Double?,
    public val dte_power: Double?,
    public val dte_street: Double?,
)