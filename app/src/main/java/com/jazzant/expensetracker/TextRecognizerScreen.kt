package com.jazzant.expensetracker

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.google.mlkit.vision.text.Text

@Composable
fun TextRecognizerScreen(
    recognizedText: Text?,
    bitmap: Bitmap,
    receiptModelList: List<ReceiptModel>,
    receiptModelIndex: Int,
    onRetakeImageButtonPress: () -> Unit,
    onCancelButtonPress: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxSize()) {
        Text(
            text = "Analyzing Image...",
            fontSize = TextUnit(5f, TextUnitType.Em),
            fontWeight = FontWeight.Bold
        )
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Image being analyzed",
            modifier = Modifier.border(2.dp, Color.Black)
        )
        if (recognizedText == null)
        { Text("No text found yet...") }
        else
        { Text("TEXT ANALYZED (TODO: add functions here)") }
        Button(onClick = onRetakeImageButtonPress)
        { Text("Retake Image") }
        Button(onClick = onCancelButtonPress)
        { Text("Cancel") }
    }
}

/**
 * Checks the recognized text if it contains any of the keyword in the receiptModelList.
 * Returns the index of the receiptModelList where a keyword is found, or -1 if none are found.
 */
fun findKeyword(
    recognizedText: Text,
    receiptModelList: List<ReceiptModel>
): Int
{
    var index = -1
    val listLastIndex = receiptModelList.size - 1
    for (i in 0..listLastIndex)
    {
        val keyword = receiptModelList[i].keyword
        if(recognizedText.text.contains(keyword, ignoreCase = true))
        {
            index = i
            break
        }
    }
    return index
}