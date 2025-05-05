package com.tvsm.iqubeindia.presentation.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import com.tvsm.connect.R
import com.tvsm.iqubeindia.presentation.ui.utils.AntiClockwiseCircularProgressIndicator
import com.tvsm.iqubeindia.presentation.ui.utils.ErrorCode
import com.tvsm.iqubeindia.presentation.ui.utils.RemoteOpState
import com.tvsm.iqubeindia.presentation.ui.utils.RemoteRequests
import com.tvsm.iqubeindia.presentation.ui.utils.WearableConstants
import com.tvsm.iqubeindia.viewmodel.WearDataLayerViewModel
import com.tvsm.iqubeindia.viewmodel.CodpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.math.RoundingMode
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.roundToInt

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun AnimRemoteOperationsScreen(
    codpViewModel: CodpViewModel,
    wearDataLayerViewModel: WearDataLayerViewModel,
    navController: NavHostController,
    animOffsetRemoteOps: Animatable<Float, AnimationVector1D>,
    currentSize: Dp
) {
   /*
      AnimRemoteOperationScreen is invoked from vehicleDetailsHome Screen on the basis of flag that's why
      here we are cancelling the api call when AnimRemoteOperationScreen is invoked and
      when it goes off again resuming the periodic api call
    */
    LaunchedEffect(Unit) {
        codpViewModel.cancelApiCall()
    }
    DisposableEffect(Unit){
        onDispose {
            codpViewModel.resetCancelFlag()
            codpViewModel.startPeriodicApiCalls(10000)
        }
    }
    val TAG = "AnimRemoteOperationsScreen"

    val vehicleState by codpViewModel.vehicleState.collectAsState()
    val isRemoteOpsEnabled by wearDataLayerViewModel.isRemoteOpsEnabled.collectAsState()

    var trunkOpenState by remember { mutableStateOf(TrunkOpenState.TrunkOpenDefault) }
    var trunkOpenProgressValue  by remember { mutableStateOf(0f) }
    var lockUnlockState by remember { mutableStateOf(LockUnlockState.LockUnlockDefault) }
    var lockUnlockProgressValue  by remember { mutableStateOf(0f) }
    var findMeState by remember { mutableStateOf(FindMeState.FindMeDefault) }
    var findMeProgressValue  by remember { mutableStateOf(0f) }

    var showVehicleNotConnectedMobileDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isScrollUp by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val mutex = Mutex()
    val vehicleModel = wearDataLayerViewModel.getVehicleModel()

    Log.d(TAG, "LaunchedEffect isRemoteOpsEnabled: $isRemoteOpsEnabled ; trunkOpenState: $trunkOpenState" +
            " lockUnlockState: $lockUnlockState ; findMeState: $findMeState")
    /*To check the RemoteOperation is enabled or not from SP and isRemoteOpsEnabled flag*/
    if (!isRemoteOpsEnabled || !wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().isRemoteOpEnabled) {
        Log.d(TAG, "LaunchedEffect ALL DISABLED")
        trunkOpenState = TrunkOpenState.TrunkOpenDisabled
        lockUnlockState = LockUnlockState.LockUnlockDisabled
        findMeState = FindMeState.FindMeDisabled
    } else {
        if (trunkOpenState == TrunkOpenState.TrunkOpenDisabled || lockUnlockState == LockUnlockState.LockUnlockDisabled ||
            findMeState == FindMeState.FindMeDisabled) {
            trunkOpenState = TrunkOpenState.TrunkOpenDefault
            lockUnlockState = LockUnlockState.LockUnlockDefault
            findMeState = FindMeState.FindMeDefault
        }
    }

    /*Read the existing vehicleBootStatus store on SP */
    Log.d(TAG, "LaunchedEffect from SP isRemoteOpsEnabled: ${wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().isRemoteOpEnabled} ; " +
            " vehicleBootStatus: ${wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleBootStatus} ; " +
            " vehicleLockStatus: ${wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleLockStatus} ; " +
            " vehicleFindMeStatus: ${wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleFindMeStatus}")
    if (trunkOpenState == TrunkOpenState.TrunkOpenDefault || trunkOpenState == TrunkOpenState.TrunkOpenActive) {
        if (wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleBootStatus) {
            trunkOpenState = TrunkOpenState.TrunkOpenActive
        } else {
            trunkOpenState = TrunkOpenState.TrunkOpenDefault
        }
    }
    /*Read the existing vehicleLockStatus store on SP */
    if (lockUnlockState == LockUnlockState.LockUnlockDefault || lockUnlockState == LockUnlockState.LockUnlockActive) {
        if (wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleLockStatus) {
            lockUnlockState = LockUnlockState.LockUnlockDefault
        } else {
            lockUnlockState = LockUnlockState.LockUnlockActive
        }
    }
    /*Read the existing vehicleLockStatus store on SP */
    if (findMeState == FindMeState.FindMeDefault || findMeState == FindMeState.FindMeActive) {
        if (wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleFindMeStatus) {
            findMeState = FindMeState.FindMeActive
        }
    }

    /*To show the Ripple effect on TrunkOpenBtn*/
    if (trunkOpenState == TrunkOpenState.TrunkOpenClicked) {
        coroutineScope.launch {
            delay(100L)
            trunkOpenState = TrunkOpenState.TrunkOpenProgressStarted
        }
    }

    /*To show the Ripple effect on LockUnlockBtn*/
    if (lockUnlockState == LockUnlockState.LockUnlockClicked) {
        coroutineScope.launch {
            delay(100L)
            lockUnlockState = LockUnlockState.LockUnlockProgressStarted
        }
    }

    /*To show the Ripple effect on FineMeBtn*/
    if (findMeState == FindMeState.FindMeClicked) {
        coroutineScope.launch {
            delay(100L)
            findMeState = FindMeState.FindMeProgressStarted
        }
    }

    Column(modifier = Modifier
        .offset {
            IntOffset(0, animOffsetRemoteOps.value.roundToInt())
        }
        .size(height = currentSize, width = LocalConfiguration.current.screenWidthDp.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
       Text(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Min),
            text = stringResource(id = R.string.tag_vehicle_controls),
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.weight(0.2f))
        Row(modifier = Modifier
            .fillMaxSize()
            .weight(1f)
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            Button(onClick = {
                    Log.d(TAG, "onClick trunkOpenState: $trunkOpenState")
                    if (trunkOpenState == TrunkOpenState.TrunkOpenDefault) {
                        wearDataLayerViewModel.sendRemoteOpsRequest(RemoteRequests.TrunkUnlock)
                        trunkOpenState = TrunkOpenState.TrunkOpenClicked
                        doVibrate(context)
                    } else if (trunkOpenState == TrunkOpenState.TrunkOpenDisabled) {
                        showVehicleNotConnectedMobileDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .weight(1f)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
            ) {
                var ic_alpha = 1.0f
                val icTrunkOpen = when (trunkOpenState) {
                    TrunkOpenState.TrunkOpenSuccess, TrunkOpenState.TrunkOpenActive -> {
                        ic_alpha = 1.0f
                        painterResource(id = R.drawable.ic_remote_trunk_open_active)
                    }

                    TrunkOpenState.TrunkOpenDisabled -> {
                        ic_alpha = 0.3f
                        if (wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleBootStatus) {
                            painterResource(id = R.drawable.ic_remote_trunk_open_active)
                        } else {
                            if(vehicleModel == WearableConstants.BIKE_U388){
                                painterResource(id = R.drawable.ic_remote_trunk_open)

                            }
                            else{
                                painterResource(id = R.drawable.ic_show_path)
                            }
                        }
                    }

                    else -> {
                        ic_alpha = 1.0f
                        painterResource(id = R.drawable.ic_remote_trunk_open)
                    }
                }
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color = getBtnRippleColor(trunkOpenState == TrunkOpenState.TrunkOpenClicked),
                            shape = CircleShape
                        )
                        .border(
                            0.5.dp,
                            color = getBorderSelectedColor(trunkOpenState),
                            CircleShape
                        ),
                    painter = icTrunkOpen,
                    alpha = ic_alpha,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
                if (trunkOpenState == TrunkOpenState.TrunkOpenProgressStarted) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val progressValue by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 6000))
                    )
                    if (progressValue > trunkOpenProgressValue) {
                        trunkOpenProgressValue = progressValue
                    }
                    CircularProgressIndicator(
                        progress = progressValue,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 1.dp,
                        indicatorColor = colorResource(id = R.color.colorRemoteButtonProgressBar)
                    )
                }
                if (trunkOpenState == TrunkOpenState.TrunkOpenSuccess ||
                    trunkOpenState == TrunkOpenState.TrunkOpenError
                ) {
                    val roundedUp =
                        trunkOpenProgressValue.toBigDecimal().setScale(1, RoundingMode.UP).toFloat()
                    CircularProgressIndicator(
                        progress = roundedUp,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 1.dp,
                        indicatorColor = colorResource(id = R.color.colorRemoteButtonProgressBar)
                    )
                    val infiniteTransition = rememberInfiniteTransition()
                    val progressComplete by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1000))
                    )
                    AntiClockwiseCircularProgressIndicator(
                        progress = progressComplete,
                        modifier = Modifier.fillMaxSize(0.98f),
                        color = colorResource(id = R.color.colorRemoteButtonProgressBar),
                        strokeWidth = 1.dp,
                    )
                    coroutineScope.launch {
                        delay(800L)
                        Log.d(
                            TAG,
                            "vehicleBootStatus from SP: " + wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleBootStatus
                        )
                        if (wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleBootStatus)
                            trunkOpenState = TrunkOpenState.TrunkOpenActive
                        else trunkOpenState = TrunkOpenState.TrunkOpenDefault
                    }
                }
            }
            Spacer(modifier = Modifier.weight(0.2f))
            Button(onClick = {
                    Log.d(TAG, "onClick lockUnlockState: $lockUnlockState")
                    if (lockUnlockState == LockUnlockState.LockUnlockDefault) {
                        wearDataLayerViewModel.sendRemoteOpsRequest(
                            RemoteRequests.LockUnlock,
                            false
                        )
                        lockUnlockState = LockUnlockState.LockUnlockClicked
                        doVibrate(context)
                    } else if (lockUnlockState == LockUnlockState.LockUnlockActive) {
                        wearDataLayerViewModel.sendRemoteOpsRequest(RemoteRequests.LockUnlock, true)
                        lockUnlockState = LockUnlockState.LockUnlockClicked
                        doVibrate(context)
                    } else if (trunkOpenState == TrunkOpenState.TrunkOpenDisabled) {
                        showVehicleNotConnectedMobileDialog = true
                    }
                    //lockUnlockState = LockUnlockState.LockUnlockClicked
                    //doVibrate(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .weight(1f)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                var ic_alpha = 1.0f
                val icLockUnlock = when (lockUnlockState) {
                    LockUnlockState.LockUnlockSuccess, LockUnlockState.LockUnlockActive -> {
                        ic_alpha = 1.0f
                        painterResource(id = R.drawable.ic_remote_lock_unlock_active)
                    }

                    LockUnlockState.LockUnlockDisabled -> {
                        ic_alpha = 0.3f
//                        if (wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleLockStatus) {
//                            painterResource(id = R.drawable.ic_remote_lock_unlock)
//                        } else {
//                            painterResource(id = R.drawable.ic_remote_lock_unlock_active)
//                        }
                        painterResource(id = R.drawable.ic_remote_lock_unlock)
                    }

                    else -> {
                        ic_alpha = 1.0f
                        painterResource(id = R.drawable.ic_remote_lock_unlock)
                    }
                }
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color = getBtnRippleColor(lockUnlockState == LockUnlockState.LockUnlockClicked),
                            shape = CircleShape
                        )
                        .border(
                            0.5.dp,
                            color = getBorderSelectedColor(lockUnlockState),
                            CircleShape
                        ),
                    alpha = ic_alpha,
                    painter = icLockUnlock,
                    contentDescription = ""
                )
                if (lockUnlockState == LockUnlockState.LockUnlockProgressStarted) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val progressValue by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 6000))
                    )
                    if (progressValue > lockUnlockProgressValue) {
                        lockUnlockProgressValue = progressValue
                    }
                    CircularProgressIndicator(
                        progress = progressValue,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 1.dp,
                        indicatorColor = colorResource(id = R.color.colorRemoteButtonProgressBar)
                    )
                }
                if (lockUnlockState == LockUnlockState.LockUnlockSuccess ||
                    lockUnlockState == LockUnlockState.LockUnlockError
                ) {
                    val roundedUp =
                        lockUnlockProgressValue.toBigDecimal().setScale(1, RoundingMode.UP)
                            .toFloat()
                    CircularProgressIndicator(
                        progress = roundedUp,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 1.dp,
                        indicatorColor = colorResource(id = R.color.colorRemoteButtonProgressBar)
                    )
                    val infiniteTransition = rememberInfiniteTransition()
                    val progressComplete by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1000))
                    )
                    AntiClockwiseCircularProgressIndicator(
                        progress = progressComplete,
                        modifier = Modifier.fillMaxSize(0.98f),
                        color = colorResource(id = R.color.colorRemoteButtonProgressBar),
                        strokeWidth = 1.dp,
                    )
                    coroutineScope.launch {
                        delay(800L)
                        Log.d(
                            TAG,
                            "vehicleLockStatus from SP: " + wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleLockStatus
                        )
                        if (wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleLockStatus)
                            lockUnlockState = LockUnlockState.LockUnlockActive
                        else lockUnlockState = LockUnlockState.LockUnlockDefault
                    }
                }
            }
            Spacer(modifier = Modifier.weight(0.2f))
            Button(onClick = {
                    Log.d(TAG, "onClick findMeState: $findMeState")
                    if (findMeState == FindMeState.FindMeDefault) {
                        wearDataLayerViewModel.sendRemoteOpsRequest(RemoteRequests.FindMe)
                        findMeState = FindMeState.FindMeClicked
                        doVibrate(context)
                        Timer().schedule(10000) {
                            findMeState = FindMeState.FindMeDefault
                        }
                    } else if (trunkOpenState == TrunkOpenState.TrunkOpenDisabled) {
                        showVehicleNotConnectedMobileDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .weight(1f)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                var ic_alpha = 1.0f
                val icFindMe = when (findMeState) {
                    FindMeState.FindMeSuccess, FindMeState.FindMeActive -> {
                        ic_alpha = 1.0f
                        painterResource(id = R.drawable.ic_remote_find_me_active)
                    }

                    FindMeState.FindMeDisabled -> {
                        /*TODO set the disabled FindMe icon*/
                        ic_alpha = 0.3f
                        painterResource(id = R.drawable.ic_remote_find_me)
                    }

                    else -> {
                        ic_alpha = 1.0f
                        painterResource(id = R.drawable.ic_remote_find_me)
                    }
                }
                Image(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            color = getBtnRippleColor(findMeState == FindMeState.FindMeClicked),
                            shape = CircleShape
                        )
                        .border(
                            0.5.dp,
                            color = getBorderSelectedColor(findMeState),
                            CircleShape
                        ),
                    alpha = ic_alpha,
                    painter = icFindMe,
                    contentDescription = ""
                )
                if (findMeState == FindMeState.FindMeProgressStarted) {
                    val infiniteTransition = rememberInfiniteTransition()
                    val progressValue by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 6000))
                    )
                    if (progressValue > lockUnlockProgressValue) {
                        findMeProgressValue = progressValue
                    }
                    CircularProgressIndicator(
                        progress = progressValue,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 1.dp,
                        indicatorColor = colorResource(id = R.color.colorRemoteButtonProgressBar)
                    )
                }
                if (findMeState == FindMeState.FindMeSuccess ||
                    findMeState == FindMeState.FindMeError
                ) {
                    val roundedUp =
                        findMeProgressValue.toBigDecimal().setScale(1, RoundingMode.UP).toFloat()
                    CircularProgressIndicator(
                        progress = roundedUp,
                        modifier = Modifier.fillMaxSize(),
                        strokeWidth = 1.dp,
                        indicatorColor = colorResource(id = R.color.colorRemoteButtonProgressBar)
                    )
                    val infiniteTransition = rememberInfiniteTransition()
                    val progressComplete by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 500))
                    )
                    AntiClockwiseCircularProgressIndicator(
                        progress = progressComplete,
                        modifier = Modifier.fillMaxSize(0.98f),
                        color = colorResource(id = R.color.colorRemoteButtonProgressBar),
                        strokeWidth = 1.dp,
                    )
                    LaunchedEffect(findMeState) {
                        coroutineScope.launch {
                            delay(10000L)
                            Log.d(
                                TAG,
                                "vehicleFindMeStatus from SP: ${wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleFindMeStatus}"
                            )

                            findMeState = if (wearDataLayerViewModel.vehicleDetailsRecordRepository.getRemoteOperationStatusPref().vehicleFindMeStatus) {
                                Log.d(TAG, "active state")
                                FindMeState.FindMeActive
                            } else {
                                Log.d(TAG, "non-active state")
                                FindMeState.FindMeDefault
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.weight(0.2f))
        }
    }


    if (showVehicleNotConnectedMobileDialog) {
        GenericDialogScreen(
            mErrorCode = ErrorCode.VEHICLE_NOT_CONNECTED_TO_MOBILE_VIA_BLE,
            onPositiveClick = { showVehicleNotConnectedMobileDialog = false }) {
        }
    }

    LaunchedEffect(true) {
        wearDataLayerViewModel.vehicleLockOpStatus.collect {
            when (it) {
                RemoteOpState.CommandInit -> {
                }
                RemoteOpState.CommandSent -> {
                }
                RemoteOpState.CommandExecuted -> {
                    Log.d(TAG, "vehicleLockOpStatus -> CommandExecuted ")
                    lockUnlockState = LockUnlockState.LockUnlockSuccess
                }
                RemoteOpState.CommandError -> {
                    Log.d(TAG, "vehicleLockOpStatus -> CommandError ")
                    lockUnlockState = LockUnlockState.LockUnlockError
                }
                else -> {}
            }
        }
    }
    LaunchedEffect(true) {
        wearDataLayerViewModel.trunkUnlockOpStatus.collect {
            when (it) {
                RemoteOpState.CommandInit -> {
                }
                RemoteOpState.CommandSent -> {
                }
                RemoteOpState.CommandExecuted -> {
                    Log.d(TAG, "trunkUnlockOpStatus -> CommandExecuted ")
                    trunkOpenState = TrunkOpenState.TrunkOpenSuccess
                }
                RemoteOpState.CommandError -> {
                    Log.d(TAG, "trunkUnlockOpStatus -> CommandError ")
                    trunkOpenState = TrunkOpenState.TrunkOpenError
                }
                else -> {}
            }
        }
    }
    LaunchedEffect(true) {
        wearDataLayerViewModel.findMeOpStatus.collect {
            when (it) {
                RemoteOpState.CommandInit -> {
                }
                RemoteOpState.CommandSent -> {
                }
                RemoteOpState.CommandExecuted -> {
                    Log.d(TAG, "findMeOpStatus -> CommandExecuted ")
                    findMeState = FindMeState.FindMeSuccess
                }
                RemoteOpState.CommandError -> {
                    Log.d(TAG, "findMeOpStatus -> CommandError ")
                    findMeState = FindMeState.FindMeError
                }
                else -> {}
            }
        }
    }
}
/*
fun doVibrate(context: Context) {
    // Vibrate the smartwatch
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}

@Composable
fun getBtnRippleColor(isBtnClicked: Boolean): Color {
    return if (!isBtnClicked) Color.Transparent
    else colorResource(id = R.color.colorRemoteButtonSelected)
}

@Composable
fun getBorderSelectedColor(isProgressStarted: Boolean, isButtonClicked: Boolean): Color {
    return if (!isProgressStarted and !isButtonClicked) colorResource(id = R.color.colorRemoteButtonBorder)
    else if (isButtonClicked and !isProgressStarted) colorResource(id = R.color.colorRemoteButtonDisabledBorder)
    else Color.Transparent
}
@Composable
fun getBorderSelectedColor(trunkOpenState: TrunkOpenState): Color {
    return when (trunkOpenState) {
        TrunkOpenState.TrunkOpenClicked, TrunkOpenState.TrunkOpenProgressStarted, TrunkOpenState.TrunkOpenSuccess,
        TrunkOpenState.TrunkOpenError -> Color.Transparent
        TrunkOpenState.TrunkOpenDisabled -> colorResource(id = R.color.colorRemoteButtonDisabledBorder)
        else -> colorResource(id = R.color.colorRemoteButtonBorder)
    }
}

@Composable
fun getBorderSelectedColor(lockUnlockState : LockUnlockState): Color {
    return when (lockUnlockState) {
        LockUnlockState.LockUnlockClicked, LockUnlockState.LockUnlockProgressStarted, LockUnlockState.LockUnlockSuccess,
        LockUnlockState.LockUnlockError -> Color.Transparent
        LockUnlockState.LockUnlockDisabled -> colorResource(id = R.color.colorRemoteButtonDisabledBorder)
        else -> colorResource(id = R.color.colorRemoteButtonBorder)
    }
}

@Composable
fun getBorderSelectedColor(findMeState : FindMeState): Color {
    return when (findMeState) {
        FindMeState.FindMeClicked, FindMeState.FindMeProgressStarted, FindMeState.FindMeSuccess,
        FindMeState.FindMeError -> Color.Transparent
        FindMeState.FindMeDisabled -> colorResource(id = R.color.colorRemoteButtonDisabledBorder)
        else -> colorResource(id = R.color.colorRemoteButtonBorder)
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun RemoteOperationsScreenPreview() {
    //RemoteOperationsScreen(null, null, null, {})
}

enum class TrunkOpenState {
    TrunkOpenDefault,
    TrunkOpenDisabled,
    TrunkOpenClicked,
    TrunkOpenProgressStarted,
    TrunkOpenSuccess,
    TrunkOpenError,
    TrunkOpenActive
}

enum class LockUnlockState {
    LockUnlockDefault,
    LockUnlockDisabled,
    LockUnlockClicked,
    LockUnlockProgressStarted,
    LockUnlockSuccess,
    LockUnlockError,
    LockUnlockActive
}

enum class FindMeState {
    FindMeDefault,
    FindMeDisabled,
    FindMeClicked,
    FindMeProgressStarted,
    FindMeSuccess,
    FindMeError,
    FindMeActive
}*/
