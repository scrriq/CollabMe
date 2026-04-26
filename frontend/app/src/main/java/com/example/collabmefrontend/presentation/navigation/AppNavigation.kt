package com.example.collabmefrontend.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.collabme.presentation.auth.login.LoginScreen
import com.example.collabmefrontend.presentation.applications.list.ApplicationsScreen
import com.example.collabmefrontend.presentation.applications.list.ApplicationsViewModel
import com.example.collabmefrontend.presentation.login.LoginViewModel
import com.example.collabmefrontend.presentation.profile.ProfileScreen
import com.example.collabmefrontend.presentation.profile.ProfileViewModel
import com.example.collabmefrontend.presentation.register.RegisterScreen
import com.example.collabmefrontend.presentation.register.RegisterViewModel
import kotlin.math.log


@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    profileViewModel: ProfileViewModel,
    applicationsViewModel: ApplicationsViewModel
){
    // Создаем nav Controller
    val navController = rememberNavController()

    //Описываем навигацию
    NavHost(
        navController = navController,
        startDestination = Routes.APPLICATIONS
    ){
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,

                // переход на профиль
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE){
                        popUpTo(Routes.LOGIN){inclusive=true}
                    }
                },
                // переход на регистрацию
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToProfile = {
                    navController.navigate(Routes.PROFILE){
                        popUpTo(Routes.LOGIN) {inclusive = true}
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN){
                        popUpTo(Routes.PROFILE) {  inclusive = true}
                    }
                }
            )
        }

        composable(Routes.APPLICATIONS){
            ApplicationsScreen(
                viewModel = applicationsViewModel
            )
        }
    }
}