package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
fun ChooseCategoryScreen(
    newCategorySwitch: Boolean,
    onNewCategorySwitchChange: (Boolean) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    categoryList: List<String>,
    onNextButtonPress: () -> Unit,
    invalidInput: Boolean,
    modifier: Modifier = Modifier,
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
            Text(stringResource(R.string.chooseCategory_header))
            Text(stringResource(R.string.chooseCategory_description))

            SwitchField(
                text = stringResource(R.string.newCategorySwitchLabel),
                state = newCategorySwitch,
                onStateChanged = onNewCategorySwitchChange
            )
            if (newCategorySwitch) {
                TextInput(
                    label = stringResource(R.string.newCategoryInputLabel),
                    value = category,
                    onValueChange = onCategoryChange,
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (categoryList.isEmpty()) {
                Text(stringResource(R.string.emptyCategoryListAlert))
            } else {
                RadioButtons(
                    label = stringResource(R.string.categorySelectorLabel),
                    radioOptions = categoryList,
                    selectedOption = category,
                    onOptionChange = onCategoryChange,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (invalidInput) {
                Text(stringResource(R.string.chooseCategory_invalidAmountLabel))
            }
        }
        if (!invalidInput) {
            NextButton(onNextButtonPress, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}
