package com.project.dosen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.repository.DosenRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DosenDashboardViewModel : ViewModel() {

    private val repository = DosenRequestRepository()

    private val _uiState = MutableStateFlow(DosenDashboardUiState())
    val uiState: StateFlow<DosenDashboardUiState> = _uiState

    fun loadDashboard(
        lecturerId: Long?
    ) {
        viewModelScope.launch {
            try {
                if (lecturerId == null) {
                    _uiState.value = DosenDashboardUiState(
                        isLoading = false,
                        errorMessage = "Akun dosen belum terhubung ke data dosen."
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                val supervisedStudents = repository.getSupervisedStudents(lecturerId)
                val reviews = repository.getReviewItems(lecturerId)

                _uiState.value = DosenDashboardUiState(
                    isLoading = false,
                    supervisedStudents = supervisedStudents,
                    reviews = reviews,
                    errorMessage = null,
                    successMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = DosenDashboardUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengambil data dashboard dosen"
                )
            }
        }
    }

    fun approveChapter(
        chapterId: Long,
        lecturerId: Long?,
        note: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.approveChapter(
                    chapterId = chapterId,
                    note = note
                )

                val currentLecturerId = lecturerId

                if (currentLecturerId != null) {
                    val supervisedStudents = repository.getSupervisedStudents(currentLecturerId)
                    val reviews = repository.getReviewItems(currentLecturerId)

                    _uiState.value = DosenDashboardUiState(
                        isLoading = false,
                        supervisedStudents = supervisedStudents,
                        reviews = reviews,
                        successMessage = "BAB berhasil disetujui."
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "BAB berhasil disetujui."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menyetujui BAB"
                )
            }
        }
    }

    fun requestRevision(
        chapterId: Long,
        lecturerId: Long?,
        note: String?
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.requestRevision(
                    chapterId = chapterId,
                    note = note
                )

                val currentLecturerId = lecturerId

                if (currentLecturerId != null) {
                    val supervisedStudents = repository.getSupervisedStudents(currentLecturerId)
                    val reviews = repository.getReviewItems(currentLecturerId)

                    _uiState.value = DosenDashboardUiState(
                        isLoading = false,
                        supervisedStudents = supervisedStudents,
                        reviews = reviews,
                        successMessage = "BAB berhasil dikembalikan untuk revisi."
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "BAB berhasil dikembalikan untuk revisi."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memberi revisi"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}