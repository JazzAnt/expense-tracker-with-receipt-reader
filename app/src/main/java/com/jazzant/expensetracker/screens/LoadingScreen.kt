package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

@Composable
fun LoadingScreen(
    loadingText: String,
    isLoading: Boolean,
    onLoadingComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()){
        if (isLoading)
        {
            Text(
                text = loadingText,
                fontSize = TextUnit(40f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        else
        { onLoadingComplete() }

    }

}