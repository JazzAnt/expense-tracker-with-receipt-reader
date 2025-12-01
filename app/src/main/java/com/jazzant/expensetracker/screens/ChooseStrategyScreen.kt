package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.analyzer.Strategy
import com.jazzant.expensetracker.ui.DescriptionText
import com.jazzant.expensetracker.ui.HeaderText
import com.jazzant.expensetracker.ui.NextButton
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            HeaderText(stringResource(R.string.chooseStrategy_header))
            DescriptionText(stringResource(R.string.chooseStrategy_description))
            RadioButtons(
                label = stringResource(R.string.chooseStrategy_radioButtonLabel),
                radioOptions = strategyList,
                selectedOption = strategy,
                onOptionChange = onStrategyChange,
                radioText = { strategyNames[it.ordinal] }
                //TODO: Modify RadioButtons to allow adding description text to put strategyDescriptions
            )
            if (invalidInput) {
                Text(stringResource(R.string.chooseStrategy_invalidStrategyLabel))
            }
        }
        if (!invalidInput) {
            NextButton(onSaveButtonPress,
                modifier = Modifier.align(Alignment.BottomEnd),
                text = "Save",
                icon = Icons.Default.Done
            )
        }
    }
}