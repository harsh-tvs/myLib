package com.tvsm.iqubeindia.viewmodel

import android.app.Activity
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.tvsm.iqubeindia.domain.entities.PreConditionCheckResult
import com.tvsm.iqubeindia.domain.repository.DispatchersProvider
import com.tvsm.iqubeindia.domain.repository.VehicleDetailsRecordRepository
import com.tvsm.iqubeindia.domain.repository.WearableDataLayerResponse
import com.tvsm.iqubeindia.domain.usecase.PhoneConnectionCheckUseCase
import com.tvsm.iqubeindia.domain.usecase.WearableUseCase
import com.tvsm.iqubeindia.presentation.ui.base.BaseViewModel
import com.tvsm.iqubeindia.presentation.ui.data.CodpAccessCredentials
import com.tvsm.iqubeindia.presentation.ui.utils.LoginState
import com.tvsm.iqubeindia.presentation.ui.utils.RemoteOpState
import com.tvsm.iqubeindia.presentation.ui.utils.RemoteRequests
import com.tvsm.iqubeindia.presentation.ui.utils.WearableConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WearDataLayerViewModel @Inject constructor(
    dispatchers: DispatchersProvider,
    private val phoneConnectionCheck: PhoneConnectionCheckUseCase,
    private val wearableUseCase: WearableUseCase,
    val vehicleDetailsRecordRepository: VehicleDetailsRecordRepository,
) : BaseViewModel(dispatchers) {

    private val TAG = WearDataLayerViewModel::class.java.simpleName

    private val _loginState = MutableStateFlow(LoginState.IdleState)
    val loginState = _loginState.asStateFlow()
    private val _preConditionCheckForPhoneComm = MutableStateFlow(PreConditionCheckResult.PreconditionFailed)
    val preConditionCheckForPhoneComm = _preConditionCheckForPhoneComm.asStateFlow()

    private val _vehicleLockOpStatus = MutableSharedFlow<RemoteOpState>()
    val vehicleLockOpStatus = _vehicleLockOpStatus.asSharedFlow()
    private val _trunkUnlockOpStatus = MutableSharedFlow<RemoteOpState>()
    val trunkUnlockOpStatus = _trunkUnlockOpStatus.asSharedFlow()
    private val _findMeOpStatus = MutableSharedFlow<RemoteOpState>()
    val findMeOpStatus = _findMeOpStatus.asSharedFlow()
    private val _isRemoteOpsEnabled = MutableStateFlow(true)
    val isRemoteOpsEnabled = _isRemoteOpsEnabled.asStateFlow()

    fun registerWearableListeners(){
        wearableUseCase.registerWearableListeners()
        wearableUseCase.subScribeWearableDataLayerResponse(wearableDataLayerResponse)
    }

    val wearableDataLayerResponse = object : WearableDataLayerResponse {
        override fun onLoginStateUpdated(loginState: Boolean) {
            if (loginState)
                changeLoginStateForValidTokenFromMobile()
            else
                changeLoginStateForEmptyTokenFromMobile()
        }

        override fun onVehicleLockOpStatusUpdated(vehicleLockOpStatus: RemoteOpState) {
            launchOnIO {
                _vehicleLockOpStatus.emit(vehicleLockOpStatus)
            }
        }

        override fun onTrunkUnlockOpStatusUpdated(trunkUnlockOpStatus: RemoteOpState) {
            launchOnIO {
                _trunkUnlockOpStatus.emit(trunkUnlockOpStatus)
            }
        }

        override fun onFindMeOpStatusUpdated(findMeOpStatus: RemoteOpState) {
            launchOnIO {
                _findMeOpStatus.emit(findMeOpStatus)
            }
        }

        override fun onIsRemoteOpsEnabledUpdated(isRemoteOpsEnabled: Boolean) {
            launchOnIO {
                _isRemoteOpsEnabled.emit(isRemoteOpsEnabled)
            }
        }
    }

    fun unRegisterWearableListeners(){
        wearableUseCase.unregisterWearableListeners()
        wearableUseCase.subScribeWearableDataLayerResponse(null)
    }

    private fun changeLoginStateForValidTokenFromMobile() {
        when(_loginState.value){
            LoginState.IdleState -> { /*DO NOTHING*/ }
            LoginState.LoggingIn -> {setLoginState(LoginState.LoggedIn)}
            LoginState.LoggedIn -> { /*DO NOTHING*/ }
            LoginState.LoggedOut -> { /*DO NOTHING*/ }
            LoginState.VehicleInCompatible -> { /*DO NOTHING*/ }
            LoginState.NoVehicleFound ->{/*DO NOTHING*/}
            else -> { /*DO NOTHING*/ }
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) // For unit testing (method overloading)
    fun testForChangeLoginStateForValidTokenFromMobile() {
        when(loginState.value){
            LoginState.IdleState -> { /*DO NOTHING*/ }
            LoginState.LoggingIn -> {setLoginState(LoginState.LoggedIn)}
            LoginState.LoggedIn -> { /*DO NOTHING*/ }
            LoginState.LoggedOut -> { /*DO NOTHING*/ }
            LoginState.VehicleInCompatible -> { /*DO NOTHING*/ }
            LoginState.NoVehicleFound ->{/*DO NOTHING*/}
            else -> { /*DO NOTHING*/ }
        }
    }

    private fun changeLoginStateForEmptyTokenFromMobile() {
        Log.d(TAG, "changeLoginStateForEmptyTokenFromMobile")
        vehicleDetailsRecordRepository.putCodpAccessCredsPref(CodpAccessCredentials())
        when(_loginState.value){
            LoginState.IdleState -> { /*DO NOTHING*/ }
            LoginState.LoggingIn -> {setLoginState(LoginState.LoginError)}
            LoginState.LoggedIn -> {setLoginState(LoginState.LoggedOut)}
            LoginState.LoggedOut -> { /*DO NOTHING*/ }
            LoginState.VehicleInCompatible ->{setLoginState(LoginState.LoggedOut) }
            LoginState.NoVehicleFound ->{setLoginState(LoginState.LoggedOut) }
            else -> { /*DO NOTHING*/ }
        }
    }
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE) // For unit testing (method overloading)
    fun testForChangeLoginStateForEmptyTokenFromMobile() {
        Log.d(TAG, "changeLoginStateForEmptyTokenFromMobile")
        vehicleDetailsRecordRepository.putCodpAccessCredsPref(CodpAccessCredentials())
        when(loginState.value){
            LoginState.IdleState -> { /*DO NOTHING*/ }
            LoginState.LoggingIn -> {setLoginState(LoginState.LoginError)}
            LoginState.LoggedIn -> {setLoginState(LoginState.LoggedOut)}
            LoginState.LoggedOut -> { /*DO NOTHING*/ }
            LoginState.VehicleInCompatible ->{setLoginState(LoginState.LoggedOut) }
            LoginState.NoVehicleFound ->{setLoginState(LoginState.LoggedOut) }
            else -> { /*DO NOTHING*/ }
        }
    }

    fun setLoginState(loginState: LoginState){
        Log.d(TAG, "setting login state $loginState")
        _loginState.value = loginState
    }
    // check vechicle compatbility for wearOS
    fun checkVehicleCompatibilityAndUpateLoginState(vehicleDetailsRecordRepository: VehicleDetailsRecordRepository): LoginState {
        val bikeType = vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vehicleModel
        return when (bikeType) {
            WearableConstants.BIKE_U388 -> {
                Log.d(TAG, "Bike: $bikeType")
                LoginState.LoggedIn // U388
            }
            WearableConstants.BIKE_U577_PREMIUM -> {
                Log.d(TAG, "Bike: $bikeType")
                LoginState.LoggedIn // U577
            }
            WearableConstants.NO_VEHICLE -> {
                Log.d(TAG, "No vehicle")
                LoginState.NoVehicleFound // No vehicle
            }
            else -> {
                Log.d(TAG, "Bike: $bikeType")
                LoginState.VehicleInCompatible  // Other bikes
            }
        }
    }

    fun getVehicleModel(): String {
        val vehicleModel = vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vehicleModel
        return vehicleModel
    }


    fun doSignInPreConditionCheck() {
        launchOnIO {
            delay(500)

            val loginState = if (wearableUseCase.getLoginStateFromMobileApp() || wearableUseCase.getPrepPurchaseLoginStateFromMobileApp())
                {
                    /*Unwanted check for verifying the codpToken value*/
                    //if(vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().codpToken.isEmpty()) {
                    wearableUseCase.getAuthCredentialsFromPhone()
                    wearableUseCase.getAuthCredentialsFromPhone()
                    //}

                    val compatibility = checkVehicleCompatibilityAndUpateLoginState(vehicleDetailsRecordRepository)
                    compatibility
                } else {
                    Log.d(TAG, "check pre")
                    _preConditionCheckForPhoneComm.emit(phoneConnectionCheck.combinedPreConditionCheck())
                    return@launchOnIO
                }
            _loginState.emit(loginState)
        }
    }


    fun sendRemoteOpsRequest(remoteOpRequest: RemoteRequests, remoteOp: Boolean = true) {
        launchOnIO {
            wearableUseCase.sendRemoteOpsRequest(remoteOpRequest, remoteOp)
        }
    }

    fun openAppInStoreOnPhone(activity: Activity) {
        launchOnMainImmediate {
            phoneConnectionCheck.openAppInStoreOnPhone(activity)
        }
    }

    fun getRemoteOpsStatusFromDataLayer() {
        launchOnIO {
            wearableUseCase.getRemoteOpsStatusFromDataLayer()
        }
    }
}