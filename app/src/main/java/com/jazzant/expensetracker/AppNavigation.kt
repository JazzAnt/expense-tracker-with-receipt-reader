package com.jazzant.expensetracker

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jazzant.expensetracker.analyzer.Strategy
import com.jazzant.expensetracker.analyzer.evaluateAllPossibleStrategies
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
import com.jazzant.expensetracker.screens.TextAnalyzerScreen
import com.jazzant.expensetracker.screens.TextRecognizerScreen
import com.jazzant.expensetracker.viewmodel.ExpenseViewModel

enum class AppScreen(){
    HOME_SCREEN,
    EDIT_EXPENSE,
    REQUEST_CAMERA_PERMISSION,
    CAMERA_PREVIEW,
    TEXT_RECOGNIZER,
    TEXT_ANALYZER,
    CHOOSE_KEYWORD,
    CHOOSE_NAME,
    CHOOSE_AMOUNT,
    CHOOSE_CATEGORY,
    ANALYZING_STRATEGY,
    CHOOSE_STRATEGY
}

@Composable
fun ExpenseApp(
    viewModel: ExpenseViewModel = viewModel(),
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
){
    viewModel.initializeViewModel(context)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route?: AppScreen.HOME_SCREEN.name
    )
    Scaffold(
        topBar = {
            TopNavBar(
                currentRoute = currentScreen.name
            )
        },
        bottomBar = {
            BottomNavBar(
                items = listOf(
                    BottomNavItem(
                        name = stringResource(R.string.homeNavIcon),
                        route = AppScreen.HOME_SCREEN.name,
                        icon = Icons.Default.Home
                    ),

                    BottomNavItem(
                        name = "camera",
                        route = AppScreen.REQUEST_CAMERA_PERMISSION.name,
                        icon = Icons.Default.ShoppingCart
                    ),

                    BottomNavItem(
                        name = stringResource(R.string.addExpenseNavIcon),
                        route = AppScreen.EDIT_EXPENSE.name,
                        icon = Icons.Default.Add,
                        floating = true,
                        onNavButtonClick = {
                            viewModel.resetUiState()
                        }
                    )
                ),
                onItemClick = {navController.navigate(it.route)},
                currentRoute = currentScreen.name
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.HOME_SCREEN.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
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
                    onSaveButtonPress = {
                        val error = viewModel.checkForErrorsInUiState(context)
                        if (error != null)
                        {
                            Toast.makeText(
                                context,
                                error,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        else
                        {
                            viewModel.insertExpenseToDB()
                            Toast.makeText(
                                context,
                                context.getString(R.string.saveExpenseToast),
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false)
                        }

                    }
                )
            }
            composable(route = AppScreen.REQUEST_CAMERA_PERMISSION.name){
                CameraPermissionScreen(
                    onCameraPermissionGranted = { navController.navigate(AppScreen.CAMERA_PREVIEW.name) }
                )
            }
            composable(route = AppScreen.CAMERA_PREVIEW.name) {
                CameraPreviewScreen(
                    onImageCapture = { imageProxy ->
                        viewModel.resetReceiptAnalyzerUiState()
                        viewModel.recognizeText(imageProxy.toBitmap())
                        navController.navigate(AppScreen.TEXT_RECOGNIZER.name)
                    }
                )
            }
            composable(route = AppScreen.TEXT_RECOGNIZER.name) {
                val receiptAnalyzerState by viewModel.receiptAnalyzerUiState.collectAsStateWithLifecycle()
                val receiptModelList by viewModel.receiptModelList.collectAsStateWithLifecycle()
                TextRecognizerScreen(
                    recognizedText = receiptAnalyzerState.recognizedText,
                    bitmap = receiptAnalyzerState.capturedBitmap!!,
                    onTextRecognized = {
                        viewModel.findKeyword(receiptModelList)
                        navController.navigate(AppScreen.TEXT_ANALYZER.name)
                    },
                    onRetakeImageButtonPress = { navController.popBackStack(route = AppScreen.CAMERA_PREVIEW.name, inclusive = false) },
                    onCancelButtonPress = { navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false) }
                )
            }
            composable(route = AppScreen.TEXT_ANALYZER.name) {
                val receiptAnalyzerState by viewModel.receiptAnalyzerUiState.collectAsStateWithLifecycle()
                val receiptModelList by viewModel.receiptModelList.collectAsStateWithLifecycle()
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
                        Toast.makeText(context, "Selected most likely values", Toast.LENGTH_SHORT).show()
                        navController.navigate(AppScreen.EDIT_EXPENSE.name)
                                                        },
                    onUseAnalyzedExpenseButtonPress = {
                        val model = receiptModelList[receiptAnalyzerState.receiptModelIndex]
                        val expense = viewModel.parseRecognizedTextFromModel(model)
                        viewModel.expenseEntityToUi(expense)
                        navController.navigate(AppScreen.EDIT_EXPENSE.name)
                    },
                    onEditAnalyzedExpenseButtonPress = {
                        val model = receiptModelList[receiptAnalyzerState.receiptModelIndex]
                        val expense = viewModel.parseRecognizedTextFromModel(model)
                        viewModel.expenseEntityToUi(expense)
                        viewModel.insertExpenseToDB()
                        navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false)
                    },
                    analyzedExpense = null, //TODO
                    onRetakeImageButtonPress = { navController.popBackStack(route = AppScreen.CAMERA_PREVIEW.name, inclusive = false) },
                    onCancelButtonPress = { navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false) }
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
                            receiptAnalyzerState.strategies[it] ?:1
                        )
                        viewModel.validateReceiptModelStrategy()
                    },
                    //TODO: Add high order function to manipulate RadioText
                    invalidInput = receiptModelState.invalidInput,
                    onNextButtonPress = {
                        viewModel.insertReceiptModelToDB()
                        Toast.makeText(context, "Saved new model to database", Toast.LENGTH_LONG).show()
                        navController.popBackStack(route = AppScreen.HOME_SCREEN.name, inclusive = false)
                        viewModel.resetUiState()
                        viewModel.receiptModelUiToExpenseUi()
                        viewModel.resetReceiptModelUiState()
                        navController.navigate(AppScreen.EDIT_EXPENSE.name)
                                        },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    currentRoute: String,
    modifier: Modifier = Modifier
){
    TopAppBar(
        title = { Text(currentRoute) },
        modifier = modifier,
        navigationIcon = {

            IconButton(
                onClick = {
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menuNavIcon),
                    tint = Color.Gray
                )
            }
        }
    )
}

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: ImageVector,
    val floating: Boolean = false,
    val onNavButtonClick: () -> Unit = {}
)

@Composable
fun BottomNavBar(
    items: List<BottomNavItem>,
    onItemClick: (BottomNavItem) -> Unit,
    currentRoute: String,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier,
        actions = {
            items.forEach {
                if(it.floating) { return@forEach }
                val selected = it.route == currentRoute
                IconButton(
                    onClick = {
                        it.onNavButtonClick()
                        onItemClick(it)
                              },
                    enabled = !selected
                ) {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.name,
                        tint = if (selected) { Color.Red } else { Color.Gray }
                    )
                }
            }
        },
        floatingActionButton = {
            items.forEach {
                if (!it.floating){ return@forEach }
                FloatingActionButton(
                    onClick = {
                        it.onNavButtonClick()
                        onItemClick(it)
                              },
                    containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                    elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = it.name,
                    )
                }
            }
        }
    )
}