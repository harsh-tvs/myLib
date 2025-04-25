package com.tvsm.mylibrary

import androidx.compose.compiler.plugins.kotlin.ComposeFqNames.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AddTwoNumber(){

    ShowAdditionResult()
}

@Composable
fun ShowAdditionResult() {
    val num1 = 2
    val num2 = 3
    val result = num1 + num2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Addition Result",
            fontSize = 24.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "$num1 + $num2 = $result",
            fontSize = 20.sp,
            color = Color.DarkGray
        )
    }
}
