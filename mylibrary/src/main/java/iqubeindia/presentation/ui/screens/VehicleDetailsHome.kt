package com.tvsm.iqubeindia.presentation.ui.screens

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.tvsm.connect.R
import com.tvsm.iqubeindia.presentation.ui.navigation.Destination
import com.tvsm.iqubeindia.presentation.ui.utils.GradientLinearProgressbar
import com.tvsm.iqubeindia.presentation.ui.utils.OfflineState
import com.tvsm.iqubeindia.presentation.ui.utils.RemoteRequests
import com.tvsm.iqubeindia.presentation.ui.utils.TvsElectricConstants
import com.tvsm.iqubeindia.presentation.ui.utils.TvsElectricConstants.OfflineStateOpacity
import com.tvsm.iqubeindia.presentation.ui.utils.WearableConstants
import com.tvsm.iqubeindia.tvsElectric.GetTvsmLatestDeviceDataQuery
import com.tvsm.iqubeindia.viewmodel.CodpViewModel
import com.tvsm.iqubeindia.viewmodel.WearDataLayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.roundToInt

val Int.nonScaledSp
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp
@Composable
fun VehicleDetailsHome(
    codpViewModel: CodpViewModel,
    wearDataViewModel: WearDataLayerViewModel,
    navController: NavHostController?,
    isDragged: Boolean,
    onIsRemoteOpsParamChange: (Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        codpViewModel.resetCancelFlag()
        Log.d("vv","i m launched")
        codpViewModel.startPeriodicApiCalls(10000)
    }
    DisposableEffect(Unit) {
        onDispose {
           codpViewModel.cancelApiCall()
        }
    }

    val TAG = "VehicleDetailsHome"
    val state by codpViewModel.codpTvsmLatestDeviceDataResponse.collectAsState()
    val codpResponse = if (state.response == null) codpViewModel.vehicleDetailsRecordRepository.getVehicleLatestDataValuePref()
    else state.response

    val offlineState by codpViewModel?.offlineState!!.collectAsState()
    val vehicleState by codpViewModel.vehicleState.collectAsState()
    val vehicleAlert by codpViewModel.vehicleAlert.collectAsState()
    var isScrollDown by remember { mutableStateOf(false) }
    var isScrollUp by remember { mutableStateOf(false) }
    var isRemoteOpsShowing by remember { mutableStateOf(false) }
    var isVehicleInfoShowing by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()
    val mutex = Mutex()

    val slideInAnimDuration = 500
    val slideOutAnimDuration = 350
    var offsetYValRemoteOps = -60f
    val animOffsetVehicleState = remember { Animatable(0f) }
    val animOffsetRemoteOps = remember { Animatable(0f) }

    val screenSizeType = getScreenSizeType(LocalContext.current.resources.displayMetrics, LocalDensity.current.density)

    Box {

        val scrollableState = rememberScrollableState { deltaY ->
            // Check the sign of deltaY to determine scroll direction
            if (deltaY < 0) {
                Log.d(TAG,"onScrollDown")
                if (!isScrollDown and !isRemoteOpsShowing) {
                    coroutineScope.launch {
                        mutex.withLock {
                            isScrollDown = true
                            isRemoteOpsShowing = true
                            onIsRemoteOpsParamChange(true)
                            isVehicleInfoShowing = false
                        }
                    }
                }
            } else if (deltaY > 0) {
                Log.d(TAG, "onScrollUp")
                if (!isScrollUp and isRemoteOpsShowing) {
                    coroutineScope.launch {
                        mutex.withLock {
                            isScrollUp = true
                            isRemoteOpsShowing = false
                            onIsRemoteOpsParamChange(false)
                            delay(slideOutAnimDuration.toLong())
                            isVehicleInfoShowing = true
                        }
                    }
                }
            }
            deltaY
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .scrollable(scrollableState, Orientation.Vertical)
                .background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier
                .height(IntrinsicSize.Min)
                .width(IntrinsicSize.Max)
                .offset {
                    IntOffset(0, animOffsetVehicleState.value.roundToInt())
                }
                .weight(1.2f),
                horizontalAlignment= Alignment.CenterHorizontally,
                verticalArrangement= Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .height(IntrinsicSize.Min)
                        .width(IntrinsicSize.Max)
                        .weight(if (isRemoteOpsShowing) WearableConstants.WEIGHT_REMOTE_OPS else WearableConstants.WEIGHT_DEFAULT),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_tvs_logo_header),
                        contentDescription = "",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Column(modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .width(IntrinsicSize.Max)
                    .weight(0.7f),
                    horizontalAlignment= Alignment.CenterHorizontally,
                    verticalArrangement= Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .height(IntrinsicSize.Min)
                            .clickable(enabled = (offlineState == OfflineState.VehicleOffline
                                    || offlineState == OfflineState.NetworkOffline),
                                onClick = {
                                    GetTvsmLatestDeviceDataQuery
                                        .GetTvsmLatestDeviceData(null, null, null)
                                }),
                        text = vehicleState,
                        color = Color.White,
                        style = MaterialTheme.typography.title3,
                    )
                    if(offlineState != OfflineState.Online
                        && !isRemoteOpsShowing && isVehicleInfoShowing) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .weight(1.2f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_offline_refresh),
                                contentDescription = "",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier//.fillMaxSize()
                                    .weight(0.2F)
                                    .clickable(onClick = {
                                        Log.d(TAG,"clicked")
                                        if(codpViewModel.isApiCallScheduled){
                                            codpViewModel.cancelApiCall()
                                            coroutineScope.launch{codpViewModel.refreshData()}
                                        }
                                        codpViewModel.resetCancelFlag()
                                        codpViewModel.startPeriodicApiCalls(10000)
                                    }
                                    ),
                            )
                            Text(
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .height(IntrinsicSize.Min)
                                    .weight(1F)
                                    .padding(start = 4.dp)
                                    .clickable(onClick = {
                                        GetTvsmLatestDeviceDataQuery
                                            .GetTvsmLatestDeviceData(null, null, null)
                                    }
                                    ),
                                text = vehicleAlert,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                textAlign = TextAlign.Center,
                            )
                            Log.d(TAG, "Vehicle Alert: $vehicleAlert")
                        }
                    }
                    else if (!isRemoteOpsShowing && isVehicleInfoShowing){
                        Text(
                            modifier = Modifier
                                .width(IntrinsicSize.Max)
                                .height(IntrinsicSize.Min)
                                .weight(1F),
                            text = vehicleAlert,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            textAlign = TextAlign.Center,
                        )
                    } else {
                        Spacer(modifier = Modifier.fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .weight(1.2f))
                    }
                }
            }
            if (!isRemoteOpsShowing and isVehicleInfoShowing) {
                Spacer(modifier = Modifier.weight(0.3f))
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 1.dp, bottom = 1.dp)
                    .weight(1.1f),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(0.5f))
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .weight(1f),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            val socProgress = codpResponse?.soc?.toFloat() ?: 90f
                            val chargingStatus = codpResponse?.charging_status?.equals(1) ?: false
                            if (socProgress > TvsElectricConstants.LowChargeThreshold) {
                                GradientLinearProgressbar(
                                    isCharging = chargingStatus,
                                    progressPercentage = socProgress,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 4.dp, end = 2.dp)
                                        .height(5.dp),
                                    gradientColors = listOf(
                                        colorResource(id = R.color.socNormalGradientColor1).copy(alpha = 0.5f),
                                        colorResource(id = R.color.socNormalGradientColor2).copy(alpha = 0.5f)
                                    )
                                )
                            } else {
                                GradientLinearProgressbar(
                                    isCharging = chargingStatus,
                                    progressPercentage = socProgress,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp),
                                    gradientColors = listOf(
                                        colorResource(id = R.color.socCriticalGradientColor1),
                                        colorResource(id = R.color.socCriticalGradientColor2)
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.35f))
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .weight(1.2f)
                            .padding(bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .width(IntrinsicSize.Min)
                                    .weight(1.1f)
                                    .height(IntrinsicSize.Min),
                                text = stringResource(id = R.string.tag_stl),
                                color = if (offlineState == OfflineState.Online)
                                    colorResource(id = R.color.textColorEcoMode)
                                else
                                    colorResource(id = R.color.textColorEcoMode).copy(alpha = OfflineStateOpacity),
                                textAlign = TextAlign.Start,
                                fontSize = 12.nonScaledSp
                            )
                            Text(
                                modifier = Modifier
                                    .width(IntrinsicSize.Min)
                                    .weight(1f)
                                    .height(IntrinsicSize.Min),
                                text = String.format("%.0f", codpResponse?.ev_range?.Xtl),
                                color = if (offlineState == OfflineState.Online)
                                    Color.White
                                else
                                    Color.White.copy(alpha = OfflineStateOpacity),
                                textAlign = TextAlign.End,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.weight(0.2f))
                            Text(
                                modifier = Modifier
                                    .width(IntrinsicSize.Min)
                                    .weight(1f)
                                    .height(IntrinsicSize.Min),
                                text = stringResource(id = R.string.tag_km),
                                color = if (offlineState == OfflineState.Online)
                                    Color.White
                                else
                                    Color.White.copy(alpha = OfflineStateOpacity),
                                textAlign = TextAlign.Start,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.weight(0.3f))
                    }
                    Row(modifier = Modifier
                        .fillMaxSize()
                        .weight(2f),
                        verticalAlignment = Alignment.Top
                    ) {
                        Spacer(modifier = Modifier.weight(0.5f))
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .height(IntrinsicSize.Min),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                Text(modifier = Modifier
                                    .width(IntrinsicSize.Min)
                                    .height(IntrinsicSize.Min),
                                    text = codpResponse?.soc?.toString() ?: "90",
                                    color = if (offlineState == OfflineState.Online)
                                        getSocValueColor(codpResponse)
                                    else
                                        getSocValueColor(codpResponse).copy(alpha = OfflineStateOpacity),
                                    textAlign = TextAlign.Start,
                                    fontSize = 36.nonScaledSp
                                )
                            }
                            Box(modifier = Modifier
                                .width(IntrinsicSize.Min)
                                .height(IntrinsicSize.Min),
                                contentAlignment = Alignment.BottomStart,
                            ) {
                                Text(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .height(IntrinsicSize.Min)
                                        .padding(start = 2.dp),
                                    text = "%",
                                    color = if (offlineState == OfflineState.Online)
                                        Color.White
                                    else
                                        Color.White.copy(alpha = OfflineStateOpacity),
                                    textAlign = TextAlign.Start,
                                    fontSize = 20.nonScaledSp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.35f))
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .weight(1.2f),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Spacer(modifier = Modifier.weight(0.2f))
                            Row(modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .weight(1.1f)
                                        .height(IntrinsicSize.Min),
                                    text = stringResource(id = R.string.tag_str),
                                    color = if (offlineState == OfflineState.Online)
                                        colorResource(id = R.color.textColorStrMode)
                                    else
                                        colorResource(id = R.color.textColorStrMode).copy(alpha = OfflineStateOpacity),
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.nonScaledSp
                                )
                                Text(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .weight(1f)
                                        .height(IntrinsicSize.Min),
                                    text = String.format("%.0f", codpResponse?.ev_range?.Xtr),
                                    color = if (offlineState == OfflineState.Online)
                                        Color.White
                                    else
                                        Color.White.copy(alpha = OfflineStateOpacity),
                                    textAlign = TextAlign.End,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.weight(0.2f))
                                Text(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .weight(1f)
                                        .height(IntrinsicSize.Min),
                                    text = stringResource(id = R.string.tag_km),
                                    color = if (offlineState == OfflineState.Online)
                                        Color.White
                                    else
                                        Color.White.copy(alpha = OfflineStateOpacity),
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.weight(0.5f))
                            Row(modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 1.dp)
                                .weight(1f)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .weight(1.1f)
                                        .height(IntrinsicSize.Min),
                                    text = stringResource(id = R.string.tag_snc),
                                    color = if (offlineState == OfflineState.Online)
                                        colorResource(id = R.color.textColorPwrMode)
                                    else
                                        colorResource(id = R.color.textColorPwrMode).copy(alpha = OfflineStateOpacity),
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.nonScaledSp
                                )
                                Text(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .weight(0.9f)
                                        .height(IntrinsicSize.Min),
                                    text = String.format("%.0f", codpResponse?.ev_range?.Xnc),
                                    color = if (offlineState == OfflineState.Online)
                                        Color.White
                                    else
                                        Color.White.copy(alpha = OfflineStateOpacity),
                                    textAlign = TextAlign.End,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.weight(0.1f))
                                Text(
                                    modifier = Modifier
                                        .width(IntrinsicSize.Min)
                                        .weight(0.9f)
                                        .height(IntrinsicSize.Min),
                                    text = stringResource(id = R.string.tag_km),
                                    color = if (offlineState == OfflineState.Online)
                                        Color.White
                                    else
                                        Color.White.copy(alpha = OfflineStateOpacity),
                                    textAlign = TextAlign.Start,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.weight(0.3f))
                    }
                    Spacer(modifier = Modifier.weight(0.5f))
                }
            }
            else {
                //Spacer(modifier = Modifier.weight(0.2f))
                val currentSize = lerp(0.dp, LocalConfiguration.current.screenHeightDp.dp,
                    animOffsetRemoteOps.value / offsetYValRemoteOps)
                Box(modifier = Modifier
                    .fillMaxSize()
                    .weight(1.3f),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AnimRemoteOperationsScreen(codpViewModel, wearDataViewModel,
                        navController!!, animOffsetRemoteOps, currentSize)
                }
            }
            Spacer(modifier = Modifier.weight(0.1f))
            Box(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .width(IntrinsicSize.Max)
                    .weight(0.4f),
                contentAlignment = Alignment.Center
            ) {
                if (!isDragged) {
                    Button(
                        modifier = Modifier
                            .height(IntrinsicSize.Min)
                            .width(IntrinsicSize.Min),
                        onClick = {
                            coroutineScope.launch {
                                mutex.withLock {
                                    isRemoteOpsShowing = isRemoteOpsShowing.not()
                                    onIsRemoteOpsParamChange(isRemoteOpsShowing)
                                    if (isRemoteOpsShowing.not()) {
                                        delay(slideOutAnimDuration.toLong())
                                        isVehicleInfoShowing = true
                                    } else {
                                        isVehicleInfoShowing = false
                                        wearDataViewModel.getRemoteOpsStatusFromDataLayer()
                                        wearDataViewModel.sendRemoteOpsRequest(RemoteRequests.RemoteOpsAvailability)
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color.Transparent)
                    ) {
                        val ic_footer_arrow = if (isRemoteOpsShowing) painterResource(id = R.drawable.ic_footer_swipe_down)
                        else painterResource(id = R.drawable.ic_footer_swipe_up)
                        Image(
                            modifier = Modifier
                                .height(IntrinsicSize.Min)
                                .width(IntrinsicSize.Min),
                            painter = ic_footer_arrow,
                            contentDescription = "",
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(isRemoteOpsShowing) {
        if (isRemoteOpsShowing) {
            val targetYVal: Float
            if (screenSizeType == ScreenSizeType.SMALL_ROUND)
                targetYVal = -38f
            else
                targetYVal = -50f
            animOffsetVehicleState.animateTo(
                targetValue = targetYVal,
                animationSpec = tween(durationMillis = slideInAnimDuration)
            )
            isScrollDown = false
        } else {
            animOffsetVehicleState.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = slideInAnimDuration)
            )
            isScrollUp = false
        }
    }
    LaunchedEffect(isRemoteOpsShowing){
        if (isRemoteOpsShowing) {
            if (screenSizeType == ScreenSizeType.SMALL_ROUND)
                offsetYValRemoteOps = -50f
            else
                offsetYValRemoteOps = -60f
            animOffsetRemoteOps.animateTo(
                targetValue = offsetYValRemoteOps,
                animationSpec = tween(durationMillis = slideInAnimDuration)
            )
        } else {
            animOffsetRemoteOps.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = slideInAnimDuration)
            )
        }
    }
}
@Composable
fun getSocValueColor(codpResponse: GetTvsmLatestDeviceDataQuery.Response?): Color {
    return if(codpResponse?.soc != null
        && codpResponse.soc.toFloat() < TvsElectricConstants.LowChargeThreshold
        && codpResponse.charging_status != 1) {
        colorResource(R.color.socValueCritical)
    }
    else {
        Color.White
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun VehicleDetailsHomeScreenPreview() {
    //VehicleDetailsHome(null, null, null)
}