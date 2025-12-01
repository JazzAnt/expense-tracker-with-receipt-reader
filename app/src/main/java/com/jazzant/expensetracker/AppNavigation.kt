package com.jazzant.expensetracker

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jazzant.expensetracker.analyzer.Strategy
import com.jazzant.expensetracker.analyzer.containsPriceLabels
import com.jazzant.expensetracker.analyzer.parseReceipt
import com.jazzant.expensetracker.analyzer.toBlockList
import com.jazzant.expensetracker.analyzer.toLineList
import com.jazzant.expensetracker.analyzer.toPriceLabelsList
import com.jazzant.expensetracker.screens.CameraPermissionScreen
import com.jazzant.expensetracker.screens.CameraPreviewScreen
import com.jazzant.expensetracker.screens.ChooseAmountScreen
import com.jazzant.expensetracker.screens.ChooseCategoryScreen
import com.jazzant.expensetracker.screens.ChooseKeywordScreen
import com.jazzant.expensetracker.screens.ChooseNameScreen
import com.jazzant.expensetracker.screens.ChooseStrategyScreen
import com.jazzant.expensetracker.screens.ExpenseEditorScreen
import com.jazzant.expensetracker.screens.ExpenseListScreen
import com.jazzant.expensetracker.screens.LoadingScreen
import com.jazzant.expensetracker.screens.ReceiptModelListScreen
import com.jazzant.expensetracker.screens.TextAnalyzerScreen
import com.jazzant.expensetracker.screens.TextRecognizerScreen
import com.jazzant.expensetracker.ui.AlertDialog
import com.jazzant.expensetracker.viewmodel.ExpenseViewModel
import com.jazzant.expensetracker.viewmodel.ViewModelException
import kotlinx.coroutines.launch

