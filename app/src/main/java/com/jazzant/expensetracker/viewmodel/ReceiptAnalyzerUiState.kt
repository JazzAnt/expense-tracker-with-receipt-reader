package com.jazzant.expensetracker.viewmodel

import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text
import com.jazzant.expensetracker.analyzer.Strategy

data class ReceiptAnalyzerUiState(
    val recognizedText: Text? = null,
    val capturedBitmap: Bitmap? = null,
    val receiptModelIndex: Int = -1,
    val recognizedTextStringList: List<String> = emptyList(),
    val priceLabelsList: List<Float> = emptyList(),
    val strategies: Map<Strategy, Int> = emptyMap(),
)
