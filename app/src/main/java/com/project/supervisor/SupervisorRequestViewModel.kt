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
        reason: String?,
        phone: String?,
        estimatedCompletion: String?,
        buktiKrsFileName: String?,
        buktiKrsMimeType: String?,
        buktiKrsBytes: ByteArray?
    ) {
        viewModelScope.launch {
            try {
                if (studentId.isNullOrBlank()) {
                    _uiState.value = SupervisorRequestUiState(
                        errorMessage = "User belum terbaca. Silakan login ulang."
                    )
                    return@launch
                }

                if (lecturerId == 0L) {
                    _uiState.value = SupervisorRequestUiState(
                        errorMessage = "Dosen pembimbing wajib dipilih."
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

                if (buktiKrsBytes == null || buktiKrsBytes.isEmpty()) {
                    _uiState.value = SupervisorRequestUiState(
                        errorMessage = "Bukti KRS wajib diunggah."
                    )
                    return@launch
                }

                _uiState.value = SupervisorRequestUiState(isLoading = true)

                val uploadedKrsUrl = repository.uploadBuktiKrs(
                    studentId = studentId,
                    fileName = buktiKrsFileName ?: "bukti_krs.pdf",
                    mimeType = buktiKrsMimeType ?: "application/pdf",
                    fileBytes = buktiKrsBytes
                )

                val finalMessage = buildString {
                    appendLine("Alasan Pengajuan:")
                    appendLine(reason?.trim().orEmpty().ifBlank { "-" })
                    appendLine()
                    appendLine("No. HP/WA:")
                    appendLine(phone?.trim().orEmpty().ifBlank { "-" })
                    appendLine()
                    appendLine("Estimasi Penyelesaian BAB 1-3:")
                    appendLine(estimatedCompletion?.trim().orEmpty().ifBlank { "-" })
                    appendLine()
                    appendLine("Bukti KRS:")
                    append(uploadedKrsUrl)
                }

                repository.createRequest(
                    studentId = studentId,
                    lecturerId = lecturerId,
                    title = title.trim(),
                    topic = topic.trim(),
                    message = finalMessage
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