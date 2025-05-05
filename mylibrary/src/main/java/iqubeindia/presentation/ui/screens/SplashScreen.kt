package com.tvsm.iqubeindia.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.res.painterResource
import com.tvsm.connect.R
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme


@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background), // Background color
        contentAlignment = Alignment.Center // Center the logo
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_round_tvs_logo), // Replace with your logo
            contentDescription = "App Logo",
            modifier = Modifier.size(48.dp) // Logo size 48dp x 48dp
        )
    }
}

