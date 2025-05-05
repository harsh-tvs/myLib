package com.tvsm.iqubeindia.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.tvsm.connect.R
import com.tvsm.iqubeindia.presentation.ui.utils.TvsElectricConstants
import com.tvsm.iqubeindia.viewmodel.CodpViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TPMSScreen(codpViewModel: CodpViewModel) {
    val state by codpViewModel.codpTvsmLatestDeviceDataResponse.collectAsState()
    val codpResponse = if (state.response == null) codpViewModel.vehicleDetailsRecordRepository.getVehicleLatestDataValuePref()
    else state.response
    val frontTirePressure = codpResponse?.tyre_pressure?.front
    val rearTirePressure = codpResponse?.tyre_pressure?.rear
    /*
    When the app goes into the background or onPause state and
    then transition from the Vehicle Detail Home screen to the TPMS screen and
    then back to the Vehicle Detail Home screen,
    (for this use case , again i have to cancel api call and resume periodic call)
     */

    LaunchedEffect(Unit) {
        codpViewModel.cancelApiCall()
    }
    DisposableEffect(Unit) {
        onDispose {
            codpViewModel.resetCancelFlag()
            codpViewModel.startPeriodicApiCalls(10000)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.1f))
        Box(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .width(IntrinsicSize.Max)
                .padding(5.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                modifier = Modifier.fillMaxSize(),
                text = stringResource(id = R.string.tag_tire_pressure),
                color = Color.White,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
        Row(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Min)
                .weight(2f),
            verticalAlignment = Alignment.Bottom
        ) {
            Spacer(modifier = Modifier.weight(0.1f))
            Box(modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Max)
                .weight(0.2f),
                contentAlignment = Alignment.BottomCenter
            ) {
                if(frontTirePressure != null) {
                    if(isFrontTirePressureCritical(frontTirePressure.toInt())) {
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            text = frontTirePressure.toString(),
                            color = colorResource(id = R.color.tpmsLowPressure),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                    else{
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            text = frontTirePressure.toString(),
                            color = colorResource(id = R.color.tpmsIdlePressure),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    /*TODO Remove the default tire pressure to display on front tire*/
                    /*Text(
                        modifier = Modifier.fillMaxSize(),
                        text = stringResource(id = R.string.front_tire_pressure),
                        color = colorResource(id = R.color.tpmsIdlePressure),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )*/
                }
            }
            Box(modifier = Modifier
                .fillMaxSize()
                .weight(0.8f)
            ) {
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Image(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.05f),
                        painter = painterResource(id = R.drawable.ic_tpms_base_line_white),
                        contentDescription = "")
                }
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomStart
                ) {
                    if(frontTirePressure!=null &&
                        isFrontTirePressureCritical(frontTirePressure.toInt())) {
                        val painter = painterResource(R.drawable.ic_tpms_tyre_pressure_red)
                        Image(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .fillMaxHeight(0.5f),
                            painter = painter,
                            contentDescription = "",
                            contentScale = ContentScale.None
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomEnd
                ){
                    if(rearTirePressure != null &&
                        isRearTirePressureCritical(rearTirePressure.toInt())) {
                        val painter = painterResource(R.drawable.ic_tpms_tyre_pressure_red)
                        Image(
                            modifier = Modifier
                                .fillMaxWidth(0.4f)
                                .fillMaxHeight(0.7f)
                                .padding(end = 1.5.dp),
                            painter = painter,
                            contentDescription = "",
                            contentScale = ContentScale.None
                        )
                    }
                }
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    var vehicleUrl=codpViewModel.vehicleDetailsRecordRepository.getCodpAccessCredentialsPref().vehicleImageUrl
                    Log.d("TPMS","$vehicleUrl")
                    if(vehicleUrl.isEmpty()){
                        Log.d("TPMS","inside")
                        vehicleUrl="https://uat-tvsconnectevapi.tvsmotor.net/domestic-assets/Content/Images/VehicleImages/U388/PEARLWHITEBikeFull@2x.png"
                       }
                    GlideImage(model = vehicleUrl, contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.78f))
                }
            }
            Box(modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Max)
                .weight(0.2f),
                contentAlignment = Alignment.BottomCenter
            ) {
                if(rearTirePressure != null) {
                    if(isRearTirePressureCritical(rearTirePressure.toInt())) {
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            text = rearTirePressure,
                            color = colorResource(id = R.color.tpmsLowPressure),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                    else{
                        Text(
                            modifier = Modifier.fillMaxSize(),
                            text = rearTirePressure,
                            color = colorResource(id = R.color.tpmsIdlePressure),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    /*TODO Remove the default tire pressure to display on rear tire*/
                    /*Text(
                        modifier = Modifier.fillMaxSize(),
                        text = stringResource(id = R.string.rear_tire_pressure),
                        color = colorResource(id = R.color.tpmsIdlePressure),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )*/
                }
            }
            Spacer(modifier = Modifier.weight(0.1f))
        }
        Spacer(modifier = Modifier.weight(0.1f))
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .weight(0.5f),
            verticalAlignment = Alignment.Top
        ) {
            Spacer(modifier = Modifier.weight(0.4f))
            Box(modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Min)
                .weight(0.5f)
            ) {
                Text(modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawRoundRect(
                            Color(0xFF333333),
                            cornerRadius = CornerRadius(20.dp.toPx())
                        )
                    }
                    .padding(4.dp),
                    text = stringResource(id = R.string.front_tire_idle_pressure),
                    color = colorResource(id = R.color.tpmsIdlePressure),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.weight(0.4f))
            Box(modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Min)
                .weight(0.5f)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            drawRoundRect(
                                Color(0xFF333333),
                                cornerRadius = CornerRadius(20.dp.toPx())
                            )
                        }
                        .padding(4.dp),
                    text = stringResource(id = R.string.rear_tire_idle_pressure),
                    color = colorResource(id = R.color.tpmsIdlePressure),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                )
            }
            Spacer(modifier = Modifier.weight(0.4f))
        }
        Spacer(modifier = Modifier.weight(0.1f))
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .weight(0.9f),
            verticalAlignment = Alignment.Top
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            Box(modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Min)
                .padding(start = 4.5.dp, top = 8.5.dp, end = 6.5.dp, bottom = 8.5.dp)
            ){
                Canvas(modifier = Modifier.fillMaxWidth()){
                    drawCircle(
                        color = Color(0xFFFF0000),
                        radius = 8f,
                        center = Offset(size.width/2, size.height/2)
                    )
                }
            }
            Box(modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Min)
                .weight(0.3f)
            ) {
                Text(modifier = Modifier
                    .fillMaxSize(),
                    text = stringResource(id = R.string.legend_text_caution),
                    color = colorResource(id = R.color.tpmsLowPressure),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Start,
                )
            }
            //Spacer(modifier = Modifier.weight(0.1f))
            Box(modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Min)
                .padding(start = 4.5.dp, top = 8.5.dp, end = 6.5.dp, bottom = 8.5.dp)
            ){
                Canvas(modifier = Modifier.fillMaxWidth()){
                    drawCircle(
                        color = Color(0xFFD1D1D1),
                        radius = 8f,
                        center = Offset(size.width/2, size.height/2)
                    )
                }
            }
            Box(modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Min)
                .weight(0.3f)
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxSize(),
                    text = stringResource(id = R.string.legend_text_optimum),
                    color = colorResource(id = R.color.tpmsIdlePressure),
                    fontSize = 12.nonScaledSp,
                    textAlign = TextAlign.Start,
                )
            }
            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

// Define a helper function to check if the rear and front tire pressure is critical
fun isFrontTirePressureCritical(pressure: Int): Boolean {
    return pressure <= TvsElectricConstants.TpmsFrontMinValue ||
            pressure >= TvsElectricConstants.TpmsFrontMaxValue
}

fun isRearTirePressureCritical(pressure: Int): Boolean {
    return pressure <= TvsElectricConstants.TpmsRearMinValue ||
            pressure >= TvsElectricConstants.TpmsRearMaxValue
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun TPMSScreenPreview() {
    //TPMSScreen(null)
}