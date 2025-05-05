package com.tvsm.iqubeindia.domain.usecase


import com.tvsm.iqubeindia.domain.repository.CodpRepository
import com.tvsm.iqubeindia.tvsElectric.GetTvsmLatestDeviceDataQuery
import com.tvsm.iqubeindia.tvsElectric.TvsmDeviceLiveTrackingSubscription
import com.tvsm.iqubeindia.tvsIce.GetTvsmAggregateDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmLatestDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmLifetimeAggregateDeviceDataWatchQuery
import javax.inject.Inject


class GetCodpApiUseCase @Inject constructor(
    private val codpRepository: CodpRepository
) {

    suspend fun getTvsmLatestDeviceData(): Pair<GetTvsmLatestDeviceDataQuery.GetTvsmLatestDeviceData?, String?> =
        codpRepository.queryGetTvsmLatestDeviceData()

    suspend fun subscribeTvsmDeviceLiveTracking(): List<TvsmDeviceLiveTrackingSubscription.TvsmDeviceLiveTracking?>? =
        codpRepository.subscriptionTvsmDeviceLiveTracking()

    suspend fun fetchLatestTvsmDeviceDataWatch(): Pair<GetTvsmLatestDeviceDataWatchQuery.GetTvsmLatestDeviceDataWatch?, String?> =
        codpRepository.queryGetTvsmLatestDeviceDataWatch()

    suspend fun fetchAggregateTvsmDeviceDataWatch(): Pair<GetTvsmAggregateDeviceDataWatchQuery.GetTvsmAggregateDeviceDataWatch?, String?> =
        codpRepository.queryGetTvsmAggregateDeviceDataWatch()

    suspend fun fetchTvsmLifetimeAggregateDeviceDataWatch(): Pair<GetTvsmLifetimeAggregateDeviceDataWatchQuery.GetTvsmLifetimeAggregateDeviceDataWatch?, String?> =
        codpRepository.queryGetTvsmLifeTimeAggregateDeviceDataWatch()
}
