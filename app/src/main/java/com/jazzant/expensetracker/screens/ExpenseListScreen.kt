package com.jazzant.expensetracker.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.database.expense.Expense
import com.jazzant.expensetracker.ui.ExpenseCard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpenseListScreen(list: List<Expense>, onCardClick: (Expense) -> Unit, sumOfExpenses: Float){
    Box(Modifier.fillMaxSize()){
        LazyColumn () {
            items(list){
                    item ->
                ExpenseCard(item, onCardClick)
            }
        }
        ExpenseSumCard(sumOfExpenses, Modifier.align(Alignment.BottomEnd))
    }
}

@Composable
fun ExpenseSumCard(sum: Float, modifier: Modifier = Modifier){
    Card(modifier
        .padding(2.dp)
        .border(BorderStroke(1.dp, Color.Black))
        .padding(3.dp)
        .fillMaxWidth()
        .height(50.dp)
        .zIndex(1f)) {
        Row(horizontalArrangement = Arrangement.Start, modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.sumOfExpensesLabel) +": $%.2f".format(sum),
                fontSize = TextUnit(5f, TextUnitType.Em),
                fontWeight = FontWeight.Bold
            )
        }
    }
}