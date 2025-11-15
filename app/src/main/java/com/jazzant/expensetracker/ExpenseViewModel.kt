package com.jazzant.expensetracker

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
    private lateinit var ADD_NEW_CATEGORY: String
    private var expenseId = mutableIntStateOf(-1)
    private val _recognizedText = mutableStateOf<Text?>(null)
    val recognizedText = _recognizedText
    private val _capturedBitmap = mutableStateOf<Bitmap?>(null)
    val capturedBitmap = _capturedBitmap
    lateinit var receiptModelRepository: ReceiptModelRepository
    lateinit var receiptModelList: StateFlow<List<ReceiptModel>>
    private val _receiptModelIndex = mutableIntStateOf(-1)
    val receiptModelIndex = _receiptModelIndex

    fun initializeViewModel(context: Context){
        //Set the Repository
        val expenseDatabase = ExpenseDatabase.getInstance(context)
        expenseRepository = ExpenseRepository(expenseDatabase.expenseDao())
        receiptModelRepository = ReceiptModelRepository(expenseDatabase.receiptModelDao())
        //Fetch the add_new_category string
        ADD_NEW_CATEGORY = context.getString(R.string.add_new_category)
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
    fun expenseUiToExpenseEntity(expenseUiState: ExpenseUiState): Expense{
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
    fun recognizeText(bitmap: Bitmap){
        _capturedBitmap.value = bitmap
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val inputImage = InputImage.fromBitmap(bitmap,0)
        recognizer.process(inputImage)
            .addOnSuccessListener { visionText -> _recognizedText.value = visionText}
            .addOnFailureListener { e -> _recognizedText.value = null }
    }

    fun resetTextRecognition(){
        _capturedBitmap.value = null
        _recognizedText.value = null
        _receiptModelIndex.intValue = -1
    }

    /**
     * Checks the recognized text if it contains any of the keyword in the receiptModelList.
     * Returns the index of the receiptModelList where a keyword is found, or -1 if none are found.
     */
    fun findKeyword(receiptModels: List<ReceiptModel>)
    {
        if(receiptModels.isEmpty()){
            _receiptModelIndex.intValue = -1
            return
        }
        var index = -1
        val listLastIndex = receiptModels.size - 1
        for (i in 0..listLastIndex)
        {
            val keyword = receiptModels[i].keyword
            if(recognizedText.value!!.text.contains(keyword, ignoreCase = true))
            {
                index = i
                break
            }
        }
        _receiptModelIndex.intValue = index
    }
}