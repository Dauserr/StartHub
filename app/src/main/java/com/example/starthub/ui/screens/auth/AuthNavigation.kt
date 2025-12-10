package com.example.starthub.ui.screens.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.starthub.ui.screens.AuthRoute

@Composable
fun AuthNavigation(
    onAuthSuccess: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AuthRoute.SignIn.route
    ) {
        composable(AuthRoute.SignIn.route) {
            SignInScreen(
                onSignInClick = { email, password ->
                    // Handle sign in logic here
                    // For now, just navigate to main screen
                    onAuthSuccess()
                },
                onSignUpClick = {
                    navController.navigate(AuthRoute.SignUp.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(AuthRoute.ForgotPassword.route)
                }
            )
        }

        composable(AuthRoute.SignUp.route) {
            SignUpScreen(
                onSignUpClick = { name, email, password ->
                    // Handle sign up logic here
                    // For now, just navigate to main screen
                    onAuthSuccess()
                },
                onSignInClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(AuthRoute.ForgotPassword.route) {
            ForgotPasswordScreen(
                onResetClick = { email ->
                    // Handle password reset logic here
                    // Show success message and go back
                    navController.popBackStack()
                },
                onBackToSignInClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}