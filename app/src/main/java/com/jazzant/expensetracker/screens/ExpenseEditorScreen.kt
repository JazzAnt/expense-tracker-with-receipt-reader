package com.jazzant.expensetracker.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jazzant.expensetracker.ui.CheckBoxField
import com.jazzant.expensetracker.ui.DatePickerField
import com.jazzant.expensetracker.ui.NumberInput
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.ui.CategoryInputField
import com.jazzant.expensetracker.ui.ExpenseCard
import com.jazzant.expensetracker.ui.RadioButtons
import com.jazzant.expensetracker.ui.SwitchField
import com.jazzant.expensetracker.ui.TextInput
import com.jazzant.expensetracker.ui.convertMillisToDate

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
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        NumberInput(
            label = stringResource(R.string.amountInputLabel),
            value = amount,
            onValueChange = onAmountChange,
            modifier = Modifier.fillMaxWidth()
        )

        TextInput(
            label = stringResource(R.string.nameInputLabel),
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth()
        )

        CategoryInputField(
            newCategoryState = newCategorySwitch,
            onNewCategoryStateChange = onNewCategorySwitchChange,
            category = category,
            onCategoryChange = onCategoryChange,
            categoryList = categoryList,
        )

        CheckBoxField(
            text = stringResource(R.string.addTipCheckboxLabel),
            state = tipping,
            onStateChanged = onTippingChange,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        if(tipping){
            NumberInput(
                label = stringResource(R.string.tipInputLabel),
                value = tip,
                onValueChange = onTipChange,
                modifier = Modifier.fillMaxWidth()
            )
        }

        DatePickerField(
            label = stringResource(R.string.datePickerLabel),
            date = date,
            onDateChange = onDateChange
        )
        ExpenseCard(
            name = name,
            category = category,
            amount = if (tipping) { amount + tip } else { amount },
            date = date,
            onCardClick = {},
        )
    }
}


