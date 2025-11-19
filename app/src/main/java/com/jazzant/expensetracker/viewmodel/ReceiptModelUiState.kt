package com.jazzant.expensetracker.viewmodel

data class ReceiptModelUiState(
    val switchState: Boolean = false,
    val checkBoxState: Boolean = false,
    val invalidInput: Boolean = false,
    val keyword: String = "",
    val name: String = "",
    val amountString: String = "",
    val amountFloat: Float = 0.0f,
    val strategy: String = "",
    val strategyValue1: Int = 0
)
