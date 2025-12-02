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
import com.jazzant.expensetracker.ui.CategoryInputField
import com.jazzant.expensetracker.ui.DescriptionText
import com.jazzant.expensetracker.ui.ErrorText
import com.jazzant.expensetracker.ui.HeaderText
import com.jazzant.expensetracker.ui.NextButton
import com.jazzant.expensetracker.ui.StandardVerticalSpacer

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
                .verticalScroll(rememberScrollState())
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

        }
        if (invalidInput) {
            ErrorText(stringResource(R.string.chooseCategory_invalidCategoryLabel),
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
        else {
            NextButton(onNextButtonPress, modifier = Modifier.align(Alignment.BottomEnd))
        }
    }
}
