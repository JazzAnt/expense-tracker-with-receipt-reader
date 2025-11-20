package com.jazzant.expensetracker.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.analyzer.containsKeyword
import com.jazzant.expensetracker.database.receiptmodel.ReceiptModel
import com.jazzant.expensetracker.database.receiptmodel.ReceiptModelRepository
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
    lateinit var receiptModelRepository: ReceiptModelRepository
    lateinit var receiptModelList: StateFlow<List<ReceiptModel>>
    private val _receiptAnalyzerUiState = MutableStateFlow(ReceiptAnalyzerUiState())
    val receiptAnalyzerUiState: StateFlow<ReceiptAnalyzerUiState> = _receiptAnalyzerUiState.asStateFlow()
    private val _receiptModelUiState = MutableStateFlow(ReceiptModelUiState())
    val receiptModelUiState: StateFlow<ReceiptModelUiState> = _receiptModelUiState.asStateFlow()

    fun initializeViewModel(context: Context){
        //Set the Repository
        val expenseDatabase = ExpenseDatabase.getInstance(context)
        expenseRepository = ExpenseRepository(expenseDatabase.expenseDao())
        receiptModelRepository = ReceiptModelRepository(expenseDatabase.receiptModelDao())
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
        receiptModelList = receiptModelRepository.getAllReceiptModels()
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

    //TEXT ANALYSIS STUFF
    fun resetReceiptAnalyzerUiState(){
        _receiptAnalyzerUiState.value = ReceiptAnalyzerUiState()
    }
    fun setAnalyzerText(recognizedText: Text){
        _receiptAnalyzerUiState.update { currentState ->
            currentState.copy(recognizedText = recognizedText)
        }
    }
    fun setAnalyzerBitmap(bitmap: Bitmap){
        _receiptAnalyzerUiState.update { currentState ->
            currentState.copy(capturedBitmap = bitmap)
        }
    }
    fun setAnalyzerModelIndex(index: Int){
        _receiptAnalyzerUiState.update { currentState ->
            currentState.copy(receiptModelIndex = index)
        }
    }
    fun setAnalyzerTextStringList(list: List<String>){
        _receiptAnalyzerUiState.update { currentState ->
            currentState.copy(recognizedTextStringList = list)
        }
    }

    fun recognizeText(bitmap: Bitmap){
        resetReceiptAnalyzerUiState()
        setAnalyzerBitmap(bitmap)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromBitmap(bitmap,0)
        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                setAnalyzerText(visionText)
            }
            .addOnFailureListener { e -> TODO("Maybe throw exception here idk")}
    }

    /**
     * Checks the recognized text if it contains any of the keyword in the receiptModelList.
     * Returns the index of the receiptModelList where a keyword is found, or -1 if none are found.
     */
    fun findKeyword(receiptModels: List<ReceiptModel>)
    {
        if(receiptModels.isEmpty()){
            setAnalyzerModelIndex(-1)
            return
        }
        var index = -1
        val listLastIndex = receiptModels.size - 1
        for (i in 0..listLastIndex)
        {
            val keyword = receiptModels[i].keyword
            if(_receiptAnalyzerUiState.value.recognizedText!!.containsKeyword(keyword))
            {
                index = i
                break
            }
        }
        setAnalyzerModelIndex(index)
    }

    //Receipt Model Ui State Stuff
    fun resetReceiptModelUiState(){
        _receiptModelUiState.value = ReceiptModelUiState()
    }

    fun setReceiptSwitch(state: Boolean){
        _receiptModelUiState.update { currentState ->
            currentState.copy(switchState = state)
        }
    }

    fun setReceiptCheckBox(state: Boolean){
        _receiptModelUiState.update { currentState ->
            currentState.copy(checkBoxState = state)
        }
    }

    fun setReceiptInvalidInput(state: Boolean){
        _receiptModelUiState.update { currentState ->
            currentState.copy(invalidInput = state)
        }
    }

    fun setReceiptKeyword(keyword: String){
        _receiptModelUiState.update { currentState ->
            currentState.copy(keyword = keyword)
        }
    }

    fun setReceiptName(name: String){
        _receiptModelUiState.update { currentState ->
            currentState.copy(name = name)
        }
    }

    fun setReceiptAmountString(amount: String){
        _receiptModelUiState.update { currentState ->
            currentState.copy(amountString = amount)
        }
    }

    fun setReceiptAmountFloat(amount: Float){
        _receiptModelUiState.update { currentState ->
            currentState.copy(amountFloat = amount)
        }
    }

    fun setReceiptStrategy(strategy: String){
        _receiptModelUiState.update { currentState ->
            currentState.copy(strategy = strategy)
        }
    }

    fun setReceiptStrategyValue1(value1: Int){
        _receiptModelUiState.update { currentState ->
            currentState.copy(strategyValue1 = value1)
        }
    }
}