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
    Editor,
    ExpenseList
}

@Composable
fun ExpenseApp(
    expenseViewModel: ExpenseViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
){
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppScreen.Editor.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ){
            composable(route = AppScreen.Editor.name) {
                ExpenseEditorScreen(expenseViewModel)
            }
            composable(route = AppScreen.ExpenseList.name) {
                val list by expenseViewModel.expenseList.collectAsState()
                ExpenseListScreen(list)
            }
        }
    }
}