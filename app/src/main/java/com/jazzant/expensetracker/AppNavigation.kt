package com.jazzant.expensetracker

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
                //TEMP REMOVED TO UPDATE ARGUMENTS
            }
            composable(route = AppScreen.EXPENSE_LIST.name) {
                val list by viewModel.expenseList.collectAsState()
                ExpenseListScreen(list)
            }
        }
    }
}