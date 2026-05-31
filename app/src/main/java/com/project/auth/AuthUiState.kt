package com.project.auth

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userId: String? = null,
    val role: String? = null,
    val name: String? = null,
    val email: String? = null,
    val nim: String? = null,
    val nidn: String? = null,
    val lecturerId: Long? = null,
    val facultyId: Long? = null,
    val departmentId: Long? = null,
    val errorMessage: String? = null
)