package com.project.dosen

import com.project.data.model.DosenReviewItem
import com.project.data.model.DosenSupervisedStudentItem



data class DosenDashboardUiState(
    val isLoading: Boolean = false,
    val supervisedStudents: List<DosenSupervisedStudentItem> = emptyList(),
    val reviews: List<DosenReviewItem> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)