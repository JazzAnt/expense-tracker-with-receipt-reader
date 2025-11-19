package com.jazzant.expensetracker.viewmodel

import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text

data class ReceiptAnalyzerUiState(
    val recognizedText: Text? = null,
    val capturedBitmap: Bitmap? = null,
    val receiptModelIndex: Int = -1
)
