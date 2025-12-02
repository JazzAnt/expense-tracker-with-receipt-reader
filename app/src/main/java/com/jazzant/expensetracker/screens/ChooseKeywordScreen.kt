package com.jazzant.expensetracker.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.ui.DescriptionText
import com.jazzant.expensetracker.ui.ErrorText
import com.jazzant.expensetracker.ui.HeaderText
import com.jazzant.expensetracker.ui.NextButton
import com.jazzant.expensetracker.ui.QuestionText
import com.jazzant.expensetracker.ui.RadioButtons
import com.jazzant.expensetracker.ui.StandardVerticalSpacer
import com.jazzant.expensetracker.ui.SwitchField
import com.jazzant.expensetracker.ui.TextInput

@Composable
fun ChooseKeywordScreen(
    switchState: Boolean,
    onSwitchStateChanged: (Boolean) -> Unit,
    textBlockList: List<String>,
    receiptDisplay: AnnotatedString,
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
                .verticalScroll(rememberScrollState())
        ) {
            HeaderText(stringResource(R.string.chooseKeyword_header))
            StandardVerticalSpacer()
            DescriptionText(stringResource(R.string.chooseKeyword_description))
            StandardVerticalSpacer()

            QuestionText(stringResource(R.string.chooseKeyword_radioButtonLabel))
            StandardVerticalSpacer()
            SwitchField(
                stringResource(R.string.chooseKeyword_switchLabel),
                state = switchState,
                onStateChanged = onSwitchStateChanged,
                leftSpacerFraction = 0.05f
            )
            StandardVerticalSpacer()
            if (switchState) {
                TextInput(
                    stringResource(R.string.chooseKeyword_textInputLabel),
                    value = keyword,
                    onValueChange = onKeywordChange,
                    labelFraction = 0.25f
                )
                StandardVerticalSpacer(multiplier = 1.5f)
                DisplayReceipt(receiptDisplay, modifier = Modifier.padding(horizontal = 12.dp))
                StandardVerticalSpacer(multiplier = 2.5f)
            } else {
                RadioButtons(
                    "",
                    textBlockList,
                    selectedOption = keyword,
                    onOptionChange = onKeywordChange,
                    labelFraction = 0.05f
                )
            }
        }
        if (invalidInput) {
            ErrorText(stringResource(R.string.chooseKeyword_invalidKeywordText),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        else {
            NextButton(onNextButtonPress, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}

@Composable
fun DisplayReceipt(receipt: AnnotatedString, modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .wrapContentSize()
            .background(Color.White)
            .border(2.dp, Color.Black)
    ) {
        Text(
            text = receipt,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            textAlign = TextAlign.Justify,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxSize().padding(5.dp),
        )
    }
}