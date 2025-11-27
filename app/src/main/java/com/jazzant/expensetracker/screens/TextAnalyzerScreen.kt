package com.jazzant.expensetracker.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.text.Text
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.database.receiptmodel.ReceiptModel

@Composable
fun TextAnalyzerScreen(
    receiptModelList: List<ReceiptModel>,
    receiptModelIndex: Int,
    bitmap: Bitmap,
    onCreateNewReceiptModelButtonPress: () -> Unit,
    onUseAnalyzedExpenseButtonPress: () -> Unit,
    onInputExpenseManuallyButtonPress: () -> Unit,
    onRetakeImageButtonPress: () -> Unit,
    onCancelButtonPress: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.imageAnalyzedText),
            fontSize = TextUnit(5f, TextUnitType.Em),
            fontWeight = FontWeight.Bold
        )
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.capturedImageBitmapContentDescription),
            modifier = Modifier.border(2.dp, Color.Black)
        )
        if (receiptModelIndex < 0)
        {
            Button(onClick = onCreateNewReceiptModelButtonPress)
            { Text(stringResource(R.string.createNewReceiptModelButton)) }
            Button(onClick = onInputExpenseManuallyButtonPress)
            { Text(stringResource(R.string.inputExpenseManuallyButton))}
        }
        else
        {
            //TODO: Display Analyzed Expense as ExpenseCard
            Button(onClick = onUseAnalyzedExpenseButtonPress)
            { Text(stringResource(R.string.useAnalyzedExpenseButton)) }
        }
        Button(onClick = onRetakeImageButtonPress)
        { Text(stringResource(R.string.retakeImageButton)) }
        Button(onClick = onCancelButtonPress)
        { Text(stringResource(R.string.cancelButton)) }
    }
}