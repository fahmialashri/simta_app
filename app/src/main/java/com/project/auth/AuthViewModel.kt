package com.project.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.core.SupabaseClientProvider
import com.project.data.repository.ProfileRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthViewModel : ViewModel() {

    private val supabase = SupabaseClientProvider.client
    private val profileRepository = ProfileRepository()

    private val _uiState = MutableStateFlow(AuthUiState(isLoading = true))
    val uiState: StateFlow<AuthUiState> = _uiState

    fun checkSession() {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState(isLoading = true)

                val user = supabase.auth.currentUserOrNull()

                if (user == null) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isLoggedIn = false,
                        userId = null,
                        role = null,
                        name = null,
                        email = null,
                        nim = null,
                        nidn = null,
                        lecturerId = null
                    )
                    return@launch
                }

                val profile = profileRepository.getMyProfile(user.id)

                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = true,
                    userId = profile.id,
                    role = profile.role,
                    name = profile.fullName,
                    email = profile.email,
                    nim = profile.nim,
                    nidn = profile.nidn,
                    lecturerId = profile.lecturerId
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    userId = null,
                    role = null,
                    name = null,
                    email = null,
                    nim = null,
                    nidn = null,
                    lecturerId = null,
                    errorMessage = e.message ?: "Gagal cek session"
                )
            }
        }
    }

    fun login(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            try {
                if (email.isBlank()) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "Email wajib diisi."
                    )
                    return@launch
                }

                if (password.isBlank()) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "Password wajib diisi."
                    )
                    return@launch
                }

                _uiState.value = AuthUiState(isLoading = true)

                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                checkSession()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    userId = null,
                    role = null,
                    name = null,
                    email = null,
                    nim = null,
                    nidn = null,
                    lecturerId = null,
                    errorMessage = e.message ?: "Login gagal"
                )
            }
        }
    }

    fun registerMahasiswa(
        fullName: String,
        email: String,
        password: String,
        nim: String
    ) {
        viewModelScope.launch {
            try {
                if (fullName.isBlank()) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "Nama lengkap wajib diisi."
                    )
                    return@launch
                }

                if (email.isBlank()) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "Email wajib diisi."
                    )
                    return@launch
                }

                if (nim.isBlank()) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "NIM wajib diisi."
                    )
                    return@launch
                }

                if (password.length < 6) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "Password minimal 6 karakter."
                    )
                    return@launch
                }

                _uiState.value = AuthUiState(isLoading = true)

                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password

                    data = buildJsonObject {
                        put("full_name", fullName)
                        put("role", "mahasiswa")
                        put("nim", nim)
                        put("nidn", null as String?)
                        put("lecturer_id", null as Long?)
                    }
                }

                delay(700)
                checkSession()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    userId = null,
                    role = null,
                    name = null,
                    email = null,
                    nim = null,
                    nidn = null,
                    lecturerId = null,
                    errorMessage = e.message ?: "Register gagal"
                )
            }
        }
    }

    fun logout(
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                supabase.auth.signOut()

                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    userId = null,
                    role = null,
                    name = null,
                    email = null,
                    nim = null,
                    nidn = null,
                    lecturerId = null,
                    errorMessage = null
                )

                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    userId = null,
                    role = null,
                    name = null,
                    email = null,
                    nim = null,
                    nidn = null,
                    lecturerId = null,
                    errorMessage = e.message ?: "Logout gagal"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}