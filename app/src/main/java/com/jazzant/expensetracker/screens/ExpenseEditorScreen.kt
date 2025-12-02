package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.jazzant.expensetracker.ui.CheckBoxField
import com.jazzant.expensetracker.ui.DatePickerField
import com.jazzant.expensetracker.ui.ExpenseCard
import com.jazzant.expensetracker.ui.NumberInput
import com.jazzant.expensetracker.ui.StandardVerticalSpacer
import com.jazzant.expensetracker.ui.TextInput

@Composable
fun ExpenseEditorScreen(
    categoryList: List<String>,
    amount: Float,
    onAmountChange: (Float) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    newCategorySwitch: Boolean,
    onNewCategorySwitchChange: (Boolean) -> Unit,
    tipping: Boolean,
    onTippingChange: (Boolean) -> Unit,
    tip: Float,
    onTipChange: (Float) -> Unit,
    date: Long,
    onDateChange: (Long?) -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier.padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        ExpenseCard(
            name = name,
            category = category,
            amount = if (tipping) { amount + tip } else { amount },
            date = date,
            onCardClick = {},
        )
        StandardVerticalSpacer(multiplier = 3f)
        NumberInput(
            label = stringResource(R.string.amountInputLabel),
            value = amount,
            onValueChange = onAmountChange,
            modifier = Modifier.fillMaxWidth()
        )
        StandardVerticalSpacer()
        TextInput(
            label = stringResource(R.string.nameInputLabel),
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth()
        )
        StandardVerticalSpacer()
        CategoryInputField(
            newCategoryState = newCategorySwitch,
            onNewCategoryStateChange = onNewCategorySwitchChange,
            category = category,
            onCategoryChange = onCategoryChange,
            categoryList = categoryList,
        )
        StandardVerticalSpacer()
        CheckBoxField(
            text = stringResource(R.string.addTipCheckboxLabel),
            state = tipping,
            onStateChanged = onTippingChange,
        )
        if(tipping){
            NumberInput(
                label = stringResource(R.string.tipInputLabel),
                value = tip,
                onValueChange = onTipChange,
            )
        }
        StandardVerticalSpacer()
        DatePickerField(
            label = stringResource(R.string.datePickerLabel),
            date = date,
            onDateChange = onDateChange
        )
    }
}


