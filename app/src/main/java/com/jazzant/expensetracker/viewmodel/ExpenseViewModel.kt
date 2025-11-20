package com.jazzant.expensetracker.viewmodel

import android.content.Context
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
    private val _expenseState = MutableStateFlow(ExpenseUiState())
    val expenseState: StateFlow<ExpenseUiState> = _expenseState.asStateFlow()

    fun initializeViewModel(context: Context){
        //Set the Repository
        val expenseDatabase = ExpenseDatabase.getInstance(context)
        expenseRepository = ExpenseRepository(expenseDatabase.expenseDao())
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
        if(_expenseState.value.id < 0)
            createExpenseOnDB()
        else
            updateExpenseOnDB()
    }

    fun createExpenseOnDB(){
        viewModelScope.launch {
            expenseRepository.insert(
                getExpenseEntityFromExpenseUIState()
            )
        }
    }
    fun updateExpenseOnDB(){
        viewModelScope.launch {
            expenseRepository.update(
                getExpenseEntityFromExpenseUIState()
            )
        }
    }

    //TYPE CONVERTER
    fun getExpenseEntityFromExpenseUIState(): Expense {
        _expenseState.value
        val amount = if(_expenseState.value.tipping){
            _expenseState.value.amount + _expenseState.value.tip
        } else {
            _expenseState.value.amount
        }

        val expense = Expense(
            amount = amount,
            category = _expenseState.value.category,
            name = _expenseState.value.name,
            date = _expenseState.value.date
        )
        if (_expenseState.value.id >= 0){
            expense.expenseId = _expenseState.value.id
        }
        return expense
    }
    fun expenseEntityToUi(expense: Expense){
        resetUiState()
        setId(expense.expenseId)
        setAmount(expense.amount)
        setCategory(expense.category)
        setName(expense.name)
        setDate(expense.date)
    }
    //UI STATE STUFF
    fun resetUiState(){
        _expenseState.value = ExpenseUiState()
    }

    /**
     * Returns either an error message or null. This weird implementation
     * (returning String? instead of Boolean) is done so that in addition to
     * knowing that there's an error, the UI can also know which value is
     * problematic. The intended use is for the UI to check if this returns
     * a null: if it does proceed with Insert/Update the DB, if it doesn't
     * then show the user the error message e.g. with a Toast.
     * @return an error message as a String or null if there's no errors
     */
    fun checkForErrorsInUiState(context: Context): String?{
        if (_expenseState.value.amount + _expenseState.value.tip < 0)
        { return context.getString(R.string.expenseUiError_invalidAmount) }
        if (_expenseState.value.category.isBlank())
        { return context.getString(R.string.expenseUiError_invalidCategory) }
        if (_expenseState.value.name.isBlank())
        { return context.getString(R.string.expenseUiError_invalidName) }
        return null
    }
    fun setId(expenseId: Int){
        _expenseState.update { currentState ->
            currentState.copy(id = expenseId)
        }
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

    fun setNewCategorySwitch(expenseNewCategorySwitchState: Boolean){
        _expenseState.update { currentState ->
            currentState.copy(newCategorySwitch = expenseNewCategorySwitchState)
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