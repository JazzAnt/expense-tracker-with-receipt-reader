package com.jazzant.expensetracker

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.exp


class ExpenseViewModel(): ViewModel() {
    //DATABASE STUFF
    lateinit var expenseRepository: ExpenseRepository
    private val _expenseList = MutableStateFlow<List<Expense>>(emptyList())
    val expenseList: StateFlow<List<Expense>> get() = _expenseList
    private val _categoryList = MutableStateFlow<List<String>>(emptyList())
    val categoryList: StateFlow<List<String>> get() = _categoryList

    fun setDatabase(context: Context){
        val expenseDatabase = ExpenseDatabase.getInstance(context)
        expenseRepository = ExpenseRepository(expenseDatabase.expenseDao())
        viewModelScope.launch {
            expenseRepository.getAllExpenses().collect { expenses ->
                _expenseList.value = expenses
            }
            expenseRepository.getAllCategories().collect { categories ->
                _categoryList.value = categories
            }
        }
    }

    fun insertExpenseUiToDb(expense: Expense){
        viewModelScope.launch {
            expenseRepository.insert(
                expenseUiToExpenseEntity(
                    _expenseState.value
                )
            )
        }
    }

    //TYPE CONVERTER
    fun expenseUiToExpenseEntity(expenseUiState: ExpenseUiState): Expense{
        val amount = if(expenseUiState.tipping){
            expenseUiState.amount + expenseUiState.tip
        } else {
            expenseUiState.amount
        }

        val category = if(expenseUiState.category == SpecialCategories.ADD_NEW_CATEGORY.name){
            expenseUiState.newCategory
        } else {
            expenseUiState.category
        }

        return Expense(
            amount = amount,
            category = category,
            name = expenseUiState.name,
            date = expenseUiState.date
        )
    }
    fun expenseEntityToUi(expense: Expense){
        resetUiState()
        setAmount(expense.amount)
        setCategory(expense.category)
        setName(expense.name)
        setDate(expense.date)
    }
    //UI STATE STUFF
    private val _expenseState = MutableStateFlow(ExpenseUiState())
    val expenseState: StateFlow<ExpenseUiState> = _expenseState.asStateFlow()

    fun resetUiState(){
        _expenseState.value = ExpenseUiState()
    }

    fun setAmount(expenseAmount: Float){
        _expenseState.update { currentState ->
            currentState.copy(amount = expenseAmount)
        }
    }
    fun setCategory(expenseCategory: String){
        _expenseState.update { currentState ->
            currentState.copy(category = expenseCategory)
        }
    }
    fun setName(expenseName : String){
        _expenseState.update { currentState ->
            currentState.copy(name = expenseName)
        }
    }
    fun setDate(expenseDate: LocalDate){
        _expenseState.update { currentState ->
            currentState.copy(date = expenseDate)
        }
    }

    fun setNewCategory(expenseNewCategory: String){
        _expenseState.update { currentState ->
            currentState.copy(newCategory = expenseNewCategory)
        }
    }

    fun setTipping(expenseTipping: Boolean){
        _expenseState.update { currentState ->
            currentState.copy(tipping = expenseTipping)
        }
    }
    fun setTip(expenseTip: Float){
        _expenseState.update { currentState ->
            currentState.copy(tip = expenseTip)
        }
    }
}