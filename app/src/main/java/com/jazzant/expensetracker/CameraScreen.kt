package com.jazzant.expensetracker

import android.content.Context
import android.media.Image
import android.util.Log
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreviewScreen(textRecognitionVM: TextRecognitionVM = viewModel()){
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    //Build the preview object
    val preview = Preview.Builder()
        .build()
    val previewView = remember {
        PreviewView(context)
    }
    //Build the camera selector, require back facing lens
    //Using CameraSelector.DEFAULT_BACK_CAMERA also works
    val cameraxSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    val imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    //Launch Camera as a coroutine
    LaunchedEffect(lensFacing) {
        //Get the camera provider from the context
        val cameraProvider = context.getCameraProvider()
        //Unbind existing use cases before rebinding
        cameraProvider.unbindAll()
        //Bind use cases to camera
        cameraProvider.bindToLifecycle(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraxSelector,
            preview,
            imageCapture
            )
        //The preview Surface property is received from the PreviewView
        preview.surfaceProvider = previewView.surfaceProvider
    }
    //Turns the PreviewView (an android View) into a composable
    Box(
        contentAlignment = Alignment.BottomCenter, modifier = Modifier.fillMaxSize()
    ){
        AndroidView(factory = {previewView}, modifier = Modifier.fillMaxSize())
        Column {

        Card {
            Text("Text:\n${textRecognitionVM.recognizedText.value}")
        }
        Button(
            onClick = {
                imageCapture?.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback(){
                        @OptIn(ExperimentalGetImage::class)
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val mediaImage = image.image
                            if(mediaImage != null) {
                                textRecognitionVM.recognizeText(
                                    mediaImage,
                                    image.imageInfo.rotationDegrees
                                )
                            }
                            image.close()
                        }

                        override fun onError(exception: ImageCaptureException) {

                        }
                    }
                )

            }
        ) { Text("Capture Image") }
        }

    }
}

class TextRecognitionVM: ViewModel(){
    private val _recognizedText = mutableStateOf<String?>(null)
    val recognizedText = _recognizedText

    fun recognizeText(mediaImage: Image, rotationDegrees: Int) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val image = InputImage.fromMediaImage(mediaImage,rotationDegrees)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                _recognizedText.value = visionText.text
            }
            .addOnFailureListener { e ->
                _recognizedText.value = "Error: ${e.message}"
            }
    }
}

private class TextAnalyzer : ImageAnalysis.Analyzer{
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if(mediaImage != null){
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val result = recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    for (block in visionText.textBlocks){
                        Log.d("TEXTREADER", block.text)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("TEXTREADER", exception.message!!)
                }
        }
    }

}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        //Get instance of CameraProvider from Context to bind lifecycle of cameras to owner
        //This means there's no need to open and close the camera manually
        //CameraX will automatically do that depending on the lifecycle
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                //Bind Lifecycle of Cameras to Lifecycle Owner (Context)
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
                //This returns an executor that runs on the main thread
                //Thus the runnable task is run there
        }
    }