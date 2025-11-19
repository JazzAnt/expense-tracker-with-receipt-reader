package com.jazzant.expensetracker.viewmodel

data class ExpenseUiState(
    val id: Int = -1,
    val amount: Float = 0.0f,
    val category: String = "",
    val newCategory: String = "",
    val name: String = "",
    val tipping: Boolean = false,
    val tip: Float = 0.0f,
    val date: Long = System.currentTimeMillis()
)