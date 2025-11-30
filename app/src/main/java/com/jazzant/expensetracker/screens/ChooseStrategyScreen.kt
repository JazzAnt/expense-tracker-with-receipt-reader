package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.analyzer.Strategy
import com.jazzant.expensetracker.ui.RadioButtons

@Composable
fun ChooseStrategyScreen(
    strategyList: List<Strategy>,
    strategy: Strategy,
    onStrategyChange: (Strategy) -> Unit,
    invalidInput: Boolean,
    onSaveButtonPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strategyNames = stringArrayResource(R.array.strategyNames)
    val strategyDescriptions = stringArrayResource(R.array.strategyDescriptions)
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxSize()) {
        Text(stringResource(R.string.chooseStrategy_header))
        Text(stringResource(R.string.chooseStrategy_description))
        RadioButtons(
            label = stringResource(R.string.chooseStrategy_radioButtonLabel),
            radioOptions = strategyList,
            selectedOption = strategy,
            onOptionChange = onStrategyChange,
            radioText = { strategyNames[it.ordinal] }
        //TODO: Modify RadioButtons to allow adding description text to put strategyDescriptions
        )
        if (invalidInput)
        {
            Text(stringResource(R.string.chooseStrategy_invalidStrategyLabel))
        }
        else
        {
            Button(onClick = onSaveButtonPress) {
                Text(stringResource(R.string.nextButton))
            }
        }
    }

}