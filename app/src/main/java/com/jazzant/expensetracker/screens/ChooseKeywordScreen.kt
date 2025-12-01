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
import com.jazzant.expensetracker.ui.RadioButtons
import com.jazzant.expensetracker.ui.SwitchField
import com.jazzant.expensetracker.ui.TextInput

@Composable
fun ChooseKeywordScreen(
    switchState: Boolean,
    onSwitchStateChanged: (Boolean) -> Unit,
    textBlockList: List<String>,
    keyword: String,
    onKeywordChange: (String) -> Unit,
    invalidInput: Boolean,
    onNextButtonPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Text(stringResource(R.string.chooseKeyword_header))
        Text(stringResource(R.string.chooseKeyword_description))

        SwitchField(
            stringResource(R.string.chooseKeyword_switchLabel),
            state = switchState,
            onStateChanged = onSwitchStateChanged
        )
        if (switchState)
        {
            TextInput(
                stringResource(R.string.chooseKeyword_textInputLabel),
                value = keyword,
                onValueChange = onKeywordChange
            )
        }
        else
        {
            RadioButtons(
                stringResource(R.string.chooseKeyword_radioButtonLabel),
                textBlockList,
                selectedOption = keyword,
                onOptionChange = onKeywordChange
            )
        }

        if (invalidInput)
        {
            Text(stringResource(R.string.chooseKeyword_invalidKeywordText))
        }
        else {
            Button(onClick = onNextButtonPress) {
                Text(stringResource(R.string.nextButton))
            }
        }
    }
}