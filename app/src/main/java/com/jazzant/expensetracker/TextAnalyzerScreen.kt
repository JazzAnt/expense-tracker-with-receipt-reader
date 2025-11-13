package com.jazzant.expensetracker

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.mlkit.vision.text.Text

@Composable
fun TextAnalyzerScreen(recognizedText: Text?){
    if (recognizedText == null)
    { Text("No Text Recognized") }
    else
    {
        Column {
            Text("TEXT BLOCKS")
            recognizedText.textBlocks.forEach { textBlock ->
                Text("--------")
                Text(textBlock.text)
            }
        }
    }
}