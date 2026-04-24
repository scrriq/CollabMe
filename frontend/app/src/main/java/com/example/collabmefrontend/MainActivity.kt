package com.example.collabmefrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.collabmefrontend.presentation.login.LoginViewModel
import com.example.collabmefrontend.presentation.navigation.AppNavigation
import com.example.collabmefrontend.presentation.register.RegisterViewModel
import com.example.collabmefrontend.ui.theme.CollabMeFrontendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val loginViewModel = LoginViewModel()
        val registerViewModel = RegisterViewModel()
        setContent {
            AppNavigation(
                loginViewModel = loginViewModel,
                registerViewModel = registerViewModel
            )
        }
    }
}