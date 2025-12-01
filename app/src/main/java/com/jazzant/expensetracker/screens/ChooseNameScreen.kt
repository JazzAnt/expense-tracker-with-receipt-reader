package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Box
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
import com.jazzant.expensetracker.ui.DescriptionText
import com.jazzant.expensetracker.ui.ErrorText
import com.jazzant.expensetracker.ui.HeaderText
import com.jazzant.expensetracker.ui.NextButton
import com.jazzant.expensetracker.ui.StandardVerticalSpacer
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            HeaderText(stringResource(R.string.chooseName_header))
            StandardVerticalSpacer()
            DescriptionText(stringResource(R.string.chooseName_description))
            StandardVerticalSpacer()
            CheckBoxField(
                text = stringResource(R.string.chooseName_checkBoxLabel),
                state = checkBoxState,
                onStateChanged = onCheckBoxStateChange,
                leftSpacerFraction = 0.25f
            )
            StandardVerticalSpacer()
            TextInput(
                label = stringResource(R.string.chooseName_textInputLabel),
                value = name,
                onValueChange = onNameChange,
                enabled = !checkBoxState,
                labelFraction = 0.3f
            )
        }
        if (invalidInput) {
            ErrorText(stringResource(R.string.chooseName_invalidNameText),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        else {
            NextButton(onNextButtonPress, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}