package com.tvsm.iqubeindia.presentation.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.SwipeToDismissBox
import androidx.wear.compose.foundation.SwipeToDismissValue
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.foundation.rememberSwipeToDismissBoxState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.tvsm.iqubeindia.presentation.getActivity
//import com.google.accompanist.navigation.animation.AnimatedNavHost
//import com.google.accompanist.navigation.animation.composable
import com.tvsm.iqubeindia.domain.entities.PreConditionCheckResult
import com.tvsm.iqubeindia.presentation.ui.navigation.Destination
import com.tvsm.iqubeindia.presentation.ui.screens.*
import com.tvsm.iqubeindia.presentation.ui.utils.*
import com.tvsm.iqubeindia.viewmodel.WearDataLayerViewModel
import com.tvsm.iqubeindia.viewmodel.CodpViewModel
import kotlinx.coroutines.*


val offsetTweenSpec = tween<IntOffset>(durationMillis = 1000, easing = FastOutSlowInEasing)

@ExperimentalAnimationApi
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun TVSElectricAppNavigator(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    codpViewModel : CodpViewModel,
    wearDataViewModel: WearDataLayerViewModel
) {
    val TAG = "TVSElectricAppNavigator"
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val loginState by wearDataViewModel.loginState.collectAsState()
    val preConditionCheckForPhoneComm by wearDataViewModel.preConditionCheckForPhoneComm.collectAsState()
    var showDialog by remember { mutableStateOf(DisplayDialogSelector.NoDialog) }
    val mContext = LocalContext.current

    Scaffold(
        modifier = modifier,
        timeText = null,
        vignette = {
            if (currentBackStackEntry?.destination?.route == Destination.LandingDashboard.route) {
                Vignette(vignettePosition = VignettePosition.TopAndBottom)
            }
        }
    ) {
        val swipeToDismissBoxState = rememberSwipeToDismissBoxState()

        //NavHost(navController = , startDestination = , builder = )
        SwipeDismissableNavHost(navController = navController,
            startDestination = Destination.Splash.route) {

            composable(route = Destination.Splash.route) {
                SplashScreen()
                LaunchedEffect(Unit) {
                    Log.d(TAG, "Destination.Splash")
                    delay(950)
                    //wearDataViewModel.setLoginState(LoginState.LoggingIn)
                    navController.navigate(route = Destination.AnimateSplash.route)
                }
            }
            composable(route = Destination.AnimateSplash.route) {
                AnimateSplashScreen()
                LaunchedEffect(Unit) {
                    Log.d(TAG, "Destination.AnimateSplash")
                    delay(950)
                    wearDataViewModel.setLoginState(LoginState.LoggingIn)
//                    navController.navigate(route = Destination.CodpLoading.route) {
//                        popUpTo(Destination.Splash.route) { inclusive = true } // Clean backstack
//                    }
                }
            }

            composable(route = Destination.CodpLoading.route) {
                Log.d(TAG, "Destination.CodpLoading")
                val mCodpCallBack = object : CodpCallback {
                    override fun onSuccess() {
                        Log.i(TAG, "CODP Callback Success")
                        navController.navigate(route = Destination.LandingDashboard.route)
                    }

                    override fun onFailure(msg: String?) {
                        Log.e(TAG, "GetTvsmLatestDeviceDataQuery onFailure: $msg")
                        navController.navigate(route = Destination.LandingDashboard.route)
                    }
                }
                val vehicleModel = wearDataViewModel.getVehicleModel()
                when (vehicleModel) {
                    WearableConstants.BIKE_U388 -> {
                        codpViewModel.getCodpTvsmLatestDeviceDataQuery(mCodpCallBack)

                    }
                    WearableConstants.BIKE_U577_PREMIUM -> {
                        codpViewModel.getTvsmAggregateDeviceDataWatchQuery(mCodpCallBack)
                        codpViewModel.getTvsmLatestDeviceDataWatchQuery(mCodpCallBack)
                        codpViewModel.getLifeTimeTvsmAggregateDeviceDataWatchQuery(mCodpCallBack)
                    }
                }
                CodpLoadingScreen()
            }

            composable(route = Destination.LoginFailed.route) {
                Log.d(TAG, "Destination.LoginFailed")
                GenericDialogScreen(
                    mErrorCode = ErrorCode.WATCH_NOT_CONNECTED_TO_MOBILE_USER_NOT_SIGNED,
                    onPositiveClick = {
                        wearDataViewModel.setLoginState(LoginState.LoggingIn)
                    },
                    onNegativeClick = { mContext.getActivity()?.finish() }) {
                }
            }

            composable(route = Destination.SignInProgress.route) {
                Log.d(TAG, "Destination.SignInProgress")
                SigningInScreen()
                wearDataViewModel.doSignInPreConditionCheck()
            }

            composable(route = Destination.LandingDashboard.route) {
                Log.d(TAG, "Destination.LandingDashboard")
                SwipeToDismissBox(state = swipeToDismissBoxState) { bg ->
                    if (!bg )
                        LandingDashboardScreen(swipeToDismissBoxState, codpViewModel, wearDataViewModel, navController)
                }
            }

            /*Removed the separate compose screen design for Remote Operation*/
            /*composable(route = Destination.RemoteOperation.route,
                enterTransition = {
                        slideInVertically(initialOffsetY = { 1000 }, animationSpec = offsetTweenSpec)
                    },
                exitTransition = {
                        slideOutVertically(targetOffsetY = { 1000 }, animationSpec = offsetTweenSpec)
                    },
                popEnterTransition = {
                        slideInVertically(initialOffsetY = { -1000 }, animationSpec = offsetTweenSpec)
                    },
                popExitTransition = {
                        slideOutVertically(targetOffsetY = { -1000 }, animationSpec = offsetTweenSpec)
                    }) {
                        Log.d(TAG, "Destination.RemoteOperation")
                        wearDataViewModel.getRemoteOpsStatusFromDataLayer()
                        RemoteOperationsScreen(codpViewModel, wearDataViewModel, navController,
                        onDismissed = { navController.navigate(route = Destination.LandingDashboard.route) })
                }*/
        }

        LaunchedEffect(swipeToDismissBoxState.currentValue) {
            if (swipeToDismissBoxState.currentValue == SwipeToDismissValue.Dismissed) {
                Log.d(TAG, "Destination.LandingDashboard SwipeToDismissValue.Dismissed")
                mContext.getActivity()?.finish()
            }
        }
    }

    when (preConditionCheckForPhoneComm) {
        PreConditionCheckResult.PreConditionOk -> {
            Log.d(TAG, "app installed")
            if(loginState == LoginState.LoggingIn){
                wearDataViewModel.setLoginState(LoginState.LoginError)
            }
            //wearDataLayerViewModel.sendRemoteOpsRequest(RemoteRequests.RequestAuthData)
        }
        PreConditionCheckResult.AppNotInstalled -> {
            Log.d(TAG, "App not installed")
            showDialog = DisplayDialogSelector.AppNotInstalled
        }
        PreConditionCheckResult.PhoneNotConnected -> {
            Log.d(TAG, "Phone Not connected")
            showDialog = DisplayDialogSelector.MobileNotConnected
        }
        else -> {
            Log.d(TAG, "waiting fro results! do nothing")
            //DO Nothing
        }
    }

    //TODO refine this condition: when phone connected and app not
    // installed for a few milliseconds connect to phone dialog comes up
    when (showDialog) {
        DisplayDialogSelector.AppNotInstalled -> {
            GenericDialogScreen(mErrorCode = ErrorCode.WATCH_CONNECTED_TO_MOBILE_APP_NOT_INSTALLED,
                onPositiveClick = {
                    wearDataViewModel.openAppInStoreOnPhone(mContext.getActivity() as Activity)
                },
                onNegativeClick = {
                    mContext.getActivity()?.finish()
                }) {
            }
        }
        DisplayDialogSelector.MobileNotConnected -> {
            GenericDialogScreen(mErrorCode = ErrorCode.WATCH_NOT_CONNECTED_TO_MOBILE,
                onPositiveClick = {
                    mContext.getActivity()?.finish()
                }) {
            }
        }
        DisplayDialogSelector.UserAccessRevoked -> {
            GenericDialogScreen(mErrorCode = ErrorCode.USER_ACCESS_REVOKED,
                onPositiveClick = {
                    wearDataViewModel.setLoginState(LoginState.LoggingIn)
                },
                onNegativeClick = { mContext.getActivity()?.finish() }) {

            }
        }
        DisplayDialogSelector.VehicleIsNotCompatible -> {
            GenericDialogScreen(mErrorCode = ErrorCode.VEHICLE_NOT_COMPATIBLE,
                onPositiveClick = {
                    mContext.getActivity()?.finish()
                }) {

            }
        }
        DisplayDialogSelector.NoVehicleFound -> {
            GenericDialogScreen(mErrorCode = ErrorCode.NO_VEHICLE_FOUND,
                onPositiveClick = {
                    mContext.getActivity()?.finish()
                }) {
            }
        }

        else -> {
            //DO NOTHING
        }
    }

    when (loginState) {
        LoginState.LoggedOut -> {
            showDialog = DisplayDialogSelector.UserAccessRevoked
        }
        LoginState.LoggingIn -> {
            if (navController.currentDestination?.route != Destination.SignInProgress.route) {
                navController.navigate(route = Destination.SignInProgress.route)
            }
        }
        LoginState.LoggedIn -> {
            navController.navigate(route = Destination.CodpLoading.route)
        }
        LoginState.LoginError -> {
            navController.navigate(route = Destination.LoginFailed.route)
        }
        LoginState.VehicleInCompatible -> {
            showDialog = DisplayDialogSelector.VehicleIsNotCompatible

        }
        LoginState.NoVehicleFound -> {
            showDialog = DisplayDialogSelector.NoVehicleFound
        }

        else -> {}
    }
}
interface CodpCallback {
    fun onSuccess()
    fun onFailure(msg: String?)
}