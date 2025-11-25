package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoadingScreen(
    loadingText: String,
    isLoading: Boolean,
    onLoadingComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()){
        if (isLoading)
        { Text(loadingText) }
        else
        { onLoadingComplete() }

    }

}