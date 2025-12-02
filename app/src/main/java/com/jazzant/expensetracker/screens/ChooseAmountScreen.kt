package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.ui.DescriptionText
import com.jazzant.expensetracker.ui.ErrorText
import com.jazzant.expensetracker.ui.HeaderText
import com.jazzant.expensetracker.ui.NextButton
import com.jazzant.expensetracker.ui.QuestionText
import com.jazzant.expensetracker.ui.RadioButtons
import com.jazzant.expensetracker.ui.StandardVerticalSpacer

@Composable
fun ChooseAmountScreen(
    amountList: List<Float>,
    amount: Float,
    onAmountChange: (Float) -> Unit,
    onNextButtonPress: () -> Unit,
    invalidInput: Boolean,
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
                .verticalScroll(rememberScrollState())
        ) {
            HeaderText(stringResource(R.string.chooseAmount_header))
            StandardVerticalSpacer()
            DescriptionText(stringResource(R.string.chooseAmount_description))
            StandardVerticalSpacer()
            QuestionText(stringResource(R.string.chooseAmount_question))
            StandardVerticalSpacer()
            RadioButtons(
                label = stringResource(R.string.chooseAmount_radioButtonLabel),
                radioOptions = amountList,
                selectedOption = amount,
                onOptionChange = onAmountChange,
                radioText = { "$%.2f".format(it) }
                //TODO: Make '$' non-static and allow user to change currency
            )
        }
        if (invalidInput) {
            ErrorText(stringResource(R.string.chooseAmount_invalidAmountLabel),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        else {
            NextButton(onNextButtonPress, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}