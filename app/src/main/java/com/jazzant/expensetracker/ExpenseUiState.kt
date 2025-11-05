package com.jazzant.expensetracker

import java.time.LocalDate

data class ExpenseUiState(
    val amount: Float = 0.0f,
    val category: String = "",
    val name: String = "",
    val date: LocalDate = LocalDate.now()
)
