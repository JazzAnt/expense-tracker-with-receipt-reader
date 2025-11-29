package com.jazzant.expensetracker.viewmodel

data class HomeNavUiState(
    val titleText: String = "Home Screen",
    val isSearching: Boolean = false,
    val searchValue: String = "",
    val dateRange: Pair<Long?,Long?> = Pair(null,null),
    val selectedCategory: String = "",
)
