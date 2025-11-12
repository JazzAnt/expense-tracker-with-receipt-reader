package com.jazzant.expensetracker

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun CameraPermissionHandler(onPermissionGranted: () -> Unit){
    val cameraPermission = Manifest.permission.CAMERA
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if(isGranted)
            { onPermissionGranted() }
            else
            { Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show() }
        }
    )

    LaunchedEffect(key1 = true) {
        if(ContextCompat.checkSelfPermission(context,cameraPermission) == PackageManager.PERMISSION_GRANTED)
        { onPermissionGranted() }
        else
        { permissionLauncher.launch(cameraPermission) }
    }
}