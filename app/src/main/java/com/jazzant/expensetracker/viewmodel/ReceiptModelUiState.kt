package com.jazzant.expensetracker.viewmodel

import com.jazzant.expensetracker.analyzer.Strategy

data class ReceiptModelUiState(
    val switchState: Boolean = false,
    val checkBoxState: Boolean = false,
    val invalidInput: Boolean = false,
    val keyword: String = "",
    val name: String = "",
    val amount: Float = -1.0f,
    val strategy: Strategy = Strategy.NTH_PRICE_LABEL_FROM_LAST,
    val strategyValue1: Int = -1
)
