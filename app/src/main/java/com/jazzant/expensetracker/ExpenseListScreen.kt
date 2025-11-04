package com.jazzant.expensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpenseCard(expense: Expense){
    Card (
        modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp)
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp)
        ) {
            Column (
                modifier = Modifier.fillMaxWidth(0.5f)
            ){
                Text("Name: " + expense.name)
                Text("Category: " + expense.category)
            }
            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ){
                Text("$%.2f".format(expense.amount))
            }
        }
    }
}
@Composable
fun ShowExpenses(list: List<Expense>){
    Text("Expenses:")
    LazyColumn (
    ) {
        items(list){
                item ->
            ExpenseCard(item)
        }

    }
}