package com.jazzant.expensetracker.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.jazzant.expensetracker.CategoryDropDownMenu
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.database.expense.Expense
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val LABEL_FRACTION = 0.4f
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
fun TextInput(
    label:String,
    value: String,
    onValueChange:(String)->Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true){
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
            enabled = enabled,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun <T> RadioButtons(label:String,
                     radioOptions:List<T>,
                     selectedOption: T,
                     onOptionChange:(T)->Unit,
                     modifier: Modifier = Modifier,
                     radioText: (T) -> String = {it.toString()},
) {
    Row(verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Text(text = label,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(LABEL_FRACTION).padding(end = 5.dp))
        Column(modifier = Modifier.fillMaxWidth().selectableGroup()) {
            radioOptions.forEach { radioOption ->
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected=(radioOption==selectedOption),
                            onClick={onOptionChange(radioOption)},
                            role=Role.RadioButton
                        )
                        .padding(bottom = 5.dp)
                ){
                    RadioButton(
                        selected=(radioOption == selectedOption),
                        onClick=null //done by Row instead so that the text is also clickable
                    )
                    Text(
                        text=radioText(radioOption),
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
                  modifier:Modifier = Modifier){
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
                modifier: Modifier = Modifier){
    Row (
        verticalAlignment = Alignment.CenterVertically
    ){
        Switch(checked = state, onCheckedChange = onStateChanged)
        Text(text, modifier=Modifier.padding(start=5.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(label: String, date: Long, onDateChange: (Long?)->Unit){
    var showDatePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ){
        OutlinedTextField(
            value = convertMillisToDate(date),
            onValueChange = { },
            label = {Text(label)},
            placeholder = {Text("DD/MM/YYYY")},
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.datePickerContentDescription)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(date){
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if(upEvent != null) {showDatePicker = true}
                    }
                }
        )
        if (showDatePicker){
            DatePickerModal(
                date = date,
                onDateSelected = onDateChange,
                onDismiss = {showDatePicker = false}
            )
        }
    }
}

@Composable
fun CategoryInputField(
    newCategoryState: Boolean,
    onNewCategoryStateChange: (Boolean) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    categoryList: List<String>,
){
    if (newCategoryState)
    {
        TextInput(
            label = "Category",
            value = category,
            onValueChange = onCategoryChange,
            enabled = true
        )
    }
    else
    {
        Row (verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Text(text = "Category",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(LABEL_FRACTION).padding(end = 5.dp)
            )
            CategoryDropDownMenu(
                categoryList = categoryList,
                selectedCategory = category,
                onSelectionChange = onCategoryChange,
                innerHorizontalPadding = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray)
            )
        }
    }
    Row {
        Spacer(Modifier.fillMaxWidth(LABEL_FRACTION))
        SwitchField(
            text = stringResource(R.string.newCategorySwitchLabel),
            state = newCategoryState,
            onStateChanged = onNewCategoryStateChange,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    date: Long,
    onDateSelected: (Long?)->Unit,
    onDismiss: ()->Unit
){
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = date)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
            ) { Text(stringResource(R.string.okButton)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancelButton))
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerField(label: String, dateRange: Pair<Long, Long>, onDateRangeChange: (Pair<Long?,Long?>)->Unit){
    var showDateRangePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ){
        OutlinedTextField(
            value = "${convertMillisToDate(dateRange.first)} - ${convertMillisToDate(dateRange.second)}",
            onValueChange = { },
            label = {Text(label)},
            placeholder = {Text("DD/MM/YYYY")},
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = stringResource(R.string.datePickerContentDescription)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(true){
                    awaitEachGesture {
                        awaitFirstDown(pass = PointerEventPass.Initial)
                        val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        if(upEvent != null) {showDateRangePicker = true}
                    }
                }
        )
        if (showDateRangePicker){
            DateRangePickerModal(
                dateRange = dateRange,
                onDateRangeSelected = onDateRangeChange,
                onDismiss = {showDateRangePicker = false},
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    dateRange: Pair<Long?, Long?>,
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = dateRange.first,
        initialSelectedEndDateMillis = dateRange.second,
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            first = dateRangePickerState.selectedStartDateMillis,
                            second = dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) { Text(stringResource(R.string.okButton)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancelButton))
            }
        },
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = { Text(stringResource(R.string.datePickerLabel)) },
            showModeToggle = false,
            modifier = Modifier.fillMaxWidth().height(500.dp).padding(16.dp)
        )
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
                Text(stringResource(R.string.expenseNameLabel)+": " + expense.name, fontSize = TextUnit(4f, TextUnitType.Em))
                Text(stringResource(R.string.expenseCategoryLabel)+": " + expense.category, fontSize = TextUnit(3f, TextUnitType.Em))
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
    val formatter = SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault())
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

fun convertMillisToDate(millis: Long): String{
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}

@Composable
fun AlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = { Icon(icon, contentDescription = "Example Icon") },
        title = { Text(text = dialogTitle) },
        text = { Text(text = dialogText) },
        onDismissRequest = { onDismissRequest() },
        confirmButton = {
            TextButton( onClick = {
                onConfirmation()
                onDismissRequest()
            } )
            { Text("Confirm") }
        },
        dismissButton = {
            TextButton( onClick = { onDismissRequest() })
            { Text("Dismiss") }
        }
    )
}