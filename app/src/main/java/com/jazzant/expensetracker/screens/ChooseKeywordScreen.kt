package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.ui.NextButton
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(stringResource(R.string.chooseKeyword_header))
            Text(stringResource(R.string.chooseKeyword_description))

            Text(stringResource(R.string.chooseKeyword_radioButtonLabel))
            SwitchField(
                stringResource(R.string.chooseKeyword_switchLabel),
                state = switchState,
                onStateChanged = onSwitchStateChanged
            )
            if (switchState) {
                TextInput(
                    stringResource(R.string.chooseKeyword_textInputLabel),
                    value = keyword,
                    onValueChange = onKeywordChange
                )
            } else {
                RadioButtons(
                    "",
                    textBlockList,
                    selectedOption = keyword,
                    onOptionChange = onKeywordChange,
                    labelFraction = 0.05f
                )
            }

            if (invalidInput) {
                Text(stringResource(R.string.chooseKeyword_invalidKeywordText))
            }
        }
        if (!invalidInput) {
            NextButton(onNextButtonPress, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}