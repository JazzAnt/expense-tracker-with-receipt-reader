package com.jazzant.expensetracker

import android.content.Context
import androidx.camera.core.CameraSelector
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraPreviewScreen(){
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    //Build the preview
    val preview = Preview.Builder()
        .build()
    val previewView = remember {
        PreviewView(context)
    }
    //Build the camera selector, require back facing lens
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

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        //Get instance of CameraProvider from Context
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                //Bind Lifecycle of Cameras to Lifecycle Owner (Context)
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }