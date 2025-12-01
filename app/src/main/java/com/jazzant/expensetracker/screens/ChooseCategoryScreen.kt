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
import com.jazzant.expensetracker.ui.CategoryInputField
import com.jazzant.expensetracker.ui.DescriptionText
import com.jazzant.expensetracker.ui.HeaderText
import com.jazzant.expensetracker.ui.NextButton
import com.jazzant.expensetracker.ui.RadioButtons
import com.jazzant.expensetracker.ui.StandardVerticalSpacer
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
            HeaderText(stringResource(R.string.chooseCategory_header))
            StandardVerticalSpacer()
            DescriptionText(stringResource(R.string.chooseCategory_description))
            StandardVerticalSpacer()

            CategoryInputField(
                newCategoryState = newCategorySwitch,
                onNewCategoryStateChange = onNewCategorySwitchChange,
                category = category,
                onCategoryChange = onCategoryChange,
                categoryList = categoryList
            )

            StandardVerticalSpacer()
            if (invalidInput) {
                Text(stringResource(R.string.chooseCategory_invalidAmountLabel))
            }
        }
        if (!invalidInput) {
            NextButton(onNextButtonPress, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}
