package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.ui.RadioButtons

@Composable
fun ChooseAmountScreen(
    amountList: List<Float>,
    amount: Float,
    onAmountChange: (Float) -> Unit,
    onNextButtonPress: () -> Unit,
    invalidInput: Boolean,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxSize()) {
        Text(stringResource(R.string.chooseAmount_header))
        Text(stringResource(R.string.chooseAmount_description))
        RadioButtons(
            label = stringResource(R.string.chooseAmount_radioButtonLabel),
            radioOptions = amountList,
            selectedOption = amount,
            onOptionChange = onAmountChange,
            radioText = {"$%.2f".format(it)}
            //TODO: Make '$' non-static and allow user to change currency
        )
        if (invalidInput)
        {
            Text(stringResource(R.string.chooseAmount_invalidAmountLabel))
        }
        else
        {
            Button(onClick = onNextButtonPress) {
                Text(stringResource(R.string.nextButton))
            }
        }
    }
}