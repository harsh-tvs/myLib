/*
* Copyright 2021 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package com.tvsm.iqubeindia.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
//import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
//import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.tvsm.iqubeindia.domain.repository.PhoneConnectionCheckRepository
import com.tvsm.iqubeindia.domain.repository.VehicleDetailsRecordRepository
import com.tvsm.iqubeindia.presentation.theme.TVSElectricTheme
import com.tvsm.iqubeindia.presentation.ui.TVSElectricAppNavigator
import com.tvsm.iqubeindia.presentation.ui.utils.NetworkMonitor
import com.tvsm.iqubeindia.presentation.ui.utils.WearableConstants
import com.tvsm.iqubeindia.viewmodel.CodpViewModel
import com.tvsm.iqubeindia.viewmodel.WearDataLayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("WearDashboardActivity")
@AndroidEntryPoint
class WearDashboardActivity : ComponentActivity() {
    private val TAG = WearDashboardActivity::class.java.simpleName

    private var navHostController: NavHostController? = null

    private val codpViewModel: CodpViewModel by viewModels()
    private val wearDataLayerViewModel: WearDataLayerViewModel by viewModels()

    @Inject
    lateinit var vehicleDetailsRecordRepository: VehicleDetailsRecordRepository

    @Inject
    lateinit var networkMonitor: NetworkMonitor

    @Inject
    lateinit var phoneConnectionCheckRepository: PhoneConnectionCheckRepository

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity: onCreate")

        initProperties()
        initSharedPreferences()
        observeViewModel()
        setContent {
            navHostController = rememberSwipeDismissableNavController()
            TVSElectricTheme {
                navHostController?.let {
                    TVSElectricAppNavigator(
                        navController = it,
                        codpViewModel = codpViewModel,
                        wearDataViewModel = wearDataLayerViewModel,
                    )
                }
            }
        }
    }

    private fun initSharedPreferences() {
        if (vehicleDetailsRecordRepository.isCacheEmpty()) {
            Log.d(TAG, "cache is empty, initializing with defaults")
            val mCodpCredentials = vehicleDetailsRecordRepository.getCodpAccessCredentialsPref()
            vehicleDetailsRecordRepository.putCodpAccessCredsPref(mCodpCredentials)
        }
    }

    private fun initProperties() {
        Log.d(TAG, "MainActivity: initProperties()")
        wearDataLayerViewModel.registerWearableListeners()
    }

    private fun observeViewModel() = with(codpViewModel) {
        launchAndRepeatWithViewLifecycle {
            launch {
                if (wearDataLayerViewModel.getVehicleModel() == WearableConstants.BIKE_U388) {
                    networkMonitor.networkState.collect { codpViewModel.handleNetworkState(it) }
                }
            }

            launch {
                if (wearDataLayerViewModel.getVehicleModel() == WearableConstants.BIKE_U388 && !codpViewModel.isApiCallScheduled) {
                    codpViewModel.resetCancelFlag()
                    codpViewModel.startPeriodicApiCalls(10000)
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (codpViewModel.isApiCallScheduled && wearDataLayerViewModel.getVehicleModel() == WearableConstants.BIKE_U388) {
            codpViewModel.cancelApiCall()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        wearDataLayerViewModel.unRegisterWearableListeners()
    }

    companion object {
        internal const val CAPABILITY_PHONE_APP = "TvsElectricMobile"
    }
}

fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}

inline fun ComponentActivity.launchAndRepeatWithViewLifecycle(lifecycleState: Lifecycle.State = Lifecycle.State.STARTED, crossinline block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(lifecycleState) {
            block()
        }
    }
}