@Composable
fun ExpenseApp(
    viewModel: ExpenseViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
){
    //INITIALIZE VARIABLES
    viewModel.initializeViewModel(context)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(value = backStackEntry?.destination?.route?: AppScreen.HOME_SCREEN.name)
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val currentItemId by viewModel.navDrawerId.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var fabPosition: FabPosition = FabPosition.End
    if (currentScreen == AppScreen.EDIT_EXPENSE)
    { fabPosition = FabPosition.Center }
    //TODO: figure out how to make these aware of the HomeScreen Lifecycle only instead of ExpenseApp
    val categoryList by viewModel.categoryList.collectAsStateWithLifecycle()
    val homeNavState by viewModel.homeNavUiState.collectAsStateWithLifecycle()

    //NAVIGATION FUNCTIONS
    fun resetAllCameraStatesAndGoBackToHome(){
        viewModel.resetReceiptAnalyzerUiState()
        viewModel.resetReceiptModelUiState()
        navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false)
    }
    fun resetAllCameraStatesAndGoBackToCamera(){
        viewModel.resetReceiptAnalyzerUiState()
        viewModel.resetReceiptModelUiState()
        navController.popBackStack(route = AppScreen.CAMERA_PREVIEW.name, inclusive = false)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            NavigationDrawerSheet(
                currentItemId = currentItemId,
                navDrawerItems = listOf(
                    DrawerItem(
                        id = 0,
                        label = "Expenses",
                        icon = Icons.Default.Home,
                        contentDescription = "Home Menu",
                        onClick = {
                            navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false)
                            viewModel.resetHomeNavUiSTate()
                            viewModel.setNavDrawerId(0)
                            scope.launch { drawerState.close() }
                        }
                    ),
                    DrawerItem(
                        id = 1,
                        label = "Receipt Models",
                        icon = Icons.Default.ShoppingCart,
                        contentDescription = "Receipt Menu",
                        onClick = {
                            navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false)
                            navController.navigate(route = AppScreen.RECEIPT_MODEL_LIST.name)
                            viewModel.setNavDrawerId(1)
                            viewModel.resetReceiptModelEdit()
                            scope.launch { drawerState.close() }
                        }
                    )
                )
            )
        },
        gesturesEnabled = true,
    ) {
        //ACTUAL COMPOSE SCREEN
        Scaffold(
            topBar = {
                if (currentScreen == AppScreen.HOME_SCREEN) {
                    HomeNavBar(
                        onMenuButtonPress = {
                            scope.launch {
                                if (drawerState.isClosed)
                                { drawerState.open() }
                                else
                                { drawerState.close() }
                            }
                        },
                        titleText = homeNavState.titleText,
                        isSearching = homeNavState.isSearching,
                        setIsSearching = { viewModel.setHomeNavSearching(it) },
                        searchValue = homeNavState.searchValue,
                        onSearchValueChange = { viewModel.setHomeNavSearchValue(it) },
                        dateRange = homeNavState.dateRange,
                        onDateRangeChanged = { viewModel.setHomeNavDateRange(it) },
                        categoryList = categoryList,
                        selectedCategory = homeNavState.selectedCategory,
                        onSelectionChange = { viewModel.setHomeNavCategory(it) }
                    )
                } else if (currentScreen == AppScreen.RECEIPT_MODEL_LIST) {
                 SettingNavBar(
                     onMenuButtonPress = {
                         scope.launch {
                             if (drawerState.isClosed)
                             { drawerState.open() }
                             else
                             { drawerState.close() }
                         }
                     },
                     onGoHomeButtonPress = { navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false)},
                     titleText = "Receipt Model List"
                 )
                } else if (currentScreen == AppScreen.EDIT_EXPENSE) {
                    val expenseState = viewModel.expenseState.collectAsStateWithLifecycle()
                    val openResetAlertDialog = remember { mutableStateOf(false) }
                    val openDeleteAlertDialog = remember { mutableStateOf(false) }
                    EditorNavBar(
                        isCreatingNewExpense = expenseState.value.id < 0,
                        onBackButtonPress = { navController.popBackStack() },
                        onResetButtonPress = { openResetAlertDialog.value = true },
                        onDeleteButtonPress = { openDeleteAlertDialog.value = true },
                        onSaveButtonPress = {
                            val error = viewModel.checkForErrorsInUiState(context)
                            if (error != null) {
                                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                            } else {
                                viewModel.insertExpenseToDB()
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.saveExpenseToast),
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.popBackStack(
                                    route = AppScreen.HOME_SCREEN.name,
                                    inclusive = false
                                )
                            }
                        }
                    )
                    when {
                        openResetAlertDialog.value -> {
                            AlertDialog(
                                onDismissRequest = { openResetAlertDialog.value = false },
                                onConfirmation = { viewModel.resetUiState() },
                                dialogTitle = "Reset Input Values",
                                dialogText = "Remove all Input Values?",
                                icon = Icons.Default.Info
                            )
                        }
                        openDeleteAlertDialog.value -> {
                            AlertDialog(
                                onDismissRequest = { openDeleteAlertDialog.value = false },
                                onConfirmation = {
                                    viewModel.deleteExpenseOnDB()
                                    Toast.makeText(context, "Deleted Expense", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false)
                                                 },
                                dialogTitle = "Delete Expense",
                                dialogText = "Delete This Expense from the Database? This action cannot be undone.",
                                icon = Icons.Default.Info
                            )
                        }
                    }
                } else if (CAMERA_ANALYZE_SCREENS.contains(currentScreen)) {
                    CameraNavBar(
                        onBackButtonPress = {
                            viewModel.resetReceiptAnalyzerUiState()
                            navController.popBackStack(
                                route = AppScreen.EDIT_EXPENSE.name,
                                inclusive = false
                            )
                        }
                    )
                } else if (RECEIPT_MODELING_SCREENS.contains(currentScreen)) {
                    val openAlertDialog = remember { mutableStateOf(false) }
                    ReceiptCreatorNavBar(
                        titleText = getReceiptModelingScreenTitle(currentScreen),
                        onBackButtonPress = {
                            // This is done because the screen before ChooseStrategy is the loading screen which should be skipped if you click back
                            if (currentScreen == AppScreen.CHOOSE_STRATEGY) {
                                navController.popBackStack(
                                    route = AppScreen.CHOOSE_AMOUNT.name,
                                    inclusive = false
                                )
                            } else {
                                navController.popBackStack()
                            }
                        },
                        onResetButtonPress = { openAlertDialog.value = true }
                    )
                    when {
                        openAlertDialog.value -> {
                            AlertDialog(
                                onDismissRequest = { openAlertDialog.value = false },
                                onConfirmation = { resetAllCameraStatesAndGoBackToCamera() },
                                dialogTitle = "Cancel Creating Model",
                                dialogText = "Discard All Values and Go Back to the Camera?",
                                icon = Icons.Default.Warning
                            )
                        }
                    }
                } else if (currentScreen == AppScreen.REQUEST_CAMERA_PERMISSION) {
                    TopNavBar(currentRoute = "Checking Camera Permission")
                } else {
                    TopNavBar(currentRoute = currentScreen.name)
                }

            },
            floatingActionButtonPosition = fabPosition,
            floatingActionButton = {
                if (currentScreen == AppScreen.EDIT_EXPENSE) {
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate(AppScreen.REQUEST_CAMERA_PERMISSION.name) },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Analyze Receipt with Camera",
                        )
                        Text("Analyze with Camera")
                    }
                } else if (currentScreen == AppScreen.HOME_SCREEN) {
                    FloatingActionButton(
                        onClick = { navController.navigate(AppScreen.EDIT_EXPENSE.name) },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Create,
                            contentDescription = "Create New Expense",
                        )
                    }
                }
            }

        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppScreen.HOME_SCREEN.name,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                composable(route = AppScreen.HOME_SCREEN.name) {
                    val expenseList by viewModel.expenseList.collectAsStateWithLifecycle()
                    val sumOfExpenses by viewModel.sumOfExpenses.collectAsStateWithLifecycle()
                    ExpenseListScreen(
                        list = expenseList, onCardClick = { expense ->
                            viewModel.expenseEntityToUi(expense)
                            navController.navigate(route = AppScreen.EDIT_EXPENSE.name)
                        },
                        sumOfExpenses = sumOfExpenses
                    )
                }
                composable(route = AppScreen.RECEIPT_MODEL_LIST.name) {
                    val receiptModelList by viewModel.receiptModelList.collectAsStateWithLifecycle()
                    val categoryList by viewModel.categoryList.collectAsStateWithLifecycle()
                    val currentModel by viewModel.modelBeingEdited.collectAsStateWithLifecycle()
                    val openAlertDialog = remember { mutableStateOf(false) }
                    ReceiptModelListScreen(
                        list = receiptModelList,
                        onCardClick = { viewModel.setModelEdit(it) },
                        currentReceiptModel = currentModel,
                        onEditorNameChange = { viewModel.setModelEditName(it) },
                        onEditorCategoryChange = { viewModel.setModelEditCategory(it) },
                        onEditorSaveChanges = {
                            viewModel.updateModelEdit()
                            Toast.makeText(context, "Saved Changes", Toast.LENGTH_SHORT).show()
                                              },
                        onEditorDelete = { openAlertDialog.value = true },
                        categoryList = categoryList
                    )
                    when {
                        openAlertDialog.value ->
                            AlertDialog(
                                onDismissRequest = { openAlertDialog.value = false },
                                onConfirmation = {
                                    viewModel.deleteModelEdit()
                                    viewModel.resetReceiptModelEdit()
                                                 },
                                dialogTitle = "Delete Confirmation",
                                dialogText = "Delete this receipt model? This action cannot be undone.",
                                icon = Icons.Default.Warning
                            )
                    }
                }
                composable(route = AppScreen.EDIT_EXPENSE.name) {
                    val expenseState by viewModel.expenseState.collectAsStateWithLifecycle()
                    val categoryList by viewModel.categoryList.collectAsStateWithLifecycle()
                    ExpenseEditorScreen(
                        categoryList = categoryList,
                        amount = expenseState.amount,
                        onAmountChange = { viewModel.setAmount(it) },
                        name = expenseState.name,
                        onNameChange = { viewModel.setName(it) },
                        category = expenseState.category,
                        onCategoryChange = { viewModel.setCategory(it) },
                        newCategorySwitch = expenseState.newCategorySwitch,
                        onNewCategorySwitchChange = { viewModel.setNewCategorySwitch(it) },
                        tipping = expenseState.tipping,
                        onTippingChange = { viewModel.setTipping(it) },
                        tip = expenseState.tip,
                        onTipChange = { viewModel.setTip(it) },
                        date = expenseState.date,
                        onDateChange = { viewModel.setDate(it ?: expenseState.date) },
                    )
                }
                composable(route = AppScreen.REQUEST_CAMERA_PERMISSION.name) {
                    CameraPermissionScreen(
                        onCameraPermissionGranted = { navController.navigate(AppScreen.CAMERA_PREVIEW.name) },
                        onCameraPermissionDenied = { navController.popBackStack() }
                    )
                }
                composable(route = AppScreen.CAMERA_PREVIEW.name) {
                    CameraPreviewScreen(
                        onImageCapture = { imageProxy ->
                            viewModel.resetReceiptAnalyzerUiState()
                            try {
                                viewModel.recognizeText(imageProxy.toBitmap())
                                navController.navigate(AppScreen.TEXT_RECOGNIZER.name)
                            } catch (e: ViewModelException) {
                                Toast.makeText(
                                    context,
                                    e.message,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    )
                }
                composable(route = AppScreen.TEXT_RECOGNIZER.name) {
                    val receiptAnalyzerState by viewModel.receiptAnalyzerUiState.collectAsStateWithLifecycle()
                    val receiptModelList by viewModel.receiptModelList.collectAsStateWithLifecycle()
                    TextRecognizerScreen(
                        recognizedText = receiptAnalyzerState.recognizedText,
                        bitmap = receiptAnalyzerState.capturedBitmap!!,
                        receiptNotFoundOnImage = receiptAnalyzerState.noReceiptFound,
                        onTextRecognized = {
                            //Validate if the text is a receipt by evaluating if it has price labels
                            if (receiptAnalyzerState.recognizedText?.containsPriceLabels()
                                    ?: false
                            ) {
                                viewModel.findKeyword(receiptModelList)
                                val index = receiptAnalyzerState.receiptModelIndex
                                if (index >= 0) {
                                    val model = receiptModelList[index]
                                    val expense = viewModel.parseRecognizedTextFromModel(model)
                                    viewModel.setAnalyzedExpense(expense)
                                }
                                navController.navigate(AppScreen.TEXT_ANALYZER.name)
                            } else {
                                viewModel.setAnalyzerNoReceiptFoundState(true)
                            }
                        },
                        onRetakeImageButtonPress = { resetAllCameraStatesAndGoBackToCamera() },
                        onCancelButtonPress = { resetAllCameraStatesAndGoBackToHome() }
                    )
                }
                composable(route = AppScreen.TEXT_ANALYZER.name) {
                    val receiptAnalyzerState by viewModel.receiptAnalyzerUiState.collectAsStateWithLifecycle()
                    TextAnalyzerScreen(
                        receiptModelIndex = receiptAnalyzerState.receiptModelIndex,
                        bitmap = receiptAnalyzerState.capturedBitmap!!,
                        onCreateNewReceiptModelButtonPress = {
                            viewModel.resetReceiptModelUiState()
                            viewModel.setAnalyzerTextStringList(
                                list = receiptAnalyzerState.recognizedText!!.toBlockList()
                            )
                            navController.navigate(AppScreen.CHOOSE_KEYWORD.name)
                        },
                        onInputExpenseManuallyButtonPress = {
                            val text = receiptAnalyzerState.recognizedText!!
                            viewModel.resetUiState()
                            viewModel.setName(text.toLineList()[0])
                            viewModel.setAmount(
                                parseReceipt(
                                    strategy = Strategy.NTH_HIGHEST_PRICE_LABEL,
                                    priceLabels = text.toPriceLabelsList(),
                                    n = 0
                                )
                            )
                            Toast.makeText(
                                context,
                                "Selected most likely values",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate(AppScreen.EDIT_EXPENSE.name)
                        },
                        onUseAnalyzedExpenseButtonPress = {
                            viewModel.expenseEntityToUi(receiptAnalyzerState.analyzedExpense!!)
                            navController.navigate(AppScreen.EDIT_EXPENSE.name)
                        },
                        onEditAnalyzedExpenseButtonPress = {
                            viewModel.expenseEntityToUi(receiptAnalyzerState.analyzedExpense!!)
                            viewModel.insertExpenseToDB()
                            resetAllCameraStatesAndGoBackToHome()
                        },
                        analyzedExpense = receiptAnalyzerState.analyzedExpense,
                        onRetakeImageButtonPress = { resetAllCameraStatesAndGoBackToCamera() },
                        onCancelButtonPress = { resetAllCameraStatesAndGoBackToHome() }
                    )
                }
                composable(route = AppScreen.CHOOSE_KEYWORD.name) {
                    val receiptAnalyzerState by viewModel.receiptAnalyzerUiState.collectAsStateWithLifecycle()
                    val receiptModelState by viewModel.receiptModelUiState.collectAsStateWithLifecycle()
                    ChooseKeywordScreen(
                        switchState = receiptModelState.switchState,
                        onSwitchStateChanged = { viewModel.setReceiptSwitch(it) },
                        textBlockList = receiptAnalyzerState.recognizedTextStringList,
                        keyword = receiptModelState.keyword,
                        onKeywordChange = {
                            viewModel.setReceiptKeyword(it)
                            viewModel.validateReceiptModelKeyword()
                        },
                        invalidInput = receiptModelState.invalidInput,
                        onNextButtonPress = {
                            viewModel.validateReceiptModelName()
                            navController.navigate(AppScreen.CHOOSE_NAME.name)
                        },
                    )
                }
                composable(route = AppScreen.CHOOSE_NAME.name) {
                    val receiptAnalyzerState by viewModel.receiptAnalyzerUiState.collectAsStateWithLifecycle()
                    val receiptModelState by viewModel.receiptModelUiState.collectAsStateWithLifecycle()
                    ChooseNameScreen(
                        checkBoxState = receiptModelState.checkBoxState,
                        onCheckBoxStateChange = { viewModel.setReceiptCheckBox(it) },
                        name = receiptModelState.name,
                        onNameChange = {
                            viewModel.setReceiptName(it)
                            viewModel.validateReceiptModelName()
                        },
                        invalidInput = receiptModelState.invalidInput,
                        onNextButtonPress = {
                            viewModel.setAnalyzerPriceLabels(
                                receiptAnalyzerState.recognizedText!!.toPriceLabelsList()
                            )
                            viewModel.validateReceiptModelAmount()
                            navController.navigate(AppScreen.CHOOSE_AMOUNT.name)
                        },
                    )
                }
                composable(route = AppScreen.CHOOSE_AMOUNT.name) {
                    val receiptAnalyzerState by viewModel.receiptAnalyzerUiState.collectAsStateWithLifecycle()
                    val receiptModelState by viewModel.receiptModelUiState.collectAsStateWithLifecycle()
                    ChooseAmountScreen(
                        amountList = receiptAnalyzerState.priceLabelsList,
                        amount = receiptModelState.amount,
                        onAmountChange = {
                            viewModel.setReceiptAmount(it)
                            viewModel.validateReceiptModelAmount()
                        },
                        invalidInput = receiptModelState.invalidInput,
                        onNextButtonPress = {
                            viewModel.validateReceiptModelCategory()
                            navController.navigate(AppScreen.CHOOSE_CATEGORY.name)
                        },
                    )
                }
                composable(route = AppScreen.CHOOSE_CATEGORY.name) {
                    val categoryList by viewModel.categoryList.collectAsStateWithLifecycle()
                    val receiptModelState by viewModel.receiptModelUiState.collectAsStateWithLifecycle()
                    ChooseCategoryScreen(
                        newCategorySwitch = receiptModelState.newCategorySwitch,
                        onNewCategorySwitchChange = { viewModel.setReceiptNewCategorySwitch(it) },
                        category = receiptModelState.category,
                        onCategoryChange = { viewModel.setReceiptCategory(it) },
                        categoryList = categoryList,
                        invalidInput = receiptModelState.invalidInput,
                        onNextButtonPress = {
                            viewModel.evaluateStrategies()
                            navController.navigate(AppScreen.ANALYZING_STRATEGY.name)
                        },
                    )
                }
                composable(route = AppScreen.ANALYZING_STRATEGY.name) {
                    val receiptAnalyzerState by viewModel.receiptAnalyzerUiState.collectAsStateWithLifecycle()
                    LoadingScreen(
                        loadingText = "Analyzing Strategies...",
                        isLoading = receiptAnalyzerState.strategies.isEmpty(),
                        onLoadingComplete = {
                            viewModel.validateReceiptModelStrategy()
                            navController.navigate(AppScreen.CHOOSE_STRATEGY.name)
                        }
                    )
                }
                composable(route = AppScreen.CHOOSE_STRATEGY.name) {
                    val receiptAnalyzerState by viewModel.receiptAnalyzerUiState.collectAsStateWithLifecycle()
                    val receiptModelState by viewModel.receiptModelUiState.collectAsStateWithLifecycle()
                    ChooseStrategyScreen(
                        strategyList = receiptAnalyzerState.strategies.keys.toList(),
                        strategy = receiptModelState.strategy,
                        onStrategyChange = {
                            viewModel.setReceiptStrategy(it)
                            viewModel.setReceiptStrategyValue1(
                                receiptAnalyzerState.strategies[it] ?: 1
                            )
                            viewModel.validateReceiptModelStrategy()
                        },
                        //TODO: Add high order function to manipulate RadioText
                        invalidInput = receiptModelState.invalidInput,
                        onSaveButtonPress = {
                            viewModel.insertReceiptModelToDB()
                            Toast.makeText(context, "Saved Model to Database", Toast.LENGTH_SHORT)
                                .show()
                            viewModel.resetUiState()
                            viewModel.receiptModelUiToExpenseUi()
                            viewModel.resetReceiptModelUiState()
                            viewModel.resetReceiptAnalyzerUiState()
                            navController.popBackStack(
                                route = AppScreen.EDIT_EXPENSE.name,
                                inclusive = false
                            )
                        },
                    )
                }
            }
        }
    }
}