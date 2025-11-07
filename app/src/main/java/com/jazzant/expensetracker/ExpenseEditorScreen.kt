package com.jazzant.expensetracker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import java.time.LocalDate

@Composable
fun ExpenseEditorScreen(
    categoryList: List<String>,
    amount: Float,
    onAmountChange: (Float) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    newCategory: String,
    onNewCategoryChange: (String) -> Unit,
    tipping: Boolean,
    onTippingChange: (Boolean) -> Unit,
    tip: Float,
    onTipChange: (Float) -> Unit,
    date: LocalDate,
    onButtonPress: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        NumberInput(
            label = "Expense Amount:",
            value = amount,
            onValueChange = onAmountChange,
            modifier = Modifier.fillMaxWidth()
        )

        TextInput(
            label = "Expense Name:",
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth()
        )

        RadioButtons(
            label = "Expense Category:",
            radioOptions = categoryList,
            selectedOption = category,
            onOptionChange = onCategoryChange,
            modifier = Modifier.fillMaxWidth()
        )
        if(category == SpecialCategories.ADD_NEW_CATEGORY.name){
            TextInput(
                label = "",
                value = newCategory,
                onValueChange = onNewCategoryChange,
                modifier = Modifier.fillMaxWidth()
            )
        }

        CheckBoxField(
            text = "Add tip?",
            state = tipping,
            onStateChanged = onTippingChange,
            modifier = Modifier.fillMaxWidth(0.5f)
        )
        if(tipping){
            NumberInput(
                label = "Tip Amount",
                value = tip,
                onValueChange = onTipChange,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Text("Amount\t: ${
                    if(tipping){
                        amount + tip
                    } else {
                        amount
                    }
                }")
                Text("Category\t: ${
                    if(category == SpecialCategories.ADD_NEW_CATEGORY.name){
                        newCategory
                    } else {
                        category
                    }
                }")
                Text("Name\t: $name")
                Text("Date\t: $date")

            }
        }

        Button(
            onClick = onButtonPress,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}


