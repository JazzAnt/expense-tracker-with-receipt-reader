package com.jazzant.expensetracker.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.analyzer.Strategy
import com.jazzant.expensetracker.analyzer.containsKeyword
import com.jazzant.expensetracker.analyzer.evaluateAllPossibleStrategies
import com.jazzant.expensetracker.database.receiptmodel.ReceiptModel
import com.jazzant.expensetracker.database.receiptmodel.ReceiptModelRepository
import com.jazzant.expensetracker.database.ExpenseDatabase
import com.jazzant.expensetracker.database.expense.Expense
import com.jazzant.expensetracker.database.expense.ExpenseRepository
import kotlinx.coroutines.coroutineScope
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
    fun setAnalyzerPriceLabels(list: List<Float>){
        _receiptAnalyzerUiState.update { currentState ->
            currentState.copy(priceLabelsList = list)
        }
    }
    fun setReceiptStrategyMap(strategies: Map<Strategy, Int>){
        _receiptAnalyzerUiState.update { currentState ->
            currentState.copy(strategies = strategies)
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

    fun setReceiptAmount(amount: Float){
        _receiptModelUiState.update { currentState ->
            currentState.copy(amount = amount)
        }
    }

    fun setReceiptStrategy(strategy: Strategy){
        _receiptModelUiState.update { currentState ->
            currentState.copy(strategy = strategy)
        }
    }

    fun setReceiptStrategyValue1(value1: Int){
        _receiptModelUiState.update { currentState ->
            currentState.copy(strategyValue1 = value1)
        }
    }
    //Receipt Model Value Validators
    /**
     * Validates the receiptModelUiState Keyword value.
     *
     * This verifies if the keyword: (1) isn't blank. (2) is contained in the recognized text.
     *
     * Note that this does not check if the keyword already exists in the database because this
     * function should only be called during the creation of a new keyword in which case the
     * assumption is that the scanned receipt does not contain an existing keyword. Thus the (2)
     * requirement should also verify that the keyword doesn't exist in the database.
     * @return no values. Instead, Boolean is directly set to the receiptModelUiState InvalidInput value.
     */
    fun validateReceiptModelKeyword(){
        val text = _receiptAnalyzerUiState.value.recognizedText
        val keyword = _receiptModelUiState.value.keyword
        if (text == null || keyword.isBlank())
        {
            setReceiptInvalidInput(true)
            return
        }
        setReceiptInvalidInput(
            !text.containsKeyword(keyword)
        )
    }
    /**
     * Validates the receiptModelUiState name value.
     *
     * This simply verifies that the name isn't blank.
     * @return no values. Instead, Boolean is directly set to the receiptModelUiState InvalidInput value.
     */
    fun validateReceiptModelName(){
        val name = _receiptModelUiState.value.name
        setReceiptInvalidInput(
            name.isBlank()
        )
    }

    /**
     * Validates the receiptModelUiState amount value.
     *
     * This verifies: (1) the float value is not negative; and (2) that the float value is actually
     * contained inside the recognized text.
     * @return no values. Instead, Boolean is directly set to the receiptModelUiState InvalidInput value.
     */
    fun validateReceiptModelAmount(){
        val amountFloat = _receiptModelUiState.value.amount
        if (amountFloat < 0)
        {
            setReceiptInvalidInput(true)
            return
        }

        val priceLabelFloatList = _receiptAnalyzerUiState.value.priceLabelsList
        setReceiptInvalidInput(
            !priceLabelFloatList.contains(amountFloat)
        )
    }

    fun validateReceiptModelStrategy(){
        val strategy = _receiptModelUiState.value.strategy
        val value1 = _receiptModelUiState.value.strategyValue1
        val viableStrategies = _receiptAnalyzerUiState.value.strategies

        if (viableStrategies[strategy] == value1)
        { setReceiptInvalidInput(false) }
        else
        { setReceiptInvalidInput(true) }
    }

    fun evaluateStrategies(){
        viewModelScope.launch {
            val priceLabels = _receiptAnalyzerUiState.value.priceLabelsList
            val desiredPriceLabel = _receiptModelUiState.value.amount
            val validStrategies = evaluateAllPossibleStrategies(priceLabels, desiredPriceLabel)
            setReceiptStrategyMap(validStrategies)
        }
    }
}