package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.ui.CheckBoxField
import com.jazzant.expensetracker.ui.TextInput

@Composable
fun ChooseNameScreen(
    checkBoxState: Boolean,
    onCheckBoxStateChange: (Boolean) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    invalidInput: Boolean,
    onNextButtonPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Text(stringResource(R.string.chooseName_header))
        Text(stringResource(R.string.chooseName_description))
        CheckBoxField(
            text = stringResource(R.string.chooseName_checkBoxLabel),
            state = checkBoxState,
            onStateChanged = onCheckBoxStateChange
        )
        TextInput(
            label = stringResource(R.string.chooseName_textInputLabel),
            value = name,
            onValueChange = onNameChange,
            enabled = !checkBoxState
        )
        if (invalidInput)
        {
            Text(stringResource(R.string.chooseName_invalidNameText))
        }
        else
        {
            Button(onClick = onNextButtonPress) {
                Text(stringResource(R.string.nextButton))
            }
        }
    }
}