package com.project.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.core.SupabaseClientProvider
import com.project.data.repository.ProfileRepository
import com.project.data.repository.SiskaRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.net.URLEncoder

class AuthViewModel : ViewModel() {

    private val supabase = SupabaseClientProvider.client
    private val profileRepository = ProfileRepository()
    private val siskaRepository = SiskaRepository()

    private val authHttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                }
            )
        }
    }

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

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            val cleanEmail = email.trim()

            if (cleanEmail.isBlank()) {
                val message = "Email wajib diisi."

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = message
                )

                _toastMessage.emit(message)
                return@launch
            }

            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val redirectUrl = "simtaapp://reset-password"

                val encodedRedirectUrl = withContext(Dispatchers.IO) {
                    URLEncoder.encode(redirectUrl, "UTF-8")
                }

                val response: HttpResponse = authHttpClient.post(
                    "${SupabaseClientProvider.SUPABASE_URL}/auth/v1/recover?redirect_to=$encodedRedirectUrl"
                ) {
                    header("apikey", SupabaseClientProvider.SUPABASE_KEY)
                    header(
                        HttpHeaders.Authorization,
                        "Bearer ${SupabaseClientProvider.SUPABASE_KEY}"
                    )
                    contentType(ContentType.Application.Json)

                    setBody(
                        buildJsonObject {
                            put("email", cleanEmail)
                        }
                    )
                }

                if (response.status != HttpStatusCode.OK) {
                    throw Exception(
                        readSupabaseError(
                            response = response,
                            fallbackMessage = "Gagal mengirim email reset password."
                        )
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null
                )

                _toastMessage.emit("Link reset password sudah dikirim ke email kamu.")
                onSuccess?.invoke()
            } catch (e: Exception) {
                val message = e.message ?: "Gagal mengirim email reset password."

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = message
                )

                _toastMessage.emit(message)
            }
        }
    }

    fun updatePasswordFromRecoveryLink(
        accessToken: String?,
        newPassword: String,
        confirmPassword: String,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            try {
                val cleanToken = accessToken.orEmpty().trim()

                if (cleanToken.isBlank()) {
                    val message =
                        "Token reset password tidak ditemukan. Silakan buka ulang link dari email atau kirim ulang link reset password."

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )

                    _toastMessage.emit(message)
                    return@launch
                }

                if (newPassword.isBlank()) {
                    val message = "Password baru wajib diisi."

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )

                    _toastMessage.emit(message)
                    return@launch
                }

                if (confirmPassword.isBlank()) {
                    val message = "Konfirmasi password wajib diisi."

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )

                    _toastMessage.emit(message)
                    return@launch
                }

                if (newPassword.length < 6) {
                    val message = "Password minimal 6 karakter."

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )

                    _toastMessage.emit(message)
                    return@launch
                }

                if (newPassword != confirmPassword) {
                    val message = "Konfirmasi password tidak sama."

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )

                    _toastMessage.emit(message)
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val response: HttpResponse = authHttpClient.put(
                    "${SupabaseClientProvider.SUPABASE_URL}/auth/v1/user"
                ) {
                    header("apikey", SupabaseClientProvider.SUPABASE_KEY)
                    header(HttpHeaders.Authorization, "Bearer $cleanToken")
                    contentType(ContentType.Application.Json)

                    setBody(
                        buildJsonObject {
                            put("password", newPassword)
                        }
                    )
                }

                if (response.status != HttpStatusCode.OK) {
                    throw Exception(
                        readSupabaseError(
                            response = response,
                            fallbackMessage = "Gagal mengganti password. Link reset mungkin sudah kadaluarsa atau sudah pernah digunakan."
                        )
                    )
                }

                supabase.auth.signOut()

                _uiState.value = AuthUiState(
                    isLoading = false,
                    isLoggedIn = false,
                    errorMessage = null
                )

                _toastMessage.emit("Password berhasil diganti. Silakan login memakai password baru.")
                onSuccess?.invoke()
            } catch (e: Exception) {
                val message = e.message ?: "Gagal mengganti password."

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = message
                )

                _toastMessage.emit(message)
            }
        }
    }

    private suspend fun readSupabaseError(
        response: HttpResponse,
        fallbackMessage: String
    ): String {
        return try {
            val text = response.body<String>()
            val json = Json.parseToJsonElement(text).jsonObject

            json["msg"]?.jsonPrimitive?.content
                ?: json["message"]?.jsonPrimitive?.content
                ?: json["error_description"]?.jsonPrimitive?.content
                ?: json["error"]?.jsonPrimitive?.content
                ?: fallbackMessage
        } catch (_: Exception) {
            fallbackMessage
        }
    }

    fun resetLoadingState() {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            errorMessage = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}