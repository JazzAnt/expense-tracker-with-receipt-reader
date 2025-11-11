package com.jazzant.expensetracker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.jazzant.expensetracker.ui.theme.ExpenseTrackerWithBillReaderTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ExpenseListScreen(list: List<Expense>, onCardClick: (Expense) -> Unit){
    Box(Modifier.fillMaxSize()){
        LazyColumn () {
            items(list){
                    item ->
                ExpenseCard(item, onCardClick)
            }
        }
        ExpenseSumCard(100f, Modifier.align(Alignment.BottomEnd))
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
        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
            Text("Sum: $%.2f".format(sum),
                fontSize = TextUnit(5f, TextUnitType.Em),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Composable
fun ExpenseCard(expense: Expense, onCardClick: (Expense)->Unit){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color.White)
            .border(width = 1.dp, color = Color.Black),
        onClick = {onCardClick(expense)}
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(5.dp)
        ) {
            DateBox(expense.date)
            Column (
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.5f)
                    .fillMaxHeight()

            ){
                Text("Name: " + expense.name, fontSize = TextUnit(4f, TextUnitType.Em))
                Text("Category: " + expense.category, fontSize = TextUnit(3f, TextUnitType.Em))
            }
            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ){
                Text("$%.2f".format(expense.amount), fontSize = TextUnit(5f, TextUnitType.Em))
            }
        }
    }
}
@Composable
fun DateBox(millis: Long){
    val formatter = SimpleDateFormat("dd/MMM/yyyy", java.util.Locale.getDefault())
    val dateText = formatter.format(Date(millis)).split('/')
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(end = 10.dp)
            .width(40.dp)
            .height(50.dp)
            .drawBehind {

                drawLine(
                    Color.Black,
                    Offset(size.width, 0f),
                    Offset(size.width, size.height),
                    5f

                )
            }
    ){
        dateText.forEach {
            Text(it)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CardPreview() {
    ExpenseCard(
        Expense(100f, "Food", "Burger", System.currentTimeMillis()),
        onCardClick = {}
    )

}

