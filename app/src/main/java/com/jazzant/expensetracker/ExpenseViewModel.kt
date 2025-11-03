package com.jazzant.expensetracker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class ExpenseViewModel(): ViewModel() {
    lateinit var expenseDatabase: ExpenseDatabase
    lateinit var expenseRepository: ExpenseRepository
    private val _expenseList = MutableStateFlow<List<Expense>>(emptyList())
    val expenseList: StateFlow<List<Expense>> get() = _expenseList

    fun setDatabase(context: Context){
        expenseDatabase = Room.databaseBuilder(
            context,
            ExpenseDatabase::class.java,
            "expense_database"
        ).build()
        expenseRepository = ExpenseRepository(expenseDatabase.expenseDao())
        viewModelScope.launch {
            expenseRepository.getAllExpenses().collect { expenses ->
                _expenseList.value = expenses
            }
        }
    }


    fun insert(expense: Expense){
        viewModelScope.launch {
            expenseRepository.insert(expense)
        }
    }
}