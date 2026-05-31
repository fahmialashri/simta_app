package com.project.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.core.SupabaseClientProvider
import com.project.data.repository.ProfileRepository
import com.project.data.repository.SiskaRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthViewModel : ViewModel() {

    private val supabase = SupabaseClientProvider.client
    private val profileRepository = ProfileRepository()
    private val siskaRepository = SiskaRepository()

    private val _uiState = MutableStateFlow(AuthUiState(isLoading = true))
    val uiState: StateFlow<AuthUiState> = _uiState

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    fun checkSession() {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState(isLoading = true)

                val user = supabase.auth.currentUserOrNull()

                if (user == null) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        isLoggedIn = false
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
                    lecturerId = profile.lecturerId,
                    facultyId = profile.facultyId,
                    departmentId = profile.departmentId
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
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
                val cleanEmail = email.trim()

                if (cleanEmail.isBlank()) {
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
                    this.email = cleanEmail
                    this.password = password
                }

                checkSession()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = e.message ?: "Login gagal"
                )
            }
        }
    }

    fun registerMahasiswa(
        fullName: String,
        email: String,
        password: String,
        nim: String,
        departmentId: Long
    ) {
        viewModelScope.launch {
            try {
                val cleanFullName = fullName.trim()
                val cleanEmail = email.trim()
                val cleanNim = nim.trim()

                if (cleanFullName.isBlank()) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "Nama lengkap wajib diisi."
                    )
                    _toastMessage.emit("Nama lengkap wajib diisi.")
                    return@launch
                }

                if (cleanEmail.isBlank()) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "Email wajib diisi."
                    )
                    _toastMessage.emit("Email wajib diisi.")
                    return@launch
                }

                if (cleanNim.isBlank()) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "NIM wajib diisi."
                    )
                    _toastMessage.emit("NIM wajib diisi.")
                    return@launch
                }

                if (!cleanNim.matches(Regex("^[0-9]{8,20}$"))) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "Format NIM tidak valid."
                    )
                    _toastMessage.emit("Format NIM tidak valid.")
                    return@launch
                }

                if (password.length < 6) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = "Password minimal 6 karakter."
                    )
                    _toastMessage.emit("Password minimal 6 karakter.")
                    return@launch
                }

                _uiState.value = AuthUiState(isLoading = true)

                _toastMessage.emit("Mengecek NIM ke SISKA...")

                val siskaResult = siskaRepository.validateNim(cleanNim)

                if (!siskaResult.valid) {
                    _uiState.value = AuthUiState(
                        isLoading = false,
                        errorMessage = siskaResult.message
                    )

                    _toastMessage.emit(siskaResult.message)
                    return@launch
                }

                _toastMessage.emit("NIM terdaftar sebagai mahasiswa UNSIKA.")

                delay(400)

                _toastMessage.emit("Membuat akun mahasiswa...")

                supabase.auth.signUpWith(Email) {
                    this.email = cleanEmail
                    this.password = password

                    data = buildJsonObject {
                        put("full_name", cleanFullName)
                        put("role", "mahasiswa")
                        put("nim", cleanNim)
                        put("nidn", null as String?)
                        put("lecturer_id", null as Long?)

                        put("faculty_id", 1L)
                        put("department_id", departmentId)

                        put("siska_verified", true)
                        put("siska_validation_note", "Valid from SISKA")
                    }
                }

                _toastMessage.emit("Pendaftaran berhasil.")

                delay(700)
                checkSession()
            } catch (e: Exception) {
                val message = e.message ?: "Register gagal"

                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = message
                )

                _toastMessage.emit(message)
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
                    errorMessage = null
                )

                onSuccess?.invoke()
            } catch (e: Exception) {
                val message = e.message ?: "Logout gagal"

                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = message
                )

                _toastMessage.emit(message)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}