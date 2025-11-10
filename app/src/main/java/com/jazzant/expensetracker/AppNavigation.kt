package com.jazzant.expensetracker

import android.content.Context
import android.graphics.drawable.Icon
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

enum class AppScreen(){
    EXPENSE_LIST,
    EDIT_EXPENSE
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
        backStackEntry?.destination?.route?: AppScreen.EXPENSE_LIST.name
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
                        name = "list",
                        route = AppScreen.EXPENSE_LIST.name,
                        icon = Icons.AutoMirrored.Filled.List
                    ),

                    BottomNavItem(
                        name = "Add",
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
        val context = LocalContext.current
        val expenseState by viewModel.expenseState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = AppScreen.EXPENSE_LIST.name,
            modifier = Modifier
                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ){
            composable(route = AppScreen.EXPENSE_LIST.name) {
                val list by viewModel.expenseList.collectAsState()

                ExpenseListScreen(list, onCardClick = { expense ->
                    viewModel.expenseEntityToUi(expense)
                    navController.navigate(route = AppScreen.EDIT_EXPENSE.name)
                })
            }
            composable(route = AppScreen.EDIT_EXPENSE.name) {
                ExpenseEditorScreen(
                    categoryList = listOf("A", "B", "C") + stringResource(R.string.add_new_category),
                    amount = expenseState.amount,
                    onAmountChange = {viewModel.setAmount(it)},
                    name = expenseState.name,
                    onNameChange = {viewModel.setName(it)},
                    category = expenseState.category,
                    onCategoryChange = {viewModel.setCategory(it)},
                    newCategory = expenseState.newCategory,
                    onNewCategoryChange = {viewModel.setNewCategory(it)},
                    tipping = expenseState.tipping,
                    onTippingChange = {viewModel.setTipping(it)},
                    tip = expenseState.tip,
                    onTipChange = {viewModel.setTip(it)},
                    date = expenseState.date,
                    onDateChange = {viewModel.setDate(it?: expenseState.date)},
                    onSaveButtonPress = {
                        //TODO: Add validator for Expense Contents
                        viewModel.insertExpenseToDB()
                        Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show()
                        //TODO: Add method to navigate to main menu once that's created
                    }
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
                    contentDescription = "Home",
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