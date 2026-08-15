package com.lifescore.app.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lifescore.app.data.remote.repository.AuthRepository
import com.lifescore.app.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val displayName: String = "",
    val isSignUp: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val authenticatedUser: UserProfile? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onDisplayNameChange(displayName: String) {
        _uiState.value = _uiState.value.copy(displayName = displayName, errorMessage = null)
    }

    fun toggleAuthMode() {
        _uiState.value = _uiState.value.copy(
            isSignUp = !_uiState.value.isSignUp,
            errorMessage = null
        )
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun submitEmailAuth(onSuccess: (UserProfile) -> Unit) {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please fill in all fields.")
            return
        }

        if (state.isSignUp && state.displayName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please enter your display name.")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = if (state.isSignUp) {
                authRepository.signUp(state.email.trim(), state.password.trim(), state.displayName.trim())
            } else {
                authRepository.signIn(state.email.trim(), state.password.trim())
            }

            _uiState.value = _uiState.value.copy(isLoading = false)

            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(authenticatedUser = user)
                    onSuccess(user)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.localizedMessage ?: "Authentication failed.")
                }
            )
        }
    }

    fun continueAsGuest(onSuccess: (UserProfile) -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = authRepository.signInAnonymously()
            _uiState.value = _uiState.value.copy(isLoading = false)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(authenticatedUser = user)
                    onSuccess(user)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.localizedMessage ?: "Guest login failed.")
                }
            )
        }
    }

    fun signInWithGoogleToken(idToken: String, onSuccess: (UserProfile) -> Unit) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = authRepository.signInWithGoogle(idToken)
            _uiState.value = _uiState.value.copy(isLoading = false)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(authenticatedUser = user)
                    onSuccess(user)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(errorMessage = error.localizedMessage ?: "Google Sign-In failed.")
                }
            )
        }
    }
}
