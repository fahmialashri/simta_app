package com.project.supervisor

import com.project.data.model.Lecturer
import com.project.data.model.SupervisorRequest
import com.project.data.model.ThesisChapter

data class SupervisorRequestUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,

    val activeRequest: SupervisorRequest? = null,
    val activeLecturer: Lecturer? = null,
    val chapters: List<ThesisChapter> = emptyList(),

    val errorMessage: String? = null
)