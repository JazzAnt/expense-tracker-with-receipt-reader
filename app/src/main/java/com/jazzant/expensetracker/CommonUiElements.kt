package com.jazzant.expensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.math.RoundingMode

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