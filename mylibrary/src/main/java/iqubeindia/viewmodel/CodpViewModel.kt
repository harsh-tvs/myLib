package com.tvsm.iqubeindia.viewmodel

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.tvsm.connect.R
import com.tvsm.connect.ui.data.CodpResponse
import com.tvsm.connect.ui.data.TvsmLatestDeviceData
import com.tvsm.iqubeindia.domain.usecase.GetCodpApiUseCase
import com.tvsm.iqubeindia.domain.repository.DispatchersProvider
import com.tvsm.iqubeindia.domain.repository.VehicleDetailsRecordRepository
import com.tvsm.iqubeindia.presentation.ui.CodpCallback
import com.tvsm.iqubeindia.presentation.ui.base.BaseViewModel
import com.tvsm.iqubeindia.presentation.ui.utils.Common
import com.tvsm.iqubeindia.presentation.ui.utils.Common.getSyncText
import com.tvsm.iqubeindia.presentation.ui.utils.Common.isDateTimePacketFresh
import com.tvsm.iqubeindia.presentation.ui.utils.NetworkMonitor
import com.tvsm.iqubeindia.presentation.ui.utils.OfflineState
import com.tvsm.iqubeindia.presentation.ui.utils.TvsElectricConstants
import com.tvsm.iqubeindia.tvsElectric.GetTvsmLatestDeviceDataQuery
import com.tvsm.iqubeindia.tvsElectric.TvsmDeviceLiveTrackingSubscription
import com.tvsm.iqubeindia.tvsIce.GetTvsmAggregateDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmLatestDeviceDataWatchQuery
import com.tvsm.iqubeindia.tvsIce.GetTvsmLifetimeAggregateDeviceDataWatchQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CodpViewModel @Inject constructor(
    private val getCodpApiUseCase: GetCodpApiUseCase,
    dispatchers: DispatchersProvider,
    val vehicleDetailsRecordRepository : VehicleDetailsRecordRepository
) : BaseViewModel(dispatchers) {

    private val TAG = CodpViewModel::class.java.simpleName

    //for api call after every 10 sec
    private val handler = Handler(Looper.getMainLooper())
    var isApiCallScheduled by mutableStateOf(false)
    var isApiCallCanceledOnce = false

    private val _codpTvsmLatestDeviceDataResponse = MutableStateFlow(
        GetTvsmLatestDeviceDataQuery
            .GetTvsmLatestDeviceData(null,null,null))
    val codpTvsmLatestDeviceDataResponse = _codpTvsmLatestDeviceDataResponse.asStateFlow()

    /*For junit test case running*/
    private val _codpTestTvsmLatestDeviceData = MutableStateFlow(TvsmLatestDeviceData(null,null,null))
    val codpTestTvsmLatestDeviceData = _codpTestTvsmLatestDeviceData.asStateFlow()

    private val _codpGetTvsmLatestDeviceDataWatchResponse = MutableStateFlow(
        GetTvsmLatestDeviceDataWatchQuery.GetTvsmLatestDeviceDataWatch(null,null,null)
    )
    val codpGetTvsmLatestDeviceDataWatchResponse =
        _codpGetTvsmLatestDeviceDataWatchResponse.asStateFlow()

    private val _codpGetTvsmAggregateDeviceDataWatchResponse = MutableStateFlow(
        GetTvsmAggregateDeviceDataWatchQuery.GetTvsmAggregateDeviceDataWatch(null,null,null)
    )
    val codpGetTvsmAggregateDeviceDataWatchResponse =
        _codpGetTvsmAggregateDeviceDataWatchResponse.asStateFlow()

    private val _codpGetLifeTimeTvsmAggregateDeviceDataWatchResponse = MutableStateFlow(
        GetTvsmLifetimeAggregateDeviceDataWatchQuery.GetTvsmLifetimeAggregateDeviceDataWatch(null,null,null)
    )
    val codpGetLifeTimeTvsmAggregateDeviceDataWatchResponse =
        _codpGetLifeTimeTvsmAggregateDeviceDataWatchResponse.asStateFlow()

    private val _offlineState = MutableStateFlow(OfflineState.Online)
    val offlineState = _offlineState.asStateFlow()

    private val _isInternet = MutableStateFlow(false)
    val isInternet=_isInternet

    private val _errorMsg= MutableStateFlow("")
    val errorMsg=_errorMsg

    private val _vehicleState = MutableStateFlow("")
    val vehicleState = _vehicleState.asStateFlow()

    private val _vehicleAlert = MutableStateFlow("")
    val vehicleAlert = _vehicleAlert.asStateFlow()

    private val _syncAlert = MutableStateFlow("")
    val syncAlert = _syncAlert.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _codpTvsmDeviceLiveTrackingResponse = MutableStateFlow(listOf<TvsmDeviceLiveTrackingSubscription.TvsmDeviceLiveTracking>())
    val codpTvsmDeviceLiveTrackingResponse = _codpTvsmDeviceLiveTrackingResponse.asStateFlow()

    //for U577

    private val _tripAduration = MutableStateFlow(0.0)
    val tripAduration = _tripAduration.asStateFlow()

    private val _tripBduration = MutableStateFlow(0.0)
    val tripBduration = _tripBduration.asStateFlow()

    private val _tripAdistance = MutableStateFlow(0.0)
    val tripAdistance = _tripAdistance.asStateFlow()

    private val _tripBdistance = MutableStateFlow(0.0)
    val tripBdistance = _tripBdistance.asStateFlow()

    private val _tripAavgSpeed = MutableStateFlow(0)
    val tripAavgSpeed = _tripAavgSpeed.asStateFlow()

    private val _tripBavgSpeed= MutableStateFlow(0)
    val tripBavgSpeed= _tripBavgSpeed.asStateFlow()


    private val _lastSync= MutableStateFlow("")
    val lastSync= _lastSync.asStateFlow()

    private val _colorState= MutableStateFlow(Color(0xFFFF0000))
    val  colorState=  _colorState.asStateFlow()

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun onInitialState(mTvsmLatestDeviceData: TvsmLatestDeviceData) = launchOnMainImmediate {
        _codpTestTvsmLatestDeviceData.value = mTvsmLatestDeviceData
    }

    fun getCodpTvsmLatestDeviceDataQuery(mCodpCallBack: CodpCallback?) {
        Log.d(
            TAG,
            "getCodpTvsmLatestDeviceDataQuery: ${vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin}"
        )
        launchOnIO {
            try {
                Log.d(TAG, "Attempting to fetch latest device data from API")
                _codpTvsmLatestDeviceDataResponse.value =
                    getCodpApiUseCase.getTvsmLatestDeviceData().first!!
                Log.d(
                    TAG,
                    "Latest device data fetched successfully: ${_codpTvsmLatestDeviceDataResponse.value.response.toString()}"
                )
                updateCurrentVehicleState(_codpTvsmLatestDeviceDataResponse.value.response)
                updateVehicleAlert(_codpTvsmLatestDeviceDataResponse.value.response)
            } catch (e: Exception) {
                val error = getCodpApiUseCase.getTvsmLatestDeviceData().second
                Log.e(TAG, "Error fetching latest device data: ${e.message} , $error")
                if (error != null) {
                    _errorMsg.value=error
                    updateCurrentVehicleState(null)
                }

                e.printStackTrace()
            } finally {
                withContext(Dispatchers.Main) {
                    if (_codpTvsmLatestDeviceDataResponse.value.response != null) {
                        vehicleDetailsRecordRepository.putVehicleLatestDataValuePref(
                            _codpTvsmLatestDeviceDataResponse.value.response!!
                        )
                        mCodpCallBack?.onSuccess()
                    } else {
                        mCodpCallBack?.onFailure(_codpTvsmLatestDeviceDataResponse.value.statusMessage)
                    }
                }
            }
        }
    }

    private fun updateCurrentVehicleState(tvsmLatestDeviceDataResponse: GetTvsmLatestDeviceDataQuery.Response?) {
        // Log.d(TAG, "In viewModel response: $tvsmLatestDeviceDataResponse datetime_pkt ${tvsmLatestDeviceDataResponse?.datetime_pkt}")
        //TODO Remove null case when error handling is done
        if (tvsmLatestDeviceDataResponse == null) {
            if ( _errorMsg.value==vehicleDetailsRecordRepository.getContext().getString(R.string.tag_network_errorMsg)) {
                updateOfflineState(OfflineState.NetworkError)
                _vehicleState.value = vehicleDetailsRecordRepository
                    .getContext()
                    .getString(R.string.tag_vehicle_status_offline)

                _vehicleAlert.value = vehicleDetailsRecordRepository
                    .getContext()
                    .getString(R.string.tag_subtext_error_no_network)
            }
            else {
                Log.d(TAG,"server error")
                handleServerError()
            }
        } else if (!isDateTimePacketFresh(tvsmLatestDeviceDataResponse.datetime_pkt.toString())) {
            updateOfflineState(OfflineState.VehicleOffline)
            _vehicleState.value = vehicleDetailsRecordRepository.getContext()
                .getString(R.string.tag_vehicle_status_vehicle_offline)

            _vehicleAlert.value = getSyncText(tvsmLatestDeviceDataResponse.datetime_pkt.toString())
        } else if (tvsmLatestDeviceDataResponse.charging_status == 1) {
            updateOfflineState(OfflineState.Online)
            if (tvsmLatestDeviceDataResponse.soc!! == TvsElectricConstants.FullChargeThreshold) {
                _vehicleState.value = vehicleDetailsRecordRepository.getContext()
                    .getString(R.string.tag_vehicle_status_charging_complete)
                _vehicleAlert.value = ""
            } else {
                _vehicleState.value = vehicleDetailsRecordRepository.getContext()
                    .getString(R.string.tag_vehicle_status_charging)
                val (hours, mins) = Common.convertMinutesToHoursAndMinutes(
                    tvsmLatestDeviceDataResponse.time_to_charge_completion!!
                )
                // Log the values of hours and mins
                Log.d(TAG, "hours & min  :$hours,$mins")

                Log.d(TAG,"time to complete  :$tvsmLatestDeviceDataResponse.time_to_charge_completion")
                if(mins <60 && hours==0) {
                    _vehicleAlert.value = String.format(
                        vehicleDetailsRecordRepository
                            .getContext()
                            .getString(R.string.tag_subtext_info_charging_time_in_min_remaining),
                        mins
                    )
                } else{
                    _vehicleAlert.value = String.format(
                        vehicleDetailsRecordRepository
                            .getContext()
                            .getString(R.string.tag_subtext_info_charging_time_remaining),
                        hours,
                        mins
                    )

                }
                Log.d(TAG, "vehicleAlertValue : ${_vehicleAlert.value}")

            }
        } else if (tvsmLatestDeviceDataResponse.ignition_status == 1) {
            updateOfflineState(OfflineState.Online)
            _vehicleAlert.value = ""
            if (tvsmLatestDeviceDataResponse.speed_can!! > 0) {
                _vehicleState.value = vehicleDetailsRecordRepository.getContext()
                    .getString(R.string.tag_vehicle_status_riding)
            } else {
                _vehicleState.value = vehicleDetailsRecordRepository.getContext()
                    .getString(R.string.tag_vehicle_status_unlocked)
                _vehicleAlert.value = getSyncText(tvsmLatestDeviceDataResponse.datetime_pkt.toString())
            }
        } else {
            updateOfflineState(OfflineState.Online)
            _vehicleAlert.value = getSyncText(tvsmLatestDeviceDataResponse.datetime_pkt.toString())
            _vehicleState.value = vehicleDetailsRecordRepository.getContext()
                .getString(R.string.tag_vehicle_status_locked)
        }
    }

    //for unit testing (method overloading)
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateCurrentVehicleState(tvsmLatestDeviceDataResponse: CodpResponse?, isUT: Boolean) {

        if (tvsmLatestDeviceDataResponse == null ) {
            if (offlineState.value == OfflineState.NetworkOffline) {
                _vehicleState.value = "Offline"
                _vehicleAlert.value = "No Network"
            } else {
                _vehicleState.value = "Refresh Failed!"
                _vehicleAlert.value = ""
            }
        } else if (!isDateTimePacketFresh(tvsmLatestDeviceDataResponse.datetime_pkt.toString())) {
            updateOfflineState(OfflineState.VehicleOffline)
            _vehicleState.value = "Vehicle Offline"
            _vehicleAlert.value = "2024-04-13 11:37:29"
        } else if (tvsmLatestDeviceDataResponse.charging_status == 1) {
            updateOfflineState(OfflineState.Online)
            if (tvsmLatestDeviceDataResponse.soc!! == 100) {
                _vehicleState.value = "Charging complete"
                _vehicleAlert.value = ""
            } else {
                _vehicleState.value = "Charging.."
                val (hours, mins) = Common.convertMinutesToHoursAndMinutes(
                    tvsmLatestDeviceDataResponse.time_to_charge_completion!!
                )
                if(mins <60 && hours==0) {
                    _vehicleAlert.value = String.format(
                       "%dmins remaining",
                        mins
                    )
                } else{
                    _vehicleAlert.value = String.format(
                        "%dhrs %dmins remaining",
                        hours,
                        mins
                    )

                }
                Log.d(TAG, "vehicleAlertValue : ${_vehicleAlert.value}")
            }
        } else if (tvsmLatestDeviceDataResponse.ignition_status == 1) {
            updateOfflineState(OfflineState.Online)
            _vehicleAlert.value = ""
            if (tvsmLatestDeviceDataResponse.speed_can!! > 0) {
                _vehicleState.value = "On Ride!"
            } else {
                _vehicleState.value = "Unlocked!"
            }
        } else {
            updateOfflineState(OfflineState.Online)
            _vehicleAlert.value = ""
            _vehicleState.value = "Locked!"
        }
    }

    private fun updateVehicleAlert(tvsmLatestDeviceDataResponse: GetTvsmLatestDeviceDataQuery.Response?) {
        if (tvsmLatestDeviceDataResponse?.soc!! < TvsElectricConstants.LowChargeThreshold && tvsmLatestDeviceDataResponse.charging_status != 1) {
            _vehicleAlert.value = vehicleDetailsRecordRepository.getContext()
                .getString(R.string.tag_subtext_info_low_battery)
        }
    }
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) // For unit testing (method overloading)
    fun updateVehicleAlert(tvsmLatestDeviceDataResponse: CodpResponse?) {
        if (tvsmLatestDeviceDataResponse?.soc!! < TvsElectricConstants.LowChargeThreshold && tvsmLatestDeviceDataResponse.charging_status != 1) {
            _vehicleAlert.value = "Battery Running Low"
        }
    }

    fun getLastSyncedVehicleData(): GetTvsmLatestDeviceDataQuery.Response? {
        return vehicleDetailsRecordRepository.getVehicleLatestDataValuePref()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun updateOfflineState(offlineState: OfflineState) {
        _offlineState.value = offlineState
    }

    fun updateNetworkAvailabilityOnWatch(networkState: Boolean) {
        Log.d(TAG, "Updated Network State : $networkState")

        if (!networkState) {
            updateOfflineState(OfflineState.NetworkOffline)
            updateCurrentVehicleState(null)
        } else {
            updateOfflineState(OfflineState.Online)
            _vehicleState.value = ""
            _vehicleAlert.value = "Refreshing.."
            getCodpTvsmLatestDeviceDataQuery(null)
        }
    }

    fun subscribeTvsmDeviceLiveTrackingSubscription(mCodpCallBack: CodpCallback?) {
        launchOnIO {
            try {
                getCodpApiUseCase.subscribeTvsmDeviceLiveTracking()?.let {
                    _codpTvsmDeviceLiveTrackingResponse.value =
                        it as List<TvsmDeviceLiveTrackingSubscription.TvsmDeviceLiveTracking>
                    Log.d(TAG, "subscribeTvsmDeviceLiveTrackingSubscription ${_codpTvsmDeviceLiveTrackingResponse.value}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "subscribeTvsmDeviceLiveTrackingSubscription ERROR: ${e.message}")
                e.printStackTrace()
            } finally {
                withContext(Dispatchers.Main) {
                    if (_codpTvsmDeviceLiveTrackingResponse.value != null) {
                        mCodpCallBack?.onSuccess()
                    } else {
                        mCodpCallBack?.onFailure(_codpTvsmDeviceLiveTrackingResponse.value[0].speed.toString())
                    }
                }
            }
        }
    }

    fun handleNetworkState(state: Pair<NetworkMonitor.NetworkState, Boolean>) {
        Log.d(TAG, "state is $state")
        if (!state.first.isAvailable()) {
            updateOfflineState(OfflineState.NetworkError)
            _vehicleState.value = vehicleDetailsRecordRepository
                .getContext()
                .getString(R.string.tag_vehicle_status_offline)
            _vehicleAlert.value = vehicleDetailsRecordRepository
                .getContext()
                .getString(R.string.tag_subtext_error_no_network)

        } else {
            if (!state.second) {
                updateOfflineState(OfflineState.NetworkError)
                _vehicleState.value = vehicleDetailsRecordRepository
                    .getContext()
                    .getString(R.string.tag_vehicle_status_offline)
                _vehicleAlert.value = vehicleDetailsRecordRepository
                    .getContext()
                    .getString(R.string.tag_subtext_error_no_network)
            } else {
                _isInternet.value=state.second
                updateOfflineState(OfflineState.Online)
                _vehicleState.value = ""
                _vehicleAlert.value = "Refreshing.."
                getCodpTvsmLatestDeviceDataQuery(null)
            }

        }

    }
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) // for unit test case (method overriding)
    fun testhandleNetworkState(state: Pair<NetworkMonitor.NetworkState, Boolean>) {
        Log.d(TAG, "state is $state")
        if (!state.first.isAvailable()) {
            updateOfflineState(OfflineState.NetworkError)
            _vehicleState.value = "Offline"
            _vehicleAlert.value = "No Network"
        } else {
            if (!state.second) {
                updateOfflineState(OfflineState.NetworkError)
                _vehicleState.value = "Offline"
                _vehicleAlert.value = "No Network"
            } else {
                _isInternet.value=state.second
                updateOfflineState(OfflineState.Online)
                _vehicleState.value = ""
                _vehicleAlert.value = "Refreshing.."
            }
        }
    }

    private fun handleServerError() {
        if (_isInternet.value==true) {
            updateOfflineState(OfflineState.ServerError)
            _vehicleState.value = vehicleDetailsRecordRepository
                .getContext()
                .getString(R.string.tag_vehicle_status_offline)
            _vehicleAlert.value = vehicleDetailsRecordRepository
                .getContext()
                .getString(R.string.tag_server_error)
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) // for unit test case (method overriding)
    fun testForHandleServerError() {
        if (isInternet.value==true) {
            updateOfflineState(OfflineState.ServerError)
            _vehicleState.value = "Offline"
            _vehicleAlert.value = "Server Error.Try Later"
        }
    }
    // Function to simulate a refresh operation
    suspend fun refreshData() {
        // Set isRefreshing to true to indicate that refresh operation is in progress
        _isRefreshing.value = true
        updateOfflineState(OfflineState.Online)
        _vehicleState.value = ""
        _vehicleAlert.value = "Refreshing.."
        delay(1000)
        getCodpTvsmLatestDeviceDataQuery(null)
        // After the delay, set isRefreshing to false to indicate that the refresh operation is completed
        _isRefreshing.value = false
    }

    // Function to call API after a delay of 10 sec
    fun startPeriodicApiCalls(
        intervalMillis: Long
    ) {
        Log.d(TAG,"isApiCallScheduled is $isApiCallScheduled")
        if (isApiCallScheduled) return
        val runnable = object : Runnable {
            override fun run() {
                val currentTimeStamp = formatTimeInMillisToSeconds(System.currentTimeMillis())
                Log.d(TAG, "API call initiated at timestamp: $currentTimeStamp")
                getCodpTvsmLatestDeviceDataQuery(null) // Execute API call
                Log.d(TAG,"api call done")
                val nextTimeStamp = formatTimeInMillisToSeconds(System.currentTimeMillis()) + intervalMillis
                Log.d(TAG, "Next API call scheduled at timestamp: $nextTimeStamp")
                handler.postDelayed(this, intervalMillis)
            }
        }
        handler.postDelayed(runnable,intervalMillis)
        isApiCallScheduled = true
    }

    fun cancelApiCall() {
        if(!isApiCallCanceledOnce) {
            handler.removeCallbacksAndMessages(null)
            isApiCallScheduled = false
            isApiCallCanceledOnce = true
            Log.d(TAG, "API CALL IS CANCEL")
        }
    }
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) // for unit test case (method overriding)
    fun testcancelApiCall() {
        if(!isApiCallCanceledOnce) {
            handler.removeCallbacksAndMessages(null)
            isApiCallScheduled = false
            isApiCallCanceledOnce = true
            Log.d(TAG, "API CALL IS CANCEL")
        }
    }

    fun resetCancelFlag() {
        isApiCallCanceledOnce = false
    }
// for checking how API calls are working
    private fun formatTimeInMillisToSeconds(millis: Long): String {
        val seconds = millis / 1000
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = seconds * 1000
        return sdf.format(calendar.time)
    }


    fun getTvsmLatestDeviceDataWatchQuery(mCodpCallBack: CodpCallback?) {
        Log.d(TAG, "Fetching latest device data for VIN: ${vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin}")

        launchOnIO {
            try {
                val response = getCodpApiUseCase.fetchLatestTvsmDeviceDataWatch()
                response.first?.let {
                    _codpGetTvsmLatestDeviceDataWatchResponse.value = it
                    updateLatestDeviceDataWatch(it.data)
                    Log.d(TAG, "Latest device data: ${it.data}")
                    withContext(Dispatchers.Main) { mCodpCallBack?.onSuccess() }
                } ?: handleFailure(mCodpCallBack, "No data received from API")
            } catch (e: Exception) {
                handleApiError(e, mCodpCallBack)
            } finally {
                saveVehicleData(mCodpCallBack)
            }
        }
    }

    fun getTvsmAggregateDeviceDataWatchQuery(mCodpCallBack: CodpCallback?) {
        Log.d(TAG, "Fetching aggregate device data for VIN: ${vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin}")

        launchOnIO {
            try {
                val response = getCodpApiUseCase.fetchAggregateTvsmDeviceDataWatch()
                response.first?.let {
                    _codpGetTvsmAggregateDeviceDataWatchResponse.value = it
                    Log.d(TAG, "Aggregate device data: ${it.data}")
                    withContext(Dispatchers.Main) { mCodpCallBack?.onSuccess() }
                } ?: handleFailure(mCodpCallBack, "No aggregate data received from API")
            } catch (e: Exception) {
                handleApiError(e, mCodpCallBack)
            } finally {
                saveVehicleData(mCodpCallBack)
            }
        }
    }

    fun getLifeTimeTvsmAggregateDeviceDataWatchQuery(mCodpCallBack: CodpCallback?) {
        Log.d(TAG, "Fetching lifetime aggregate device data for VIN: ${vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vin}")

        launchOnIO {
            try {
                val response = getCodpApiUseCase.fetchTvsmLifetimeAggregateDeviceDataWatch()
                response.first?.let {
                    _codpGetLifeTimeTvsmAggregateDeviceDataWatchResponse.value = it
                    Log.d(TAG, "Lifetime aggregate device data: ${it.data}")
                    withContext(Dispatchers.Main) { mCodpCallBack?.onSuccess() }
                } ?: handleFailure(mCodpCallBack, "No lifetime aggregate data received from API")
            } catch (e: Exception) {
                handleApiError(e, mCodpCallBack)
            } finally {
                saveVehicleData(mCodpCallBack)
            }
        }
    }

    private suspend fun handleFailure(mCodpCallBack: CodpCallback?, message: String) {
        Log.e(TAG, message)
        withContext(Dispatchers.Main) { mCodpCallBack?.onFailure(message) }
    }

    private fun handleApiError(e: Exception, mCodpCallBack: CodpCallback?) {
        launchOnIO {
            val error = runCatching { getCodpApiUseCase.fetchLatestTvsmDeviceDataWatch().second }.getOrNull()
            Log.e(TAG, "API error: ${e.message}, $error")
            _errorMsg.value = error ?: "Unknown error"
            withContext(Dispatchers.Main) { mCodpCallBack?.onFailure(_errorMsg.value) }
        }
    }

    private  suspend fun saveVehicleData(mCodpCallBack: CodpCallback?) {
        withContext(Dispatchers.Main) {
            val latestData = _codpGetTvsmLatestDeviceDataWatchResponse.value?.data
            val aggregateData = _codpGetTvsmAggregateDeviceDataWatchResponse.value?.data
            val lifeTimeData = _codpGetLifeTimeTvsmAggregateDeviceDataWatchResponse.value?.data

            if (latestData != null && aggregateData != null && lifeTimeData != null) {
                Log.d(TAG, "Saving vehicle data successfully")
                vehicleDetailsRecordRepository.putVehicleLatestDataValuePrefU577(latestData, aggregateData, lifeTimeData)
                mCodpCallBack?.onSuccess()
            } else {
                handleFailure(mCodpCallBack, "Incomplete data")
            }
        }
    }

    private fun updateLatestDeviceDataWatch(response: GetTvsmLatestDeviceDataWatchQuery.Data1?) {
        if (response != null) {
            Log.d(TAG, "Latest device data: $response")
            val lastSync = response.last_sync.toString()
            _lastSync.value = lastSync
            updateColor(lastSync)
            _syncAlert.value = getSyncText(lastSync)
        } else {
            _syncAlert.value = vehicleDetailsRecordRepository
                .getContext()
                .getString(R.string.tag_server_error)
            Log.e(TAG, "Null API response received in updateLatestDeviceDataWatch. Cannot update odometer reading.")
        }
    }


    fun updateColor(lastSync: String) {
        _colorState.value = if (isDateTimePacketFresh(lastSync)) Color(0xFF00FF1A) else Color(0xFFFF0000)
    }

    fun resetTripValues() {
        _tripAdistance.value = 0.0
        _tripAavgSpeed.value = 0
        _tripAduration.value = 0.0
        _tripBdistance.value = 0.0
        _tripBavgSpeed.value = 0
        _tripBduration.value = 0.0
    }

}