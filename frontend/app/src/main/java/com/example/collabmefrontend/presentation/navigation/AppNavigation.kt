package com.example.collabmefrontend.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.collabme.presentation.auth.login.LoginScreen
import com.example.collabmefrontend.presentation.applications.detail.ApplicationDetailScreen
import com.example.collabmefrontend.presentation.applications.detail.ApplicationDetailViewModel
import com.example.collabmefrontend.presentation.applications.form.ApplicationFormScreen
import com.example.collabmefrontend.presentation.applications.form.ApplicationFormViewModel
import com.example.collabmefrontend.presentation.applications.list.ApplicationsScreen
import com.example.collabmefrontend.presentation.applications.list.ApplicationsViewModel
import com.example.collabmefrontend.presentation.login.LoginViewModel
import com.example.collabmefrontend.presentation.profile.ProfileScreen
import com.example.collabmefrontend.presentation.profile.ProfileViewModel
import com.example.collabmefrontend.presentation.profile.publicprofile.PublicProfileScreen
import com.example.collabmefrontend.presentation.profile.publicprofile.PublicProfileViewModel
import com.example.collabmefrontend.presentation.register.RegisterViewModel
import com.example.collabmefrontend.presentation.register.RegisterScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    profileViewModel: ProfileViewModel,
    applicationsViewModel: ApplicationsViewModel,
    applicationDetailViewModel: ApplicationDetailViewModel,
    applicationFormViewModel: ApplicationFormViewModel,
    publicProfileViewModel: PublicProfileViewModel,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToProfile = {
                    navController.navigate(Routes.APPLICATIONS) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
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
                    navController.navigate(Routes.PROFILE) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToPublicProfile = { userId ->
                    navController.navigate(Routes.publicProfile(userId))
                }
            )
        }

        composable(
            route = Routes.PUBLIC_PROFILE,
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
            PublicProfileScreen(
                viewModel = publicProfileViewModel,
                userId = userId,
                isOwnProfile = false,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.APPLICATIONS) {
            ApplicationsScreen(
                viewModel = applicationsViewModel,
                onNavigateToDetails = { id -> navController.navigate(Routes.applicationDetail(id)) },
                onNavigateToCreate = { navController.navigate(Routes.APPLICATION_CREATE) }
            )
        }

        composable(
            route = Routes.APPLICATIONS_DETAILS,
            arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("applicationId") ?: return@composable
            ApplicationDetailScreen(
                applicationId = id,
                viewModel = applicationDetailViewModel,
                onBack = { navController.popBackStack() },
                onEdit = { editId -> navController.navigate(Routes.applicationEdit(editId)) },
                onNavigateToProfile = { userId -> navController.navigate(Routes.publicProfile(userId)) }
            )
        }

        composable(Routes.APPLICATION_CREATE) {
            ApplicationFormScreen(
                viewModel = applicationFormViewModel,
                applicationId = null,
                onClose = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.APPLICATION_EDIT,
            arguments = listOf(navArgument("applicationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("applicationId")
            ApplicationFormScreen(
                viewModel = applicationFormViewModel,
                applicationId = id,
                onClose = { navController.popBackStack() }
            )
        }
    }
}