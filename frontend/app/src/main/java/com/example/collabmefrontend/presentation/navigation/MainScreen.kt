package com.example.collabmefrontend.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.collabmefrontend.presentation.applications.detail.ApplicationDetailViewModel
import com.example.collabmefrontend.presentation.applications.favorites.FavoriteApplicationsViewModel
import com.example.collabmefrontend.presentation.applications.favorites.FavoriteApplicationsIntent
import com.example.collabmefrontend.presentation.applications.form.ApplicationFormViewModel
import com.example.collabmefrontend.presentation.applications.list.ApplicationsViewModel
import com.example.collabmefrontend.presentation.login.LoginViewModel
import com.example.collabmefrontend.presentation.profile.ProfileViewModel
import com.example.collabmefrontend.presentation.profile.publicprofile.PublicProfileViewModel
import com.example.collabmefrontend.presentation.register.RegisterViewModel

@Composable
fun MainScreen(
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    profileViewModel: ProfileViewModel,
    applicationsViewModel: ApplicationsViewModel,
    favoriteApplicationsViewModel: FavoriteApplicationsViewModel,
    applicationDetailViewModel: ApplicationDetailViewModel,
    applicationFormViewModel: ApplicationFormViewModel,
    publicProfileViewModel: PublicProfileViewModel,
    isUserLoggedIn: Boolean,
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomBarScreens = listOf(
        Routes.APPLICATIONS,
        Routes.FAVORITES,
        Routes.PROFILE,
    )

    val shouldShowBottomBar = currentDestination?.route in bottomBarScreens

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                ) {
                    val items = listOf(
                        BottomNavItem.Applications,
                        BottomNavItem.Favorites,
                        BottomNavItem.Profile,
                    )
                    items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                if (item.route == Routes.FAVORITES) {
                                    favoriteApplicationsViewModel.onIntent(FavoriteApplicationsIntent.Load)
                                }
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            loginViewModel = loginViewModel,
            registerViewModel = registerViewModel,
            profileViewModel = profileViewModel,
            applicationsViewModel = applicationsViewModel,
            favoriteApplicationsViewModel = favoriteApplicationsViewModel,
            applicationDetailViewModel = applicationDetailViewModel,
            applicationFormViewModel = applicationFormViewModel,
            publicProfileViewModel = publicProfileViewModel,
            startDestination = if (isUserLoggedIn) Routes.APPLICATIONS else Routes.LOGIN,
        )
    }
}
