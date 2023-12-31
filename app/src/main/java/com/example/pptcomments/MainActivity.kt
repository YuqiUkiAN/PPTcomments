package com.example.pptcomments

import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.example.pptcomments.mainActivityUI.AuthState
import com.example.pptcomments.mainActivityUI.AuthViewModel
import com.example.pptcomments.mainActivityUI.LoadingScreen
import com.example.pptcomments.mainActivityUI.LoginFailedScreen
import com.example.pptcomments.mainActivityUI.MyApp

class MainActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val authState = authViewModel.authState.collectAsState()

            when (authState.value) {
                AuthState.Loading -> LoadingScreen()
                AuthState.LoginFailed -> LoginFailedScreen { authViewModel.login() }
                AuthState.LoggedIn -> MyApp()
            }
        }
    }
}
