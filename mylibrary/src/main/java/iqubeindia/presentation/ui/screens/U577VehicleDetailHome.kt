package com.tvsm.iqubeindia.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.withStyle
import androidx.wear.compose.material.Text
import com.tvsm.connect.R
import com.tvsm.iqubeindia.viewmodel.CodpViewModel


private val TAG = "U577VehicleDetailHome"
@Composable
fun U577VehicleDetailHome(
    codpViewModel: CodpViewModel
) {

    val stateLatestDataWatch by codpViewModel.codpGetTvsmLatestDeviceDataWatchResponse.collectAsState()
    val codpResponseLatestDataWatch = if (stateLatestDataWatch.data == null) codpViewModel.vehicleDetailsRecordRepository.getVehicleLatestDataWatchValuePref()
    else stateLatestDataWatch.data

    val stateLifeTimeTvsmAggregateDeviceDataWatch by codpViewModel.codpGetLifeTimeTvsmAggregateDeviceDataWatchResponse.collectAsState()
    val codpResponseLifeTimeTvsmAggregateDeviceDataWatch = if (stateLifeTimeTvsmAggregateDeviceDataWatch.data == null) codpViewModel.vehicleDetailsRecordRepository.getVehicleLifetimeAggregateDataValuePref()
    else stateLifeTimeTvsmAggregateDeviceDataWatch.data

    val syncAlert by codpViewModel.syncAlert.collectAsState()
    val colorState by codpViewModel.colorState.collectAsState()

    // Main container for the wearOs U577 compose UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            painter = painterResource(id = R.drawable.component_427_72),
            contentDescription = null,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .width(IntrinsicSize.Max),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // TVS logo
            Box(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .width(IntrinsicSize.Max)
                    .weight(0.5f),
                contentAlignment = Alignment.TopCenter
            )
            {
                Image(
                    painter = painterResource(id = R.drawable.ic_tvs_logo_header),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp)
                )
            }

            // Synced Text below the Logo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(
                            color = colorState,
                            shape = CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(4.dp))
                androidx.compose.material.Text(
                    text = syncAlert,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.roboto_regular))
                )

            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = 8.dp),
            horizontalAlignment= Alignment.CenterHorizontally,
            verticalArrangement= Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val fuelLevel = codpResponseLatestDataWatch?.fuel_level?.toInt()
                        Log.d("progressFuel", "progress is $fuelLevel")
                        val progressValue = if (fuelLevel != null) fuelLevel else 0
                        SemiCircularBarsProgressBar(progress = progressValue, modifier = Modifier.fillMaxSize()) // Let the ProgressBar fill the Box

                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Odo",
                                fontSize = 12.sp,
                                color = Color(0xFFB7B7B7),
                                fontFamily = FontFamily(Font(R.font.roboto_regular))
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color.White,
                                            fontFamily = FontFamily(Font(R.font.roboto_medium))
                                        )
                                    ) {
                                        append(
                                            if (codpResponseLatestDataWatch?.odometer != null)
                                                "${codpResponseLatestDataWatch.odometer.toInt()}"
                                            else "-"
                                        )
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color(0xFFB7B7B7),
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily(Font(R.font.roboto_regular))
                                        )
                                    ) {
                                        append(" km")
                                    }

                                },
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "Last Ride",
                                fontSize = 12.sp,
                                color = Color(0xFFB7B7B7),
                                fontFamily = FontFamily(Font(R.font.roboto_regular))
                            )
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color.White,
                                            fontFamily = FontFamily(Font(R.font.roboto_medium))
                                        )
                                    ) {
                                        append(
                                            if (codpResponseLifeTimeTvsmAggregateDeviceDataWatch?.last_ride_distance != null)
                                                "${codpResponseLifeTimeTvsmAggregateDeviceDataWatch.last_ride_distance}"
                                            else "-"
                                        )
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color(0xFFB7B7B7),
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily(Font(R.font.roboto_regular))
                                        )
                                    ) {
                                        append(" km")
                                    }

                                },
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }

            }

        }

    }

}
