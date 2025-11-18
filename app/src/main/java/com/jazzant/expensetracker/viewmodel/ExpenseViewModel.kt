package com.jazzant.expensetracker.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.database.ExpenseDatabase
import com.jazzant.expensetracker.database.expense.Expense
import com.jazzant.expensetracker.database.expense.ExpenseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds


class ExpenseViewModel(): ViewModel() {
    //DATABASE STUFF
    lateinit var expenseRepository: ExpenseRepository
    lateinit var expenseList: StateFlow<List<Expense>>
    lateinit var categoryList: StateFlow<List<String>>
    lateinit var sumOfExpenses: StateFlow<Float>
    private lateinit var ADD_NEW_CATEGORY: String
    private var expenseId = mutableIntStateOf(-1)

    fun initializeViewModel(context: Context){
        //Set the Repository
        val expenseDatabase = ExpenseDatabase.getInstance(context)
        expenseRepository = ExpenseRepository(expenseDatabase.expenseDao())
        //Fetch the add_new_category string
        ADD_NEW_CATEGORY = context.getString(R.string.addNewCategorySelection)
        //Pass the flows
        expenseList = expenseRepository.getAllExpenses()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = emptyList()
            )
        categoryList = expenseRepository.getAllCategories()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = emptyList()
            )
        sumOfExpenses = expenseRepository.getSumOfExpenses()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = 0f
            )
    }

    fun insertExpenseToDB(){
        if(expenseId.intValue < 0)
            createExpenseOnDB()
        else
            updateExpenseOnDB()
    }

    fun createExpenseOnDB(){
        viewModelScope.launch {
            expenseRepository.insert(
                expenseUiToExpenseEntity(
                    expenseUiState = _expenseState.value,
                )
            )
        }
    }
    fun updateExpenseOnDB(){
        val expense = expenseUiToExpenseEntity(_expenseState.value)
        expense.expenseId = expenseId.intValue
        viewModelScope.launch {
            expenseRepository.update(
                expense
            )
        }
    }

    //TYPE CONVERTER
    fun expenseUiToExpenseEntity(expenseUiState: ExpenseUiState): Expense {
        val amount = if(expenseUiState.tipping){
            expenseUiState.amount + expenseUiState.tip
        } else {
            expenseUiState.amount
        }

        val category = if(expenseUiState.category == ADD_NEW_CATEGORY){
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
        expenseId.intValue = expense.expenseId
    }
    //UI STATE STUFF
    private val _expenseState = MutableStateFlow(ExpenseUiState())
    val expenseState: StateFlow<ExpenseUiState> = _expenseState.asStateFlow()

    fun resetUiState(){
        _expenseState.value = ExpenseUiState()
        expenseId.intValue = -1
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
    fun setDate(expenseDate: Long){
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