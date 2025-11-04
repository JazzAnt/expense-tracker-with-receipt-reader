package com.jazzant.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.jazzant.expensetracker.ui.theme.ExpenseTrackerWithBillReaderTheme

const val LABEL_FRACTION = 0.4f
const val ADD_CATEGORY = "Add New Category"
class MainActivity : ComponentActivity() {
    private lateinit var expenseViewModel: ExpenseViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        expenseViewModel = ExpenseViewModel()
        expenseViewModel.setDatabase(this)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerWithBillReaderTheme {
                val list by expenseViewModel.expenseList.collectAsState()
                ShowExpenses(list)
                ExpenseInput(expenseViewModel)
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExpenseTrackerWithBillReaderTheme {
    }
}