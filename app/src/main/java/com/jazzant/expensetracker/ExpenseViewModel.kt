package com.jazzant.expensetracker

import android.app.Application
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    val expenses: SnapshotStateList<Expense>
    val repository: ExpenseRepository

    init {
        val dao = ExpenseDatabase.getInstance(application).expenseDao()
        repository = ExpenseRepository(dao)
        expenses = repository.expenses
    }

    fun addExpense(expense: Expense) = viewModelScope.launch {
        repository.insert(expense)
    }
    fun updateExpense(expense: Expense) = viewModelScope.launch {
        repository.update(expense)
    }
    fun deleteExpense(expense: Expense) = viewModelScope.launch {
        repository.delete(expense)
    }
    fun deleteAllExpenses() = viewModelScope.launch {
        repository.deleteAll()
    }
}