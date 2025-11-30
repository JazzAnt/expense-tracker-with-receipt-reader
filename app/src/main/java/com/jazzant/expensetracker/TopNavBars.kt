package com.jazzant.expensetracker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.jazzant.expensetracker.ui.DateRangePickerModal


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavBar(
    currentRoute: String,
    modifier: Modifier = Modifier
){
    TopAppBar(
        title = { Text(currentRoute) },
        modifier = modifier,
        navigationIcon = {

            IconButton(
                onClick = {
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.menuNavIcon),
                    tint = Color.Gray
                )
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavBar(
    onResetButtonPress: () -> Unit,
    titleText: String,
    isSearching: Boolean,
    setIsSearching: (Boolean) -> Unit,
    searchValue: String,
    onSearchValueChange: (String)->Unit,
    dateRange: Pair<Long?, Long?>,
    onDateRangeChanged: (Pair<Long?, Long?>) -> Unit,
    categoryList: List<String>,
    selectedCategory: String,
    onSelectionChange: (String) -> Unit,
){
    var showDatePicker by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            if (isSearching) {
                TextField(
                    value = searchValue,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true,
                    onValueChange = onSearchValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { onSearchValueChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search",
                                tint = Color.Gray
                            )
                        }
                    }
                )
            }
            else {
                Text(titleText)
            }
        },
        navigationIcon = {
            SettingDropDownMenu(onResetButtonPress)
        },
        actions = {
            if (isSearching) {
                IconButton(onClick = { setIsSearching(false) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Search",
                        tint = Color.Black
                    )
                }
            }
            else {
                CategoryDropDownMenu(
                    categoryList = categoryList,
                    selectedCategory = selectedCategory,
                    onSelectionChange = onSelectionChange
                )
                if (dateRange.first == null || dateRange.second == null){
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Filter by Date",
                            tint = Color.Black
                        )
                    }
                }
                else{
                    IconButton(onClick = { onDateRangeChanged(Pair(null,null)) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Reset Date",
                            tint = Color.Black
                        )
                    }
                }

                IconButton(onClick = { setIsSearching(true) }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color.Black
                    )
                }
            }

        }

    )
    if (showDatePicker) {
        DateRangePickerModal(
            dateRange = dateRange,
            onDateRangeSelected = onDateRangeChanged,
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun SettingDropDownMenu(
    onResetButtonPress: () -> Unit,
){
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton( onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Settings"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text("Reset Query")
                },
                onClick = {onResetButtonPress()}
            )
        }
    }
}

@Composable
fun CategoryDropDownMenu(
    categoryList: List<String>,
    selectedCategory: String,
    onSelectionChange: (String) -> Unit,
){
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton( onClick = { expanded = !expanded }) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Filter by Category"
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text("No Category", color = Color.Red)
                },
                onClick = {onSelectionChange("")}
            )
            categoryList.forEach {
                DropdownMenuItem(
                    text = {
                        Text(it, color = selectedColor(selectedCategory == it))
                    },
                    onClick = {onSelectionChange(it)}
                )
            }
        }
    }
}

fun selectedColor(selected: Boolean): Color {
    return if (selected)
        Color.Blue
    else
        Color.Black
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorNavBar(
    modifier: Modifier = Modifier,
    isCreatingNewExpense: Boolean = true,
    onBackButtonPress: () -> Unit,
    onResetButtonPress: () -> Unit,
    onSaveButtonPress: () -> Unit
){
    TopAppBar(
        title = {
            if (isCreatingNewExpense)
            { Text("Creating Expense") }
            else
            { Text("Editing Expense") }
                },
        modifier = modifier,
        navigationIcon = {
            IconButton( onClick = {onBackButtonPress()} )
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        actions = {
            IconButton( onClick = {onResetButtonPress()} )
            {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Input Values",
                )
            }
            IconButton( onClick = {onSaveButtonPress()} )
            {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = "Save",
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraNavBar(
    modifier: Modifier = Modifier,
    onBackButtonPress: () -> Unit,
){
    TopAppBar(
        title = { Text("Using Camera To Get Receipt") },
        modifier = modifier,
        navigationIcon = {
            IconButton( onClick = {onBackButtonPress()} )
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptCreatorNavBar(
    titleText: String,
    modifier: Modifier = Modifier,
    onBackButtonPress: () -> Unit,
    onResetButtonPress: () -> Unit
){
    TopAppBar(
        title = { Text(titleText) },
        modifier = modifier,
        navigationIcon = {
            IconButton( onClick = {onBackButtonPress()} )
            {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                )
            }
        },
        actions = {
            IconButton( onClick = {onResetButtonPress()} )
            {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Input Values",
                )
            }
        }
    )
}
