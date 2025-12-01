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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
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
import kotlin.Float

private const val DEFAULT_FRACTION = 0.4f
@Composable
fun NumberInput(label:String, value: Float,
                onValueChange:(Float)->Unit,
                modifier: Modifier = Modifier,
                labelFraction: Float = DEFAULT_FRACTION,
){
    var temp: Float?;
    Row (verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Text(text = label,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(labelFraction).padding(end = 5.dp))
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
    enabled: Boolean = true,
    labelFraction: Float = DEFAULT_FRACTION,
){
    Row (verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Text(text = label,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(labelFraction).padding(end = 5.dp))
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
                     labelFraction: Float = DEFAULT_FRACTION,
) {
    Row(verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ){
        Text(text = label,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(labelFraction).padding(end = 5.dp))
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
                  modifier: Modifier = Modifier,
                  leftSpacerFraction: Float = DEFAULT_FRACTION,
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ){
        Spacer(Modifier.fillMaxWidth(leftSpacerFraction))
        Checkbox(checked=state, onCheckedChange = onStateChanged)
        Text(text)
    }
}

@Composable
fun SwitchField(text:String,
                state: Boolean,
                onStateChanged: (Boolean) -> Unit,
                modifier: Modifier = Modifier,
                leftSpacerFraction: Float = DEFAULT_FRACTION,
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ){
        Spacer(Modifier.fillMaxWidth(leftSpacerFraction))
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
    leftSpacerFraction: Float = DEFAULT_FRACTION,
){
    if (newCategoryState)
    {
        TextInput(
            label = "Category",
            value = category,
            onValueChange = onCategoryChange,
            enabled = true,
            labelFraction = leftSpacerFraction,
        )
    }
    else if (categoryList.isEmpty())
    {
        Text(stringResource(R.string.emptyCategoryListAlert))
    }
    else
    {
        Row (verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ){
            Text(text = "Category",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(leftSpacerFraction).padding(end = 5.dp)
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
        SwitchField(
            text = stringResource(R.string.newCategorySwitchLabel),
            state = newCategoryState,
            onStateChanged = onNewCategoryStateChange,
            leftSpacerFraction = leftSpacerFraction,
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
            .height(64.dp)
            .background(Color.White),
        onClick = {onCardClick(expense)}
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(5.dp)
        ) {
            DateBox(expense.date)
            Column (
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.64f)
                    .fillMaxHeight()

            ){
                Text(stringResource(R.string.expenseNameLabel)+": " + expense.name, fontSize = TextUnit(3.6f, TextUnitType.Em), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(stringResource(R.string.expenseCategoryLabel)+": " + expense.category, fontSize = TextUnit(3f, TextUnitType.Em), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ){
                Text("$%.2f".format(expense.amount), fontSize = TextUnit(4.4f, TextUnitType.Em))
            }
        }
    }
}

@Composable
fun ExpenseCard(
    name: String,
    category: String,
    amount: Float,
    date: Long,
    onCardClick: ()->Unit,
){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White),
        onClick = {onCardClick()}
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(5.dp)
        ) {
            DateBox(date)
            Column (
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.64f)
                    .fillMaxHeight()

            ){
                Text(stringResource(R.string.expenseNameLabel)+": " + name, fontSize = TextUnit(3.6f, TextUnitType.Em), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(stringResource(R.string.expenseCategoryLabel)+": " + category, fontSize = TextUnit(3f, TextUnitType.Em), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ){
                Text("$%.2f".format(amount), fontSize = TextUnit(4.4f, TextUnitType.Em))
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
            .padding(end = 12.dp)
            .width(60.dp)
            .height(64.dp)
            .drawBehind {
                drawLine(
                    Color.Black,
                    Offset(size.width, 0f),
                    Offset(size.width, size.height),
                    5f
                )
            }
    ){
        Text(dateText[0], fontSize = TextUnit(4.8f, TextUnitType.Em))
        Text(dateText[1], fontSize = TextUnit(3.0f, TextUnitType.Em))
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

@Composable
fun StandardButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String = "",
    width: Dp = 640.dp,
){
    Button(onClick = onClick, modifier = modifier.width(width))
    {
        Text(
            text = text,
            fontSize = TextUnit(24f, TextUnitType.Sp),
        )
    }
}

@Composable
fun NextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Filled.ArrowForward,
    text: String = stringResource(R.string.nextButton),
    width: Dp = 210.dp,

){
    ExtendedFloatingActionButton(
        onClick = onClick,
        modifier = modifier.width(width)
    ) {
        Text(
            text = text,
            fontSize = TextUnit(24f, TextUnitType.Sp),
        )
        Spacer(Modifier.width(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = text
        )
    }
}

@Composable
fun HeaderText(text: String, modifier: Modifier = Modifier){
    Text(
        text = text,
        fontSize = TextUnit(30f, TextUnitType.Sp),
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun DescriptionText(text: String, modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .border(1.dp, Color.Black)
    ) {
        Text(
            text = text,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            textAlign = TextAlign.Justify,
            modifier = Modifier.fillMaxWidth().padding(5.dp).align(Alignment.Center)
        )
    }

}

@Composable
fun StandardVerticalSpacer(
    spacing: Float = 12f,
    multiplier: Float = 1f,
){
    Spacer(Modifier.height((spacing * multiplier).dp))
}

@Composable
fun ErrorText(text: String, modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Red)
            .border(1.dp, Color.Black)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning Icon",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                fontSize = TextUnit(18f, TextUnitType.Sp),
                textAlign = TextAlign.Justify,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}
