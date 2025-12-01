package com.jazzant.expensetracker.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.jazzant.expensetracker.database.receiptmodel.ReceiptModel
import com.jazzant.expensetracker.ui.CategoryInputField
import com.jazzant.expensetracker.ui.TextInput

@Composable
fun ReceiptModelListScreen(
    list: List<ReceiptModel>,
    onCardClick: (ReceiptModel) -> Unit,
    currentReceiptModel: ReceiptModel?,
    onEditorNameChange: (String) -> Unit,
    onEditorCategoryChange: (String) -> Unit,
    onEditorSaveChanges: () -> Unit,
    onEditorDelete: () -> Unit,
    categoryList: List<String>,
){
    Box(Modifier.fillMaxSize()){
        LazyColumn {
            items(list){
                    item ->
                ReceiptModelCard(item, onCardClick)
            }
        }
        if (currentReceiptModel != null){
            ReceiptModelEditor(
                modifier = Modifier.align(Alignment.BottomEnd),
                model = currentReceiptModel,
                onNameChange = onEditorNameChange,
                onCategoryChange = onEditorCategoryChange,
                onSaveChanges = onEditorSaveChanges,
                onDelete = onEditorDelete,
                categoryList = categoryList,
            )
        }
    }


}

@Composable
fun ReceiptModelCard(model: ReceiptModel, onCardClick: (ReceiptModel)->Unit){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color.White),
        onClick = {onCardClick(model)}
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(5.dp)
        ) {
            Column (
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth(0.64f)
                    .fillMaxHeight()

            ){
                Text(text = "Name: " + model.name, fontSize = TextUnit(3.6f, TextUnitType.Em), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = "Keyword: " + model.keyword, fontSize = TextUnit(3f, TextUnitType.Em), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Row (
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Category: " + model.category, fontSize = TextUnit(4.0f, TextUnitType.Em), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun ReceiptModelEditor(
    model: ReceiptModel,
    modifier: Modifier = Modifier,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onSaveChanges: () -> Unit,
    onDelete: () -> Unit,
    categoryList: List<String>,
){
    var newCategoryState by remember { mutableStateOf(false) }
    Card(modifier
        .padding(16.dp)
        .border(BorderStroke(1.dp, Color.Black))
        .fillMaxWidth()
        .height(220.dp)
        .shadow( elevation = 3.dp)
    ) {
        Column(Modifier.fillMaxSize()) {
            TextInput(
                label = "Name",
                value = model.name,
                onValueChange = onNameChange,
            )
            Spacer(Modifier.height(3.dp))
            CategoryInputField(
                newCategoryState = newCategoryState,
                onNewCategoryStateChange = {newCategoryState = it},
                category = model.category,
                onCategoryChange = onCategoryChange,
                categoryList = categoryList,
            )
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Button",
                    )
                    Text("Delete Model")
                }
                Spacer(Modifier.width(20.dp))
                Button(onClick = onSaveChanges) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = "Save Button",
                    )
                    Text("Save Changes")
                }
            }
        }
    }
}
