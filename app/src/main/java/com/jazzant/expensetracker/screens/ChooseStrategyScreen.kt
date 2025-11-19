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
fun ChooseStrategyScreen(
    strategyList: List<String>,
    strategy: String,
    onStrategyChange: (String) -> Unit,
    invalidInput: Boolean,
    onNextButtonPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxSize()) {
        Text(stringResource(R.string.chooseStrategy_header))
        Text(stringResource(R.string.chooseStrategy_description))
        RadioButtons(
            label = stringResource(R.string.chooseStrategy_radioButtonLabel),
            radioOptions = strategyList,
            selectedOption = strategy,
            onOptionChange = onStrategyChange
        )
        if (invalidInput)
        {
            Text(stringResource(R.string.chooseStrategy_invalidStrategyLabel))
        }
        else
        {
            Button(onClick = onNextButtonPress) {
                Text(stringResource(R.string.nextButton))
            }
        }
    }

}