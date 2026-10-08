package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.api.ApiClient
import com.example.data.repository.GuestFlowRepository
import com.example.data.security.KioskPreferences
import com.example.ui.screens.MainKioskScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.KioskViewModel

class MainActivity : ComponentActivity() {

    private val kioskViewModel: KioskViewModel by viewModels {
        val preferences = KioskPreferences(applicationContext)
        val apiClient = ApiClient(preferences)
        val repository = GuestFlowRepository(apiClient, preferences)
        KioskViewModel.Factory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = com.example.ui.theme.GuestFlowCream
                ) {
                    MainKioskScreen(viewModel = kioskViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh device status when app returns to foreground
        kioskViewModel.checkDeviceStatus()
    }
}
