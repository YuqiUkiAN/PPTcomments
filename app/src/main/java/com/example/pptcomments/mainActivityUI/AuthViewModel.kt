package com.example.pptcomments.mainActivityUI

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {
    private val auth = Firebase.auth

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

    init {
        login()
    }

    fun login() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val result = auth.signInAnonymously().await()
                _authState.value = if (result.user != null) AuthState.LoggedIn else AuthState.LoginFailed
            } catch (e: Exception) {
                _authState.value = AuthState.LoginFailed
            }
        }
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object LoggedIn : AuthState()
    object LoginFailed : AuthState()
}
