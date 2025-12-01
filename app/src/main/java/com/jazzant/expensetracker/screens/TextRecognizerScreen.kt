package com.jazzant.expensetracker.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
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
import com.jazzant.expensetracker.ui.StandardButton
import com.jazzant.expensetracker.ui.StandardVerticalSpacer

@Composable
fun TextRecognizerScreen(
    recognizedText: Text?,
    bitmap: Bitmap,
    onTextRecognized:() -> Unit,
    onRetakeImageButtonPress: () -> Unit,
    onCancelButtonPress: () -> Unit,
    modifier: Modifier = Modifier,
    receiptNotFoundOnImage: Boolean = false,
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        if (recognizedText == null){
            Text(
                text = stringResource(R.string.analyzingImageText),
                fontSize = TextUnit(40f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = stringResource(R.string.imageAnalyzedText),
                fontSize = TextUnit(40f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold
            )
        }
        StandardVerticalSpacer()
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.capturedImageBitmapContentDescription),
            modifier = Modifier.border(4.dp, Color.Black)
        )
        StandardVerticalSpacer()
        if (recognizedText == null)
        { Text(stringResource(R.string.noTextRecognizedText)) }
        else if (receiptNotFoundOnImage)
        {
            Icon(
                Icons.Default.Warning,
                tint = Color.Red,
                contentDescription = "Error Sign"
            )
            Text(text = "No Receipt Recognized on Image!",
                color = Color.Red,
                fontSize = TextUnit(24f, TextUnitType.Sp),
                fontWeight = FontWeight.Bold
            )
        }
        else
        { onTextRecognized() }
        StandardVerticalSpacer()
        StandardButton(
            onClick = onRetakeImageButtonPress,
            text = stringResource(R.string.retakeImageButton)
        )
        StandardVerticalSpacer()
        StandardButton(
            onClick = onCancelButtonPress,
            text = stringResource(R.string.cancelButton)
        )
    }
}