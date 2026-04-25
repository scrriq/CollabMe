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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.collabmefrontend.core.network.ApiClient
import com.example.collabmefrontend.data.remote.api.AuthApi
import com.example.collabmefrontend.data.repository.RemoteAuthRepository
import com.example.collabmefrontend.data.storage.TokenStorage
import com.example.collabmefrontend.presentation.login.LoginViewModel
import com.example.collabmefrontend.presentation.navigation.AppNavigation
import com.example.collabmefrontend.presentation.register.RegisterViewModel
import com.example.collabmefrontend.ui.theme.CollabMeFrontendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = this@MainActivity

            val httpClient = remember { ApiClient.create() }
            val tokenStorage = remember { TokenStorage(context.applicationContext) }

            val authRepository = remember {
                RemoteAuthRepository(
                    api = AuthApi(httpClient),
                    tokenStorage = tokenStorage
                )
            }
            val loginViewModel = remember { LoginViewModel(authRepository) }
            val registerViewModel = remember { RegisterViewModel(authRepository) }
            AppNavigation(
                loginViewModel = loginViewModel,
                registerViewModel = registerViewModel
            )
        }
    }
}