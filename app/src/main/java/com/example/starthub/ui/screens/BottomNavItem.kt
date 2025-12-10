package com.example.starthub.ui.screens


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomNavItem(val route: String,
                           val title: String,
                           val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Catalogue : BottomNavItem("catalogue", "Catalogue", Icons.Default.ShoppingCart)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)

}

sealed class AuthRoute(val route: String) {
    object SignIn : AuthRoute("sign_in")
    object SignUp : AuthRoute("sign_up")
    object ForgotPassword : AuthRoute("forgot_password")
}