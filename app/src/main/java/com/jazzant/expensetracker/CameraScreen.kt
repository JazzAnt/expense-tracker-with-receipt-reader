package com.jazzant.expensetracker

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreviewScreen(){
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
            preview)
        //The preview Surface property is received from the PreviewView
        preview.surfaceProvider = previewView.surfaceProvider
    }
    //Turns the PreviewView (an android View) into a composable
    AndroidView(factory = {previewView}, modifier = Modifier.fillMaxSize())
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