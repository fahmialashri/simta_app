package com.project.upload

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.repository.ThesisDocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UploadBerkasUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val uploadedCount: Int = 0
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
            _uiState.value = UploadBerkasUiState(
                errorMessage = "User belum login"
            )
            return
        }

        if (files.isEmpty()) {
            _uiState.value = UploadBerkasUiState(
                errorMessage = "Belum ada file yang dipilih"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = UploadBerkasUiState(
                isLoading = true
            )

            val result = repository.uploadMultipleDocuments(
                context = context,
                userId = userId,
                stage = stage,
                files = files
            )

            if (result.isSuccess) {
                _uiState.value = UploadBerkasUiState(
                    isSuccess = true,
                    uploadedCount = result.getOrThrow().size
                )
            } else {
                _uiState.value = UploadBerkasUiState(
                    errorMessage = result.exceptionOrNull()?.message
                        ?: "Gagal upload berkas"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = UploadBerkasUiState()
    }
}