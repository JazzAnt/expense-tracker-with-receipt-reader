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
import androidx.compose.ui.res.stringResource
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
    onTextRecognized:() -> Unit,
    onRetakeImageButtonPress: () -> Unit,
    onCancelButtonPress: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.analyzingImageText),
            fontSize = TextUnit(5f, TextUnitType.Em),
            fontWeight = FontWeight.Bold
        )
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.capturedImageBitmapContentDescription),
            modifier = Modifier.border(2.dp, Color.Black)
        )
        if (recognizedText == null)
        { Text(stringResource(R.string.noTextRecognizedText)) }
        else
        { onTextRecognized() }
        Button(onClick = onRetakeImageButtonPress)
        { Text(stringResource(R.string.retakeImageButton)) }
        Button(onClick = onCancelButtonPress)
        { Text(stringResource(R.string.cancelButton)) }
    }
}