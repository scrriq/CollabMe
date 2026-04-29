package com.example.collabmefrontend.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.collabme.presentation.auth.login.LoginScreen
import com.example.collabmefrontend.presentation.applications.detail.ApplicationDetailScreen
import com.example.collabmefrontend.presentation.applications.detail.ApplicationDetailViewModel
import com.example.collabmefrontend.presentation.applications.list.ApplicationsScreen
import com.example.collabmefrontend.presentation.applications.list.ApplicationsViewModel
import com.example.collabmefrontend.presentation.login.LoginViewModel
import com.example.collabmefrontend.presentation.profile.ProfileScreen
import com.example.collabmefrontend.presentation.profile.ProfileViewModel
import com.example.collabmefrontend.presentation.profile.publicprofile.PublicProfileScreen
import com.example.collabmefrontend.presentation.profile.publicprofile.PublicProfileViewModel
import com.example.collabmefrontend.presentation.register.RegisterScreen
import com.example.collabmefrontend.presentation.register.RegisterViewModel


@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    profileViewModel: ProfileViewModel,
    applicationsViewModel: ApplicationsViewModel,
    applicationDetailViewModel: ApplicationDetailViewModel,
    publicProfileViewModel: PublicProfileViewModel
){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.PROFILE
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
                },
                onNavigateToPublicProfile = { userId ->
                    navController.navigate("public_profile/$userId")
                }
            )
        }

        composable(
            route = Routes.PUBLIC_PROFILE,
            arguments = listOf(
                navArgument("userId"){
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable

            PublicProfileScreen(
                viewModel = publicProfileViewModel,
                userId = userId,
                isOwnProfile = false,
                onBack = {navController.popBackStack()},
                onEdit = {navController.navigate(Routes.PROFILE)}
            )
        }

        composable(Routes.APPLICATIONS){
            ApplicationsScreen(
                viewModel = applicationsViewModel,
                onNavigateToDetails = { applicationId ->
                    navController.navigate(Routes.applicationDetail(applicationId))
                }
            )
        }

        composable(
            route = Routes.APPLICATIONS_DETAILS,
            arguments = listOf(
                navArgument("applicationId"){
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val applicationId = backStackEntry.arguments?.getString("applicationId")
                ?: return@composable

            ApplicationDetailScreen(
                applicationId = applicationId,
                viewModel = applicationDetailViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}