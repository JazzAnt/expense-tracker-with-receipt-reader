package com.jazzant.expensetracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jazzant.expensetracker.ui.theme.ExpenseTrackerWithBillReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerWithBillReaderTheme {
                MainContent()
            }
        }
    }
}

@Composable
fun MainContent(){
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        Column {
            //Expense Input
            val (expense, onExpenseChange) = remember { mutableStateOf<Number>(0) }
            NumberInput(expense, onExpenseChange)

            //Category Selector
            val categories = listOf<String>("Groceries", "Restaurant", "Vending Machine")
            val (category, onCategoryChange) = remember { mutableStateOf(categories[0]) }
            RadioButtons(categories,category,onCategoryChange)

            //Tip Checkbox
            val (tipping, onTipChange) = remember { mutableStateOf(false) }
            CheckBoxField("Add Tip?", tipping, onTipChange)
            SwitchField("Add Tip?", tipping, onTipChange)

            //For Testing
            Card (
                modifier = Modifier.padding(innerPadding),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Text(expense.toDouble().toString())
                Text(category)
                Text("Tipping is $tipping")
            }

            val context = LocalContext.current
            Button(
                onClick = {
                    Toast.makeText(context,"Saved to Database", Toast.LENGTH_LONG).show()
                }
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun NumberInput(value: Number, onValueChange:(Number)->Unit,
                modifier: Modifier = Modifier){
    var temp: Number?;
    TextField(
        value = value.toString(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        onValueChange = {
            temp = it.toDoubleOrNull()
            if(temp == null){
                temp = 0
            }
            onValueChange(temp!!)
        }
    )
}

@Composable
fun RadioButtons(radioOptions:List<String>, selectedOption:String, onOptionChange:(String)->Unit,
                 modifier: Modifier = Modifier) {
    Column(Modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .selectable(
                        selected=(text==selectedOption),
                        onClick={onOptionChange(text)},
                        role=Role.RadioButton
                    )
            ){
                RadioButton(
                    selected=(text == selectedOption),
                    onClick=null //done by Row instead
                )
                Text(
                    text=text,
                )
            }
        }
    }
}

@Composable
fun CheckBoxField(text:String, state: Boolean, onStateChanged:(Boolean)->Unit,
             modifier:Modifier=Modifier){
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Checkbox(checked=state, onCheckedChange = onStateChanged)
        Text(text)
    }
}

@Composable
fun SwitchField(text:String, state: Boolean, onStateChanged: (Boolean) -> Unit,
                modifier: Modifier=Modifier){
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Switch(checked = state, onCheckedChange = onStateChanged)
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExpenseTrackerWithBillReaderTheme {
        MainContent()
    }
}