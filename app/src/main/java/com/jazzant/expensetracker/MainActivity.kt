package com.jazzant.expensetracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.jazzant.expensetracker.ui.theme.ExpenseTrackerWithBillReaderTheme
import kotlinx.coroutines.Job
import java.math.RoundingMode
import java.time.LocalDate
import kotlin.math.exp

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
fun ExpenseInput(expenseViewModel: ExpenseViewModel){
    val (expense, onExpenseChange) = remember { mutableFloatStateOf(0.0f) }
    val (name, onNameChange) = remember { mutableStateOf("") }
    val categories = listOf("Groceries", "Restaurant", "Vending Machine", ADD_CATEGORY)
    val (category, onCategoryChange) = remember { mutableStateOf(categories[0]) }
    val (newCategory, onNewCategoryChange) = remember { mutableStateOf("") }
    val (tip, onTipChange) = remember { mutableFloatStateOf(0.0f) }
    val (tipping, onTippingChange) = remember { mutableStateOf(false) }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize()
            .padding(top = 20.dp, end = 10.dp, bottom = 20.dp, start = 10.dp)
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(innerPadding)
        ) {
            //Expense Input
            NumberInput(
                label = "Expense Amount:",
                value = expense,
                onValueChange = onExpenseChange,
                modifier = Modifier.fillMaxWidth()
            )

            TextInput(
                label = "Expense Name:",
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth()
            )

            //Category Selector
            RadioButtons(
                label = "Expense Category:",
                radioOptions = categories,
                selectedOption = category,
                onOptionChange = onCategoryChange,
                modifier = Modifier.fillMaxWidth()
            )
            if(category == ADD_CATEGORY){
                TextInput(
                    label = "",
                    value = newCategory,
                    onValueChange = onNewCategoryChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            //Tip Checkbox

            CheckBoxField(
                text = "Add tip?",
                state = tipping,
                onStateChanged = onTippingChange,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            if(tipping){
                NumberInput(
                    label = "Tip Amount",
                    value = tip,
                    onValueChange = onTipChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    if(
                        expense < 0 ||
                        tip < 0 ||
                        name.isBlank() ||
                        (category == ADD_CATEGORY && newCategory.isBlank())
                    ){
                        Toast.makeText(context,"ERROR: Invalid input values", Toast.LENGTH_LONG).show()
                    }
                    else {
                        val chosenCategory: String = if(category == ADD_CATEGORY){
                            newCategory.trim()
                        } else {
                            category
                        }

                        val newExpense: Expense = Expense(
                            amount = expense + tip,
                            category = chosenCategory,
                            name = name,
                            date = LocalDate.now()
                        )
                        expenseViewModel.insert(newExpense)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun NumberInput(label:String, value: Float,
                onValueChange:(Float)->Unit,
                modifier: Modifier = Modifier){
    var temp: Float?;
    Row (verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Text(text = label,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(LABEL_FRACTION).padding(end = 5.dp))
        TextField(
            value = value.toString(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            onValueChange = {
                temp = it.toFloatOrNull()
                if(temp == null || temp!! < 0){
                    temp = value
                }
                else {
                    temp = temp!!.toBigDecimal().setScale(2, RoundingMode.DOWN).toFloat()
                    onValueChange(temp!!)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun TextInput(label:String, value: String,
                onValueChange:(String)->Unit,
                modifier: Modifier = Modifier){
    Row (verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Text(text = label,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(LABEL_FRACTION).padding(end = 5.dp))
        TextField(
            value = value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RadioButtons(label:String,
                radioOptions:List<String>,
                selectedOption:String,
                onOptionChange:(String)->Unit,
                modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Text(text = label,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(LABEL_FRACTION).padding(end = 5.dp))
        Column(modifier = Modifier.fillMaxWidth().selectableGroup()) {
            radioOptions.forEach { text ->
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected=(text==selectedOption),
                            onClick={onOptionChange(text)},
                            role=Role.RadioButton
                        )
                        .padding(bottom = 5.dp)
                ){
                    RadioButton(
                        selected=(text == selectedOption),
                        onClick=null //done by Row instead so that the text is also clickable
                    )
                    Text(
                        text=text,
                    )
                }
            }
        }
    }
}

@Composable
fun CheckBoxField(text:String,
                  state: Boolean,
                  onStateChanged:(Boolean)->Unit,
                  modifier:Modifier=Modifier){
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Checkbox(checked=state, onCheckedChange = onStateChanged)
        Text(text)
    }
}

@Composable
fun SwitchField(text:String,
                state: Boolean,
                onStateChanged: (Boolean) -> Unit,
                modifier: Modifier=Modifier){
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Switch(checked = state, onCheckedChange = onStateChanged)
        Text(text, modifier=Modifier.padding(start=5.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExpenseTrackerWithBillReaderTheme {
    }
}