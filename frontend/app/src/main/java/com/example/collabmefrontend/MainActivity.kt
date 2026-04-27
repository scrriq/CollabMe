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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.collabmefrontend.core.network.ApiClient
import com.example.collabmefrontend.core.viewmodel.ViewModelFactory
import com.example.collabmefrontend.data.remote.api.ApplicationApi
import com.example.collabmefrontend.data.remote.api.AuthApi
import com.example.collabmefrontend.data.remote.api.ProfileApi
import com.example.collabmefrontend.data.repository.RemoteApplicationRepository
import com.example.collabmefrontend.data.repository.RemoteAuthRepository
import com.example.collabmefrontend.data.repository.RemoteProfileRepository
import com.example.collabmefrontend.data.storage.TokenStorage
import com.example.collabmefrontend.presentation.applications.detail.ApplicationDetailViewModel
import com.example.collabmefrontend.presentation.applications.list.ApplicationsViewModel
import com.example.collabmefrontend.presentation.login.LoginViewModel
import com.example.collabmefrontend.presentation.navigation.AppNavigation
import com.example.collabmefrontend.presentation.profile.ProfileViewModel
import com.example.collabmefrontend.presentation.register.RegisterViewModel
import com.example.collabmefrontend.ui.theme.CollabMeFrontendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val httpClient = remember { ApiClient.create() }
            val tokenStorage = remember { TokenStorage(applicationContext) }

            val authApi = remember { AuthApi(httpClient) }
            val profileApi = remember{ProfileApi(httpClient)}
            val applicationsApi = remember{
                ApplicationApi(
                    client = httpClient,
                    baseUrl = ApiClient.BASE_URL
                )
            }
            val authRepository = remember {
                RemoteAuthRepository(authApi, tokenStorage)
            }

            val profileRepository = remember {
                RemoteProfileRepository(profileApi, tokenStorage)
            }

            val applicationsRepository = remember {
                RemoteApplicationRepository(applicationsApi)
            }

            val loginViewModel: LoginViewModel = viewModel(
                factory = ViewModelFactory {
                    LoginViewModel(authRepository)
                }
            )

            val registerViewModel: RegisterViewModel = viewModel(
                factory = ViewModelFactory {
                    RegisterViewModel(authRepository)
                }
            )

            val profileViewModel: ProfileViewModel = viewModel(
                factory = ViewModelFactory {
                    ProfileViewModel(
                        profileRepository = profileRepository,
                        authRepository = authRepository
                    )
                }
            )

            val applicationsViewModel: ApplicationsViewModel = viewModel(
                factory = ViewModelFactory {
                    ApplicationsViewModel(applicationsRepository)
                }
            )

            val applicationDetailViewModel: ApplicationDetailViewModel = viewModel(
                factory = ViewModelFactory{
                    ApplicationDetailViewModel(applicationsRepository)
                }
            )



            AppNavigation(
                loginViewModel = loginViewModel,
                registerViewModel = registerViewModel,
                profileViewModel = profileViewModel,
                applicationsViewModel = applicationsViewModel,
                applicationDetailViewModel = applicationDetailViewModel
            )
        }
    }
}