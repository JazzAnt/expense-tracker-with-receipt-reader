package com.jazzant.expensetracker

import java.time.LocalDate

data class ExpenseUiState(
    val amount: Float = 0.0f,
    val category: String = "",
    val newCategory: String = "",
    val name: String = "",
    val tipping: Boolean = false,
    val tip: Float = 0.0f,
    val date: LocalDate = LocalDate.now()
)

enum class SpecialCategories{
    ADD_NEW_CATEGORY
}