package com.jazzant.expensetracker.viewmodel

import android.content.Context
import android.graphics.Bitmap
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
import com.jazzant.expensetracker.analyzer.parseReceipt
import com.jazzant.expensetracker.analyzer.toPriceLabelsList
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
    private lateinit var expenseRepository: ExpenseRepository
    lateinit var expenseList: StateFlow<List<Expense>>
    lateinit var categoryList: StateFlow<List<String>>
    lateinit var sumOfExpenses: StateFlow<Float>
    private val _expenseState = MutableStateFlow(ExpenseUiState())
    val expenseState: StateFlow<ExpenseUiState> = _expenseState.asStateFlow()
    private lateinit var receiptModelRepository: ReceiptModelRepository
    lateinit var receiptModelList: StateFlow<List<ReceiptModel>>
    private val _receiptAnalyzerUiState = MutableStateFlow(ReceiptAnalyzerUiState())
    val receiptAnalyzerUiState: StateFlow<ReceiptAnalyzerUiState> = _receiptAnalyzerUiState.asStateFlow()
    private val _receiptModelUiState = MutableStateFlow(ReceiptModelUiState())
    val receiptModelUiState: StateFlow<ReceiptModelUiState> = _receiptModelUiState.asStateFlow()
    private val _homeNavUiState = MutableStateFlow(HomeNavUiState())
    val homeNavUiState: StateFlow<HomeNavUiState> = _homeNavUiState
    private val _navDrawerId = MutableStateFlow(0)
    val navDrawerId = _navDrawerId.value
    private val _modelBeingEdited: MutableStateFlow<ReceiptModel?> = MutableStateFlow(null)
    val modelBeingEdited: StateFlow<ReceiptModel?> = _modelBeingEdited

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

    fun setNavDrawerId(id: Int)
    { _navDrawerId.value = id }

    //RECEIPT MODEL EDITOR STUFF
    fun resetReceiptModelEdit(){
        _modelBeingEdited.value = null
    }
    fun setModelEdit(receiptModel: ReceiptModel){
        _modelBeingEdited.value = receiptModel
    }
    fun setModelEditName(value: String){
        _modelBeingEdited.update { currentState ->
            currentState!!.copy(name = value)
        }
    }
    fun setModelEditCategory(value: String){
        _modelBeingEdited.update { currentState ->
            currentState!!.copy(category = value)
        }
    }
    fun deleteModelEdit(){
        viewModelScope.launch {
            receiptModelRepository.delete(_modelBeingEdited.value!!)
        }
    }
    fun updateModelEdit(){
        viewModelScope.launch {
            receiptModelRepository.update(_modelBeingEdited.value!!)
        }
    }
    /**
     * Checks the value of HomeNavUiState and re-Query the expenseList based on the
     * availability of the Query values (e.g. if DateRange is not null then Query based
     * on dateRange).
     * If no query value are valid then Query all expenses.
     */
    fun updateExpenseList(){
        val navState = _homeNavUiState.value
        if (navState.isSearching){
            expenseList = expenseRepository.searchExpensesByName(navState.searchValue)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = emptyList()
                )
            sumOfExpenses = expenseRepository.sumOfExpensesByName(navState.searchValue)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = 0f
                )
            return
        }
        if (navState.selectedCategory.isNotBlank() && navState.dateRange.first != null && navState.dateRange.second != null){
            expenseList = expenseRepository.getAllExpenses(
                category = navState.selectedCategory,
                dateRange = navState.dateRange as Pair<Long, Long>
            )
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = emptyList()
                )
            sumOfExpenses = expenseRepository.getSumOfExpenses(
                category = navState.selectedCategory,
                dateRange = navState.dateRange
            )
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = 0f
                )
            return
        }
        if (navState.selectedCategory.isNotBlank()){
            expenseList = expenseRepository.getAllExpenses(
                category = navState.selectedCategory,
            )
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = emptyList()
                )
            sumOfExpenses = expenseRepository.getSumOfExpenses(
                category = navState.selectedCategory,
            )
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = 0f
                )
            return
        }
        if (navState.dateRange.first != null && navState.dateRange.second != null){
            expenseList = expenseRepository.getAllExpenses(
                dateRange = navState.dateRange as Pair<Long, Long>
            )
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = emptyList()
                )
            sumOfExpenses = expenseRepository.getSumOfExpenses(
                dateRange = navState.dateRange
            )
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5.seconds),
                    initialValue = 0f
                )
            return
        }
        expenseList = expenseRepository.getAllExpenses()
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

    fun deleteExpenseOnDB(){
        viewModelScope.launch {
            expenseRepository.delete(
                getExpenseEntityFromExpenseUIState()
            )
        }
    }
    fun insertReceiptModelToDB(){
        viewModelScope.launch {
            receiptModelRepository.insert(
                receiptModelUiToEntity()
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
    
    fun receiptModelUiToEntity(): ReceiptModel{
        val receiptUiState = _receiptModelUiState.value
        return ReceiptModel(
            keyword = receiptUiState.keyword,
            name = receiptUiState.name,
            category = receiptUiState.category,
            parserStrategyId = receiptUiState.strategy.ordinal,
            parserStrategyValue1 = receiptUiState.strategyValue1,
        )
    }

    fun receiptModelUiToExpenseUi() {
        val receiptUiState = _receiptModelUiState.value
        setAmount(receiptUiState.amount)
        setName(receiptUiState.name)
        setCategory(receiptUiState.category)
        setDate(System.currentTimeMillis())
        setNewCategorySwitch(false)
        setTipping(false)
    }
    //HOME NAV UI STATE STUFF
    fun resetHomeNavUiSTate(){
        _homeNavUiState.value = HomeNavUiState()
    }
    fun setHomeNavTitleText(value: String){
        _homeNavUiState.update { currentState ->
            currentState.copy(titleText = value)
        }
    }
    fun setHomeNavSearching(value: Boolean){
        _homeNavUiState.update { currentState ->
            currentState.copy(isSearching = value)
        }
    }
    fun setHomeNavSearchValue(value: String){
        _homeNavUiState.update { currentState ->
            currentState.copy(searchValue = value)
        }
    }
    fun setHomeNavDateRange(value: Pair<Long?, Long?>){
        _homeNavUiState.update { currentState ->
            currentState.copy(dateRange =  value)
        }
    }
    fun setHomeNavCategory(value: String){
        _homeNavUiState.update { currentState ->
            currentState.copy(selectedCategory = value)
        }
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
    fun setAnalyzerNoReceiptFoundState(state: Boolean){
        _receiptAnalyzerUiState.update { currentState ->
            currentState.copy(noReceiptFound = state)
        }
    }
    fun setAnalyzerModelIndex(index: Int){
        _receiptAnalyzerUiState.update { currentState ->
            currentState.copy(receiptModelIndex = index)
        }
    }
    fun setAnalyzedExpense(expense: Expense){
        _receiptAnalyzerUiState.update { currentState ->
            currentState.copy(analyzedExpense = expense)
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
            .addOnFailureListener { e -> throw ViewModelException("ERROR: Recognizer Fails to Find Text on Image") }
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

    /**
     * Parses the recognized text using the given model.
     * @return an Expense entity containing the parsed data.
     */
    fun parseRecognizedTextFromModel(model: ReceiptModel): Expense
    {
        //Parse Strategy and Value
        val strategy = Strategy.entries[model.parserStrategyId]
        val value1 = model.parserStrategyValue1
        //Parse Amount
        val priceLabels = _receiptAnalyzerUiState.value.recognizedText!!.toPriceLabelsList()
        val amount = parseReceipt(
            strategy = strategy,
            priceLabels = priceLabels,
            n = value1
        )
        //Generate Expense()
        val expense = Expense(
            amount = amount,
            category = model.category,
            name = model.name,
            date = System.currentTimeMillis()
        )
        return expense
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

    fun setReceiptCategory(category: String){
        _receiptModelUiState.update { currentState ->
            currentState.copy(category = category)
        }
    }
    fun setReceiptNewCategorySwitch(state: Boolean){
        _receiptModelUiState.update { currentState ->
            currentState.copy(newCategorySwitch = state)
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

    fun validateReceiptModelCategory(){
        val category = _receiptModelUiState.value.category
        val newCategorySwitch = _receiptModelUiState.value.newCategorySwitch
        if (newCategorySwitch)
        {
            if (category.isNotBlank())
            { setReceiptInvalidInput(true) }
            else
            { setReceiptInvalidInput(false) }
        }
        else
        {
            val categoryList = _receiptModelUiState.value.category
            if (categoryList.contains(category))
            { setReceiptInvalidInput(false) }
            else
            { setReceiptInvalidInput(true) }
        }
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