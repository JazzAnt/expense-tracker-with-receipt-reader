package com.jazzant.expensetracker.viewmodel

import android.graphics.Bitmap
import com.google.mlkit.vision.text.Text
import com.jazzant.expensetracker.analyzer.Strategy
import com.jazzant.expensetracker.database.expense.Expense

data class ReceiptAnalyzerUiState(
    val recognizedText: Text? = null,
    val capturedBitmap: Bitmap? = null,
    val noReceiptFound: Boolean = false,
    val receiptModelIndex: Int = -1,
    val analyzedExpense: Expense? = null,
    val recognizedTextStringList: List<String> = emptyList(),
    val priceLabelsList: List<Float> = emptyList(),
    val strategies: Map<Strategy, Int> = emptyMap(),
)
