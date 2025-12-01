package com.jazzant.expensetracker.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.jazzant.expensetracker.R
import com.jazzant.expensetracker.database.expense.Expense
import com.jazzant.expensetracker.ui.ExpenseCard
import com.jazzant.expensetracker.ui.StandardButton

@Composable
fun TextAnalyzerScreen(
    receiptModelIndex: Int,
    bitmap: Bitmap,
    onCreateNewReceiptModelButtonPress: () -> Unit,
    onInputExpenseManuallyButtonPress: () -> Unit,
    onUseAnalyzedExpenseButtonPress: () -> Unit,
    onEditAnalyzedExpenseButtonPress: () -> Unit,
    analyzedExpense: Expense?,
    onRetakeImageButtonPress: () -> Unit,
    onCancelButtonPress: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Text(
            text = stringResource(R.string.imageAnalyzedText),
            fontSize = TextUnit(40f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(SPACING.dp))
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.capturedImageBitmapContentDescription),
            modifier = Modifier.border(4.dp, Color.Black)
        )
        Spacer(Modifier.height(SPACING.dp))
        if (receiptModelIndex < 0)
        {
            Text(text = "The app has never seen a receipt like this!",
                color = Color.Red,
                fontSize = TextUnit(18f, TextUnitType.Sp),
            )
            Spacer(Modifier.height(SPACING.dp))
            StandardButton(
                onClick = onCreateNewReceiptModelButtonPress,
                text = stringResource(R.string.createNewReceiptModelButton)
            )
            Spacer(Modifier.height(SPACING.dp))
            StandardButton(
                onClick = onInputExpenseManuallyButtonPress,
                text = stringResource(R.string.inputExpenseManuallyButton)
            )
        }
        else
        {
            Text(text = "Expense Detected:",
                color = Color.Red,
                fontSize = TextUnit(18f, TextUnitType.Sp),
            )
            Spacer(Modifier.height(SPACING.dp))
            if (analyzedExpense != null){
                ExpenseCard(
                    expense = analyzedExpense,
                    onCardClick = {}
                )
            }
            Spacer(Modifier.height(SPACING.dp))
            StandardButton(
                onClick = onUseAnalyzedExpenseButtonPress,
                text = stringResource(R.string.useAnalyzedExpenseButton)
            )
            Spacer(Modifier.height(SPACING.dp))
            StandardButton(
                onClick = onEditAnalyzedExpenseButtonPress,
                text = stringResource(R.string.editAnalyzedExpenseButton)
            )
        }

        Spacer(Modifier.height(SPACING.dp))
        StandardButton(
            onClick = onRetakeImageButtonPress,
            text = stringResource(R.string.retakeImageButton)
        )
        Spacer(Modifier.height(SPACING.dp))
        StandardButton(
            onClick = onCancelButtonPress,
            text = stringResource(R.string.cancelButton)
        )
    }
}