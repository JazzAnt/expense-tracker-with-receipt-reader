package com.jazzant.expensetracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.jazzant.expensetracker.ui.theme.ExpenseTrackerWithBillReaderTheme

class MainActivity : ComponentActivity() {
    private lateinit var expenseViewModel: ExpenseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (PackageManager.PERMISSION_GRANTED){
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) -> {
                setAppContent()
            }
            else -> {
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            }
        }

        expenseViewModel = ExpenseViewModel()
        expenseViewModel.initializeViewModel(this)
    }

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
            if(isGranted){
                setAppContent()
            } else {
                //Permission Denied
            }
        }
    private fun setAppContent(){
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerWithBillReaderTheme {
                ExpenseApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ExpenseTrackerWithBillReaderTheme {
    }
}