package com.jazzant.expensetracker

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun CameraPreviewScreen(modifier:Modifier = Modifier, onImageCapture: (ImageProxy)-> Unit){
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val previewView = remember { PreviewView(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    Box(modifier = Modifier.padding(bottom = 50.dp)) {
        AndroidView(
            factory = { previewView },
            modifier = modifier
        ) { view -> //this is the block provided to the factory, in this case previewView
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                imageCapture = ImageCapture.Builder().build()
                preview.surfaceProvider = view.surfaceProvider

                /* Bind the preview and imageCapture to the cameraProvider use cases
                 * Basically tells the cameraProvider what values we want from it
                 * In this case: Preview to show the camera view on our composable
                 * And imageCapture to capture the image and store the value as ImageProxy
                 */
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner = lifecycleOwner,
                        cameraSelector = cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (e: Exception) {
                    Log.e("CameraScreen", "Camera Binding Failed: ${e.message}")
                }
            }, ContextCompat.getMainExecutor(context))
        }
        FloatingActionButton(
            onClick = {
                imageCapture?.takePicture(
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageCapturedCallback(){
                        override fun onCaptureSuccess(imageProxy: ImageProxy) {
                            onImageCapture(imageProxy)
                            imageProxy.close()
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e("CameraScreen", "Image Capture Failed: ${exception.message}")
                        }
                    }
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Icon(Icons.Default.Search, contentDescription = "Take Picture to Analyze")
        }
    }
}