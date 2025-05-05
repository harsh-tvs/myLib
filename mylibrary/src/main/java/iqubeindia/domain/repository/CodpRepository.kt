package com.tvsm.iqubeindia.domain.repository


import com.tvsm.iqubeindia.tvsElectric.GetTvsmLatestDeviceDataQuery
import com.tvsm.iqubeindia.tvsElectric.TvsmDeviceLiveTrackingSubscription
import com.tvsm.iqubeindia.tvsIce.GetTvsmAggregateDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmLatestDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmLifetimeAggregateDeviceDataWatchQuery

interface CodpRepository {

    suspend fun queryGetTvsmLatestDeviceData():  Pair<GetTvsmLatestDeviceDataQuery.GetTvsmLatestDeviceData?, String?>
    suspend fun subscriptionTvsmDeviceLiveTracking(): List<TvsmDeviceLiveTrackingSubscription.TvsmDeviceLiveTracking?>?
    suspend fun queryGetTvsmLatestDeviceDataWatch():  Pair<GetTvsmLatestDeviceDataWatchQuery.GetTvsmLatestDeviceDataWatch?, String?>
    suspend fun queryGetTvsmAggregateDeviceDataWatch():  Pair<GetTvsmAggregateDeviceDataWatchQuery.GetTvsmAggregateDeviceDataWatch?, String?>
    suspend fun queryGetTvsmLifeTimeAggregateDeviceDataWatch():  Pair<GetTvsmLifetimeAggregateDeviceDataWatchQuery.GetTvsmLifetimeAggregateDeviceDataWatch?, String?>



}