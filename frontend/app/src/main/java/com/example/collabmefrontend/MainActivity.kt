package com.example.collabmefrontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.collabmefrontend.core.network.ApiClient
import com.example.collabmefrontend.core.network.NetworkMonitor
import com.example.collabmefrontend.core.viewmodel.ViewModelFactory
import com.example.collabmefrontend.data.local.CollabMeDatabase
import com.example.collabmefrontend.data.remote.api.ApplicationApi
import com.example.collabmefrontend.data.remote.api.AuthApi
import com.example.collabmefrontend.data.remote.api.CatalogApi
import com.example.collabmefrontend.data.remote.api.ProfileApi
import com.example.collabmefrontend.data.repository.CachedApplicationRepository
import com.example.collabmefrontend.data.repository.CachedProfileRepository
import com.example.collabmefrontend.data.repository.RemoteApplicationCatalogRepository
import com.example.collabmefrontend.data.repository.RemoteApplicationRepository
import com.example.collabmefrontend.data.repository.RemoteAuthRepository
import com.example.collabmefrontend.data.repository.RemoteProfileCatalogRepository
import com.example.collabmefrontend.data.repository.RemoteProfileRepository
import com.example.collabmefrontend.data.storage.TokenStorage
import com.example.collabmefrontend.presentation.applications.cards.ApplicationCardsViewModel
import com.example.collabmefrontend.presentation.applications.detail.ApplicationDetailViewModel
import com.example.collabmefrontend.presentation.applications.responders.ApplicationRespondersViewModel
import com.example.collabmefrontend.presentation.applications.favorites.FavoriteApplicationsViewModel
import com.example.collabmefrontend.presentation.applications.form.ApplicationFormViewModel
import com.example.collabmefrontend.presentation.applications.list.ApplicationsViewModel
import com.example.collabmefrontend.presentation.login.LoginViewModel
import com.example.collabmefrontend.presentation.navigation.MainScreen
import com.example.collabmefrontend.presentation.profile.ProfileViewModel
import com.example.collabmefrontend.presentation.profile.publicprofile.PublicProfileViewModel
import com.example.collabmefrontend.presentation.register.RegisterViewModel
import com.example.collabmefrontend.ui.theme.CollabMeFrontendTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CollabMeFrontendTheme {
                val tokenStorage = remember { TokenStorage(applicationContext) }

                // Используем produceState для вызова suspend функции getToken()
                // initialValue = null означает, что мы еще "грузимся"
                val tokenState by produceState<String?>(initialValue = null) {
                    value = tokenStorage.getToken() ?: "" // Если токена нет, ставим пустую строку
                }

                // Пока tokenState равен null (именно null, а не ""), показываем лоадер
                if (tokenState == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Как только получили результат (даже пустую строку), инициализируем всё остальное
                    val isUserLoggedIn = tokenState!!.isNotEmpty()

                    val database = remember { CollabMeDatabase.getInstance(applicationContext) }
                    val networkMonitor = remember { NetworkMonitor(applicationContext) }

                    val httpClient = remember { ApiClient.create() }
                    val authApi = remember { AuthApi(httpClient) }
                    val profileApi = remember { ProfileApi(httpClient) }
                    val catalogApi = remember { CatalogApi(httpClient) }
                    val applicationsApi = remember {
                        ApplicationApi(httpClient, ApiClient.BASE_URL, tokenStorage)
                    }

                    val authRepository = remember { RemoteAuthRepository(authApi, tokenStorage) }
                    val remoteProfileRepository = remember { RemoteProfileRepository(profileApi, tokenStorage) }
                    val profileRepository = remember {
                        CachedProfileRepository(remoteProfileRepository, database.userProfileDao())
                    }
                    val profileCatalogRepository = remember { RemoteProfileCatalogRepository(catalogApi) }
                    val applicationCatalogRepository = remember { RemoteApplicationCatalogRepository(catalogApi) }
                    val remoteApplicationsRepository = remember { RemoteApplicationRepository(applicationsApi) }
                    val applicationsRepository = remember {
                        CachedApplicationRepository(
                            remoteApplicationsRepository,
                            database.applicationDao(),
                            database.favoriteApplicationDao(),
                        )
                    }

                    // ViewModels
                    val loginViewModel: LoginViewModel = viewModel(factory = ViewModelFactory { LoginViewModel(authRepository) })
                    val registerViewModel: RegisterViewModel = viewModel(factory = ViewModelFactory { RegisterViewModel(authRepository) })
                    val profileViewModel: ProfileViewModel = viewModel(factory = ViewModelFactory {
                        ProfileViewModel(profileRepository, profileCatalogRepository, authRepository)
                    })
                    val publicProfileViewModel: PublicProfileViewModel = viewModel(factory = ViewModelFactory {
                        PublicProfileViewModel(profileRepository, profileCatalogRepository)
                    })
                    val applicationsViewModel: ApplicationsViewModel = viewModel(factory = ViewModelFactory {
                        ApplicationsViewModel(applicationsRepository)
                    })
                    val favoriteApplicationsViewModel: FavoriteApplicationsViewModel = viewModel(factory = ViewModelFactory {
                        FavoriteApplicationsViewModel(applicationsRepository)
                    })
                    val applicationCardsViewModel: ApplicationCardsViewModel = viewModel(factory = ViewModelFactory {
                        ApplicationCardsViewModel(
                            applicationsRepository,
                            profileRepository,
                            profileCatalogRepository,
                            networkMonitor,
                        )
                    })
                    val applicationDetailViewModel: ApplicationDetailViewModel = viewModel(factory = ViewModelFactory {
                        ApplicationDetailViewModel(applicationsRepository, profileRepository)
                    })
                    val applicationRespondersViewModel: ApplicationRespondersViewModel = viewModel(factory = ViewModelFactory {
                        ApplicationRespondersViewModel(applicationsRepository)
                    })
                    val applicationFormViewModel: ApplicationFormViewModel = viewModel(factory = ViewModelFactory {
                        ApplicationFormViewModel(applicationsRepository, applicationCatalogRepository)
                    })

                    MainScreen(
                        loginViewModel = loginViewModel,
                        registerViewModel = registerViewModel,
                        profileViewModel = profileViewModel,
                        applicationsViewModel = applicationsViewModel,
                        favoriteApplicationsViewModel = favoriteApplicationsViewModel,
                        applicationCardsViewModel = applicationCardsViewModel,
                        applicationDetailViewModel = applicationDetailViewModel,
                        applicationRespondersViewModel = applicationRespondersViewModel,
                        applicationFormViewModel = applicationFormViewModel,
                        publicProfileViewModel = publicProfileViewModel,
                        isUserLoggedIn = isUserLoggedIn
                    )
                }
            }
        }
    }
}

