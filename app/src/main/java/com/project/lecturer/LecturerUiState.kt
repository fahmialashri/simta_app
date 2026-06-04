package com.project.lecturer

import com.project.data.model.Lecturer

data class LecturerUiState(
    val isLoading: Boolean = false,
    val lecturers: List<Lecturer> = emptyList(),
    val recommendations: List<Lecturer> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)