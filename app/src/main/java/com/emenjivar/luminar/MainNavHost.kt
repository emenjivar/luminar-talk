package com.emenjivar.luminar

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.emenjivar.luminar.screen.camera.CameraScreen
import com.emenjivar.luminar.screen.camera.HomeRoute
import com.emenjivar.luminar.screen.settings.SettingsRoute
import com.emenjivar.luminar.screen.settings.SettingsScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = HomeRoute) {
        composable<HomeRoute> {
            CameraScreen(navController = navController)
        }
        composable<SettingsRoute> {
            SettingsScreen(navController = navController)
        }
    }
}
