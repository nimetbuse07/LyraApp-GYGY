package com.nimetatila.lyraapp.ui.auth.navigation

sealed class LyraDestination(val route: String) {
    data object Login : LyraDestination("login")
    data object Register : LyraDestination("register")
    data object Home : LyraDestination("home")
}