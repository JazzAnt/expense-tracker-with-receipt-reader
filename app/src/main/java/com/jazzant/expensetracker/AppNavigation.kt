package com.jazzant.expensetracker

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlin.math.exp

enum class AppScreen(){
    EXPENSE_EDITOR,
    EXPENSE_LIST
}

@Composable
fun ExpenseApp(
    viewModel: ExpenseViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
){
    Scaffold { innerPadding ->
        val context = LocalContext.current
        val expenseState by viewModel.expenseState.collectAsState()
        NavHost(
            navController = navController,
            startDestination = AppScreen.EXPENSE_EDITOR.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ){
            composable(route = AppScreen.EXPENSE_EDITOR.name) {
                val list by viewModel.categoryList.collectAsState()
                viewModel.resetUiState()
                if(list.isEmpty()){
                    viewModel.setCategory(SpecialCategories.ADD_NEW_CATEGORY.name)
                } else {
                    viewModel.setCategory(list[0])
                }
                ExpenseEditorScreen(
                    categoryList = list + SpecialCategories.ADD_NEW_CATEGORY.name,
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
                        viewModel.insertExpenseUiToDb()
                        Toast.makeText(context, "Successfully added to Database", Toast.LENGTH_SHORT).show()
                        //TODO: Add method to navigate to main menu once that's created
                    }
                )
            }
            composable(route = AppScreen.EXPENSE_LIST.name) {
                val list by viewModel.expenseList.collectAsState()
                ExpenseListScreen(list)
            }
        }
    }
}