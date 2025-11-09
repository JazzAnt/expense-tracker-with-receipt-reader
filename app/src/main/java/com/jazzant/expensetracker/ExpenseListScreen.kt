package com.jazzant.expensetracker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ExpenseListScreen(list: List<Expense>, onCardClick: (Expense) -> Unit){
    LazyColumn () {
        items(list){
            item ->
            ExpenseCard(item, onCardClick)
        }
    }
}
@Composable
fun ExpenseCard(expense: Expense, onCardClick: (Expense)->Unit){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(width = 1.dp, color = Color.Black),
        onClick = {onCardClick(expense)}
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(5.dp)
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