package com.tvsm.iqubeindia.presentation.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.wear.compose.foundation.SwipeToDismissBoxState
import androidx.wear.compose.foundation.edgeSwipeToDismiss
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.wear.compose.material.*
import androidx.wear.compose.material.HorizontalPageIndicator
import com.google.accompanist.pager.*
import com.tvsm.connect.R
import com.tvsm.iqubeindia.domain.repository.VehicleDetailsRecordRepository
import com.tvsm.iqubeindia.presentation.ui.utils.WearableConstants
import com.tvsm.iqubeindia.viewmodel.CodpViewModel
import com.tvsm.iqubeindia.viewmodel.WearDataLayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalPagerApi::class)
@Composable
fun LandingDashboardScreen(
    swipeToDismissBoxState: SwipeToDismissBoxState,
    codpViewModel: CodpViewModel,
    wearDataViewModel: WearDataLayerViewModel,
    navController: NavHostController
) {
    val maxPages = 2
    var selectedPage by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState()
    val isDragged = pagerState.interactionSource.collectIsDraggedAsState()
    var isPageIndicatorTimeOut by remember { mutableStateOf(false) }
    var isRemoteOpsShowing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var lifecycleEvent by remember { mutableStateOf(Lifecycle.Event.ON_ANY) }
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

    val vehicleModel = wearDataViewModel.getVehicleModel()

    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            lifecycleEvent = event
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
    LaunchedEffect(lifecycleEvent) {
        when(lifecycleEvent) {
            Lifecycle.Event.ON_RESUME -> {
                android.util.Log.d("LandingDashboardScreen","ON_RESUME")
               //TODO Refactor and optimize CODP calls by implementing a generic interface that can scale to multiple vehicles.

                when (vehicleModel) {
                    WearableConstants.BIKE_U388 -> {
                        codpViewModel.getCodpTvsmLatestDeviceDataQuery(null)

                    }
                    WearableConstants.BIKE_U577_PREMIUM -> {
                        codpViewModel.getTvsmAggregateDeviceDataWatchQuery(null)
                        codpViewModel.getTvsmLatestDeviceDataWatchQuery(null)
                        codpViewModel.getLifeTimeTvsmAggregateDeviceDataWatchQuery(null)
                    }
                }

            }
            else -> {}
        }
    }

    //Add a PagerIndicatorState
    val pageIndicatorState: PageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageOffset: Float
                get() = 0f
            override val selectedPage: Int
                get() = selectedPage
            override val pageCount: Int
                get() = maxPages
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        if (selectedPage.equals(0)) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopStart
            ) {
                val painter = painterResource(R.drawable.ic_gradient_round)
                Image(
                    painter = painter,
                    contentDescription = "",
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        //.aspectRatio(painter.intrinsicSize.width / painter.intrinsicSize.height)
                        .fillMaxSize()
                )
            }
        }
        HorizontalPager(modifier = Modifier
            .fillMaxSize()
            .edgeSwipeToDismiss(swipeToDismissBoxState),
            state = pagerState, count = maxPages,
            userScrollEnabled = !isRemoteOpsShowing
        ) { page ->
            selectedPage = pagerState.currentPage
            when (page) {
                0 -> {
                    when (vehicleModel) {
                        WearableConstants.BIKE_U388 -> {
                            VehicleDetailsHome(
                                codpViewModel,
                                wearDataViewModel,
                                navController,
                                isDragged.value
                            ) { newParamValue -> isRemoteOpsShowing = newParamValue }
                        }
                        WearableConstants.BIKE_U577_PREMIUM -> {
                            U577VehicleDetailHome(
                                codpViewModel
                            )
                        }
                        else -> {}
                    }


                }
                1 -> {
                    when (vehicleModel) {
                        WearableConstants.BIKE_U388 -> {
                            TPMSScreen(codpViewModel = codpViewModel)
                        }
                        WearableConstants.BIKE_U577_PREMIUM  -> {
                            U577TripStatsScreen(
                                codpViewModel
                            )
                        }
                    }

                }
                else -> {}
            }
        }
        //Add a HorizontalPageIndicator to this screen
        if (isDragged.value or selectedPage.equals(1)) { // Skip the first page
            if (!isPageIndicatorTimeOut) {
                HorizontalPageIndicator(
                    pageIndicatorState = pageIndicatorState,
                    selectedColor = Color.White,
                    modifier = Modifier.padding(5.dp)
                )
            }
            if (selectedPage.equals(1)) {
                if (isDragged.value) {
                    isPageIndicatorTimeOut = false
                }
                coroutineScope.launch {
                    delay(15*1000)
                    isPageIndicatorTimeOut = true
                }
            }
        }
    }
}
