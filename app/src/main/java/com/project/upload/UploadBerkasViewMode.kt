package com.project.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.model.ThesisSubmission
import com.project.data.model.ThesisSubmissionDocument
import com.project.data.repository.ThesisDocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UploadBerkasUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val uploadedCount: Int = 0,
    val submissions: List<ThesisSubmission> = emptyList(),
    val selectedDocuments: List<ThesisSubmissionDocument> = emptyList()
)

class UploadBerkasViewModel : ViewModel() {

    private val repository = ThesisDocumentRepository()

    private val _uiState = MutableStateFlow(UploadBerkasUiState())
    val uiState: StateFlow<UploadBerkasUiState> = _uiState.asStateFlow()

    fun uploadDocuments(
        context: Context,
        userId: String?,
        stage: String,
        files: Map<String, Uri>
    ) {
        if (userId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = "User belum login."
            )
            return
        }

        if (files.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = "Belum ada file yang dipilih."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isSuccess = false,
                errorMessage = null
            )

            val result = repository.uploadMultipleDocuments(
                context = context,
                userId = userId,
                stage = stage,
                files = files
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    uploadedCount = result.getOrThrow().size,
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "Gagal upload berkas."
                )
            }
        }
    }

    fun submitRegistration(
        context: Context,
        userId: String?,
        stage: String,
        studentName: String?,
        nim: String?,
        phone: String?,
        title: String?,
        titleEnglish: String? = null,
        supervisor1: String? = null,
        supervisor2: String? = null,
        examiner1: String? = null,
        examiner2: String? = null,
        files: Map<String, Uri>
    ) {
        if (userId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = "User belum terbaca. Silakan login ulang."
            )
            return
        }

        if (stage.isBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = "Tahap pendaftaran tidak valid."
            )
            return
        }

        if (files.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSuccess = false,
                errorMessage = "Belum ada file yang dipilih."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                isSuccess = false,
                errorMessage = null
            )

            val result = repository.submitThesisRegistration(
                context = context,
                userId = userId,
                stage = stage,
                studentName = studentName,
                nim = nim,
                phone = phone,
                title = title,
                titleEnglish = titleEnglish,
                supervisor1 = supervisor1,
                supervisor2 = supervisor2,
                examiner1 = examiner1,
                examiner2 = examiner2,
                files = files
            )

            if (result.isSuccess) {
                val updatedSubmissions = repository.getSubmissionsByStudentId(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true,
                    uploadedCount = files.size,
                    submissions = updatedSubmissions,
                    errorMessage = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = false,
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "Gagal mengirim pendaftaran."
                )
            }
        }
    }

    fun loadSubmissionsByStage(stage: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isSuccess = false,
                    errorMessage = null
                )

                val submissions = repository.getSubmissionsByStage(stage)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = submissions,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengambil data pendaftaran."
                )
            }
        }
    }

    fun loadMySubmissions(
        userId: String?
    ) {
        if (userId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "User belum terbaca untuk mengambil notifikasi."
            )
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isSuccess = false,
                    errorMessage = null
                )

                val submissions = repository.getSubmissionsByStudentId(userId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = submissions,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengambil notifikasi berkas."
                )
            }
        }
    }

    fun loadPendingSubmissions() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isSuccess = false,
                    errorMessage = null
                )

                val submissions = repository.getPendingSubmissions()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = submissions,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengambil data pending."
                )
            }
        }
    }

    fun loadDocumentsBySubmissionId(submissionId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isSuccess = false,
                    errorMessage = null
                )

                val documents = repository.getDocumentsBySubmissionId(submissionId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedDocuments = documents,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengambil dokumen."
                )
            }
        }
    }

    fun approveSubmission(
        submissionId: String,
        reloadStage: String? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isSuccess = false,
                    errorMessage = null
                )

                repository.approveSubmission(submissionId)

                if (reloadStage != null) {
                    val submissions = repository.getSubmissionsByStage(reloadStage)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submissions = submissions,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menyetujui pendaftaran."
                )
            }
        }
    }

    fun rejectSubmission(
        submissionId: String,
        reloadStage: String? = null
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isSuccess = false,
                    errorMessage = null
                )

                repository.rejectSubmission(submissionId)

                if (reloadStage != null) {
                    val submissions = repository.getSubmissionsByStage(reloadStage)

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submissions = submissions,
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menolak pendaftaran."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isSuccess = false,
            errorMessage = null,
            uploadedCount = 0,
            selectedDocuments = emptyList()
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}