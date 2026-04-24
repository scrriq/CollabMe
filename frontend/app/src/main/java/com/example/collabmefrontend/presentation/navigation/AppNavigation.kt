package com.example.collabmefrontend.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.collabme.presentation.auth.login.LoginScreen
import com.example.collabmefrontend.presentation.login.LoginViewModel
import com.example.collabmefrontend.presentation.profile.ProfileScreen
import com.example.collabmefrontend.presentation.register.RegisterScreen
import com.example.collabmefrontend.presentation.register.RegisterViewModel
import kotlin.math.log


@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel
){
    // Создаем nav Controller
    val navController = rememberNavController()

    //Описываем навигацию
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
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
            // Логика с регистрацией. Необходимо добавить RegisterScreen
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
            // логика с выходом из профиля
            ProfileScreen(
                text = "Экран профиля"
            )
        }
    }
}