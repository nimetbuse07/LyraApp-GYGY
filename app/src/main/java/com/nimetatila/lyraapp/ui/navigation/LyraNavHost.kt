package com.nimetatila.lyraapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nimetatila.lyraapp.ui.auth.login.LoginRoute
import com.nimetatila.lyraapp.ui.auth.register.RegisterRoute

/**
 * Uygulamanın iskelet navigasyon yapısı.
 *
 * Tek [NavHost] Auth grafiğini barındırır; başlangıç hedefi [LyraDestination.Login]'dir.
 * Her ekranın `Route` composable'ı, MVI Effect'lerini buradan sağlanan navigasyon
 * lambda'larına köprüler (ViewModel navigasyon API'si bilmez; bkz. mvi-viewmodel-rules §6).
 *
 * Not: Ana akış (Home) henüz kapsamda değildir; `onNavigateToHome` ileride Home grafiği
 * eklendiğinde bağlanacaktır.
 */
@Composable
fun LyraNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = LyraDestination.Login.route,
        modifier = modifier,
    ) {
        composable(LyraDestination.Login.route) {
            LoginRoute(
                onNavigateToHome = { /* TODO: Home grafiği eklenince bağlanacak. */ },
                onNavigateToRegister = {
                    navController.navigate(LyraDestination.Register.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(LyraDestination.Register.route) {
            RegisterRoute(
                onNavigateToHome = { /* TODO: Home grafiği eklenince bağlanacak. */ },
                onNavigateToLogin = {
                    navController.navigate(LyraDestination.Login.route) {
                        popUpTo(LyraDestination.Login.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}