package com.project.supervisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.repository.SupervisorRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SupervisorRequestViewModel : ViewModel() {

    private val repository = SupervisorRequestRepository()

    private val _uiState = MutableStateFlow(SupervisorRequestUiState())
    val uiState: StateFlow<SupervisorRequestUiState> = _uiState

    fun loadMyRequestProgress(studentId: String?) {
        viewModelScope.launch {
            try {
                if (studentId.isNullOrBlank()) return@launch

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val request = repository.getMyLatestRequest(studentId)
                val lecturer = request?.let {
                    repository.getLecturerById(it.lecturerId)
                }

                val chapters = if (request?.status == "accepted") {
                    repository.getChaptersByRequest(
                        studentId = studentId,
                        requestId = request.id
                    )
                } else {
                    emptyList()
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    activeRequest = request,
                    activeLecturer = lecturer,
                    chapters = chapters,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengambil progress pengajuan"
                )
            }
        }
    }

    fun submitRequest(
        studentId: String?,
        lecturerId: Long,
        title: String,
        topic: String,
        message: String?
    ) {
        viewModelScope.launch {
            try {
                if (studentId.isNullOrBlank()) {
                    _uiState.value = SupervisorRequestUiState(
                        errorMessage = "User belum terbaca. Silakan login ulang."
                    )
                    return@launch
                }

                if (title.isBlank()) {
                    _uiState.value = SupervisorRequestUiState(
                        errorMessage = "Judul skripsi wajib diisi."
                    )
                    return@launch
                }

                if (topic.isBlank()) {
                    _uiState.value = SupervisorRequestUiState(
                        errorMessage = "Topik skripsi wajib diisi."
                    )
                    return@launch
                }

                _uiState.value = SupervisorRequestUiState(isLoading = true)

                repository.createRequest(
                    studentId = studentId,
                    lecturerId = lecturerId,
                    title = title,
                    topic = topic,
                    message = message
                )

                _uiState.value = SupervisorRequestUiState(
                    isLoading = false,
                    isSuccess = true
                )
            } catch (e: Exception) {
                _uiState.value = SupervisorRequestUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengajukan dosen pembimbing"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = SupervisorRequestUiState()
    }
}