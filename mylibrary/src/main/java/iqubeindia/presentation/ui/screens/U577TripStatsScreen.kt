package com.tvsm.iqubeindia.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import  androidx.wear.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.wear.compose.material.ButtonDefaults.buttonColors
import androidx.wear.compose.material.PositionIndicator
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.tvsm.connect.R
import com.tvsm.iqubeindia.viewmodel.CodpViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private val TAG = "U577TripStatsScreen"

/**
 * Data class representing a card with a list of key-value pairs and gradient colors
 */
data class CardItem(
    val data: List<Pair<String, String>>,
    val color1: Color,
    val color2: Color
)
/**
 * Data class representing an embedded card within the main card
 */
data class EmbeddedCardItem(
    val data: List<Pair<String, String>>,
    val color1: Color,
    val color2: Color
)

/**
 * Main card screen composable with overall layout and navigation
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun U577TripStatsScreen(
    codpViewModel: CodpViewModel,
) {
    val stateLatestDataWatch by codpViewModel.codpGetTvsmLatestDeviceDataWatchResponse.collectAsState()
    val codpResponseLatestDataWatch = if (stateLatestDataWatch.data == null) codpViewModel.vehicleDetailsRecordRepository.getVehicleLatestDataWatchValuePref()
    else stateLatestDataWatch.data

    val stateLifeTimeTvsmAggregateDeviceDataWatch by codpViewModel.codpGetLifeTimeTvsmAggregateDeviceDataWatchResponse.collectAsState()
    val codpResponseLifeTimeTvsmAggregateDeviceDataWatch = if (stateLifeTimeTvsmAggregateDeviceDataWatch.data == null) codpViewModel.vehicleDetailsRecordRepository.getVehicleLifetimeAggregateDataValuePref()
    else stateLifeTimeTvsmAggregateDeviceDataWatch.data

    val stateTvsmAggregateDeviceDataWatch by codpViewModel.codpGetTvsmAggregateDeviceDataWatchResponse.collectAsState()
    val codpResponseTvsmAggregateDeviceDataWatch = if (stateTvsmAggregateDeviceDataWatch.data == null) codpViewModel.vehicleDetailsRecordRepository.getVehicleAggregateDataValuePref()
    else stateTvsmAggregateDeviceDataWatch.data

    // Collect ride statistics from ViewModel

    val tripAduration by codpViewModel.tripAduration.collectAsState()
    val tripBduration by codpViewModel.tripBduration.collectAsState()
    val tripAdistance by codpViewModel.tripAdistance.collectAsState()
    val tripBdistance by codpViewModel.tripBdistance.collectAsState()
    val tripAavgSpd by codpViewModel.tripAavgSpeed.collectAsState()
    val tripBavgSpd by codpViewModel.tripBavgSpeed.collectAsState()
    val syncAlert by codpViewModel.syncAlert.collectAsState()
    val colorState by codpViewModel.colorState.collectAsState()

    val embeddedPagerState = rememberPagerState()
    val gradientColors = listOf(
        Pair(Color(0xFF170D56), Color(0xFF7625B0)),
        Pair(Color(0xFF0B2F50), Color(0xFF2496B0)),
        Pair(Color(0xFF54250C), Color(0xFFB07425))
    )

    val cards = listOf(
        CardItem(
            data = listOf(
                "Dist. Trvl \n   (km)" to if (tripAdistance.toInt() == 0) "-" else tripAdistance.toString(),
                "Avg. Spd \n (km/hr)" to if (tripAavgSpd == 0) "-" else tripAavgSpd.toString(),
                "Time \n (hrs)" to if (tripAduration.toInt() == 0) "-" else tripAduration.toString()
            ),
            color1 = gradientColors[0].first,
            color2 = gradientColors[0].second
        ),
        CardItem(
            data = listOf(
                "No. of Rds \n" to (codpResponseTvsmAggregateDeviceDataWatch?.number_of_rides?.takeIf { it != 0 }?.toString() ?: "-"),
                "Dist. Trvl \n   (km)" to (codpResponseTvsmAggregateDeviceDataWatch?.distance_covered_today?.toInt()?.takeIf { it != 0 }?.toString() ?: "-"),
                "Fuel Cons.\n       (l)" to "-" // Keeping fuel consumption as "-"
            ),
            color1 = gradientColors[1].first,
            color2 = gradientColors[1].second
        ),
        CardItem(
            data = listOf(
                "Top Spd \n (km/hr)" to (codpResponseLifeTimeTvsmAggregateDeviceDataWatch?.top_speed?.toInt()?.takeIf { it != 0 }?.toString() ?: "-"),
                "Avg. Spd \n  (km/hr)" to (codpResponseLifeTimeTvsmAggregateDeviceDataWatch?.avg_speed?.toInt()?.takeIf { it != 0 }?.toString() ?: "-"),
                "0.60 \n(Sec)" to (codpResponseLifeTimeTvsmAggregateDeviceDataWatch?.best_0_60?.toInt()?.takeIf { it != 0 }?.toString() ?: "-")
            ),
            color1 = gradientColors[2].first,
            color2 = gradientColors[2].second
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ) {
        // Observe changes to activeIndex
        var activeIndex by remember { mutableStateOf(0) }
        // Column to arrange items vertically
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
                modifier = Modifier.padding(top = 8.dp) // Adjust padding to 2dp for minimal space
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
                Text(
                    text = syncAlert,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontFamily = FontFamily(Font(R.font.roboto_regular))
                )

            }
        }

        // Display the text when the active index is 1 (second card)
        when {
            activeIndex == 0 -> {
                // Check the page of the embedded pager for the first card
                when (embeddedPagerState.currentPage) {
                    0 -> {
                        resetAndTrip(
                            resetValues = { codpViewModel.resetTripValues() },
                            tripLabel = "Trip A",
                        )
                    }
                    1 -> {
                        resetAndTrip(
                            resetValues = { codpViewModel.resetTripValues() },
                            tripLabel = "Trip B",
                            showResetButton = true
                        )

                    }
                }
            }
            activeIndex == 1 -> {
                todayRide()
            }
            activeIndex == 2 -> {
                speed()
            }
        }

        // Centered stackCard
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(top = 20.dp),
            horizontalAlignment= Alignment.CenterHorizontally,
            verticalArrangement= Arrangement.Center
        ) {
            StackedCards(
                cards = cards,
                modifier = Modifier,
                onActiveIndexChanged = { newIndex ->
                    activeIndex = newIndex
                },
                embeddedPagerState = embeddedPagerState,
                //pagerState=pagerState,
                codpViewModel
            )
        }

        // Use LaunchedEffect to set initial state to Trip A
        LaunchedEffect(Unit) {
            embeddedPagerState.scrollToPage(0)
        }

    }

}

/**
 * Composable function to create a stacked card UI with vertical drag interaction
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun StackedCards(
    cards: List<CardItem>,
    modifier: Modifier = Modifier,
    onActiveIndexChanged: (Int) -> Unit,
    embeddedPagerState: PagerState,
    //pagerState: PagerState,
    codpViewModel:CodpViewModel
) {
    var activeIndex by remember { mutableStateOf(0) }
    var dragOffset by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    // Collect ride statistics from codpViewModel
    val tripAduration by codpViewModel.tripAduration.collectAsState()
    val tripBduration by codpViewModel.tripBduration.collectAsState()
    val tripAdistance by codpViewModel.tripAdistance.collectAsState()
    val tripBdistance by codpViewModel.tripBdistance.collectAsState()
    val tripAavgSpd by codpViewModel.tripAavgSpeed.collectAsState()
    val tripBavgSpd by codpViewModel.tripBavgSpeed.collectAsState()
    val threshold = 50f
    // Pager state for embedded cards in the first card
    val embeddedPagerState = embeddedPagerState

    // Embedded cards definition for the first card
    val embeddedCards = listOf(
        EmbeddedCardItem(
            data = listOf(
                "Dist. Trvl \n   (km)" to if (tripAdistance.toInt() == 0) "-" else tripAdistance.toString(),
                "Avg. Spd \n (km/hr)" to if (tripAavgSpd == 0) "-" else tripAavgSpd.toString(),
                "Time \n (hrs)" to if (tripAduration.toInt() == 0) "-" else tripAduration.toString()
            ),
            color1 = Color(0xFF170D56),
            color2 = Color(0xFF7625B0)
        ),
        EmbeddedCardItem(
            data = listOf(
                "Dist. Trvl \n   (km)" to if (tripBdistance.toInt() == 0) "-" else tripBdistance.toString(),
                "Avg. Spd \n (km/hr)" to if (tripBavgSpd == 0) "-" else tripBavgSpd.toString(),
                "Time \n (hrs)" to if (tripBduration.toInt() == 0) "-" else tripBduration.toString()
            ),
            color1 = Color(0xFF170D56),
            color2 = Color(0xFF7625B0)
        )
    )


    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        when {
                            dragOffset < -threshold && activeIndex < cards.size - 1 -> {
                                activeIndex++
                                onActiveIndexChanged(activeIndex)
                            }
                            dragOffset > threshold && activeIndex > 0 -> {
                                activeIndex--
                                onActiveIndexChanged(activeIndex)
                            }
                            dragOffset > threshold && activeIndex == 0 -> {
                                // Navigate to page 0 when swiping down from the top card
//                                coroutineScope.launch {
//                                    pagerState.animateScrollToPage(0) // Reset to page 0
//                                }
                            }
                        }
                        dragOffset = 0f
                    },
                    onDragCancel = { dragOffset = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        dragOffset = (dragOffset + dragAmount).coerceIn(-100f, 100f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        for (i in (activeIndex + 1) downTo activeIndex) {
            if (i < cards.size) {
                val isActive = i == activeIndex
                val yOffset = if (isActive) dragOffset else 60f

                Card(
                    modifier = Modifier
                        .width(190.dp)
                        .height(90.dp)
                        .graphicsLayer {
                            val scale = if (isActive) 1f else 0.8f
                            scaleX = scale
                            scaleY = scale
                            translationY = yOffset
                            alpha = if (isActive) 1f else 0.5f
                        }
                        .zIndex(if (isActive) 100f else 0f),
                    elevation = if (isActive) 8.dp else 4.dp,
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(cards[i].color1, cards[i].color2)
                                )
                            )
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        // For the first card, add horizontal pager
                        if (i == 0) {
                            HorizontalPager(
                                count = embeddedCards.size,
                                state = embeddedPagerState,
                                modifier = Modifier.fillMaxSize()
                            ) { pageIndex ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(2.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    embeddedCards[pageIndex].data.forEach { (title, value) ->
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.weight(0.1f)
                                        ) {
                                            Text(
                                                text = title,
                                                color = Color.Gray,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily(Font(R.font.roboto_regular))
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = value,
                                                color = Color.White,
                                                fontSize = 20.sp,
                                                fontFamily = FontFamily(Font(R.font.roboto_medium))
                                            )

                                        }
                                    }
                                }
                            }

                            // horizontal Dot Indicator at the bottom
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 10.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                repeat(embeddedCards.size) { iteration ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 2.dp)
                                            .size(6.dp)
                                            .background(
                                                color = if (embeddedPagerState.currentPage == iteration)
                                                    Color.White
                                                else
                                                    Color.Gray.copy(alpha = 0.5f),
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }

                        } else {
                            // For other cards, use existing layout
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                cards[i].data.forEach { (title, value) ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                        modifier = Modifier.weight(0.1f)
                                    ) {
                                        Text(
                                            text = title,
                                            color = Color.Gray,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily(Font(R.font.roboto_regular))
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = value,
                                            color = Color.White,
                                            fontSize = 20.sp,
                                            fontFamily = FontFamily(Font(R.font.roboto_medium))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Existing image display logic
                if (i == 2 && activeIndex == 2) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_gradient_bg_u577),
                        contentDescription = "Drawable below third card",
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .width(380.dp)
                            .height(150.dp)

                    )
                }
            }
        }
    }
}

@Composable
fun resetAndTrip(
    resetValues: () -> Unit,
    tripLabel: String,
    showResetButton: Boolean = true
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(top = 60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = tripLabel,
            fontSize = 14.sp,
            color = Color.White ,
            fontFamily = FontFamily(Font(R.font.roboto_regular)),
            modifier = Modifier
                .offset(y = (-10).dp)
                .padding(start=25.dp)
        )

    }
    if (showResetButton) {
        Button(
            onClick = { resetValues() },
            shape = RectangleShape,
            colors = buttonColors(
                backgroundColor = Color(0xFF0069E0),
                contentColor = Color.White
            ),
            modifier = Modifier
                .width(40.dp)
                .padding(top=58.dp)
                .height(20.dp)
                .offset(x = (55).dp, y = (-10).dp)
        ) {
            Text(
                "Reset",
                fontSize = 12.sp,
                color = Color.White,
                fontFamily = FontFamily(Font(R.font.roboto_regular))
            )
        }

    }
 }

@Composable
fun todayRide() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp,start=25.dp) // Adjust the top padding as needed
    ) {
        Text(
            text = "Today's Ride",
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.roboto_regular)),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart) // Align the text to the start (left) of the box
                .offset(y = (-10).dp) // Slightly lift the text above the card (adjust the offset as needed)
        )
    }
}


@Composable
fun speed() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp, start = 25.dp) // Adjust the top padding as needed
    ) {
        Text(
            text = "Speed",
            fontSize = 14.sp,
            fontFamily = FontFamily(Font(R.font.roboto_regular)),
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopStart) // Align the text to the start (left) of the box
                .offset(y = (-10).dp) // Slightly lift the text above the card (adjust the offset as needed)
        )
    }
}

