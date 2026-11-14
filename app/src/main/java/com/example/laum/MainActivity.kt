package com.example.laum

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.laum.data.AuthRepository
import com.example.laum.ui.home.HomeScreen
import com.example.laum.ui.login.LoginScreen
import com.example.laum.ui.register.RegisterScreen
import com.example.laum.ui.theme.LaumTheme
import com.example.laum.ui.welcome.WelcomeScreen

private enum class Screen { Welcome, Login, Register, Home }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaumTheme {
                var screen by remember {
                    mutableStateOf(if (AuthRepository.isLoggedIn()) Screen.Home else Screen.Welcome)
                }

                when (screen) {
                    Screen.Welcome -> WelcomeScreen(
                        onLoginClick = { screen = Screen.Login },
                        onRegisterClick = { screen = Screen.Register }
                    )

                    Screen.Login -> {
                        BackHandler { screen = Screen.Welcome }
                        LoginScreen(
                            onClose = { screen = Screen.Welcome },
                            onRegisterClick = { screen = Screen.Register },
                            onLogin = { username, password, done ->
                                AuthRepository.login(username, password) { error ->
                                    if (error == null) screen = Screen.Home
                                    done(error)
                                }
                            }
                        )
                    }

                    Screen.Register -> {
                        BackHandler { screen = Screen.Welcome }
                        RegisterScreen(
                            onClose = { screen = Screen.Welcome },
                            onRegister = { username, password, campus, major, email, done ->
                                AuthRepository.register(username, password, campus, major, email) { error ->
                                    if (error == null) screen = Screen.Home
                                    done(error)
                                }
                            }
                        )
                    }

                    Screen.Home -> HomeScreen(
                        onLogout = {
                            AuthRepository.logout()
                            screen = Screen.Welcome
                        }
                    )
                }
            }
        }
    }
}