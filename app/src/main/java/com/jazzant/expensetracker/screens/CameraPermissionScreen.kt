package com.jazzant.expensetracker.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.jazzant.expensetracker.R

@Composable
fun CameraPermissionScreen(
    onCameraPermissionGranted: () -> Unit,
    onCameraPermissionDenied: () -> Unit
){
    var permissionGranted by remember { mutableStateOf(false) }
    CameraPermissionHandler(
        onPermissionGranted = { permissionGranted = true },
        onPermissionDenied = onCameraPermissionDenied
    )
    if (permissionGranted)
    { onCameraPermissionGranted() }
}

@Composable
fun CameraPermissionHandler(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
){
    val cameraPermission = Manifest.permission.CAMERA
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted)
            { onPermissionGranted() }
            else
            {
                Toast.makeText(context, context.getString(R.string.cameraPermissionDeniedToast), Toast.LENGTH_SHORT).show()
                onPermissionDenied()
            }
        }
    )

    LaunchedEffect(key1 = true) {
        if (ContextCompat.checkSelfPermission(context,cameraPermission) == PackageManager.PERMISSION_GRANTED)
        { onPermissionGranted() }
        else
        { permissionLauncher.launch(cameraPermission) }
    }
}