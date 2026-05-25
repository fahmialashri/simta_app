package com.project.kaprodi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.repository.KaprodiRepository
import com.project.data.repository.KaprodiSubmissionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KaprodiUiState(
    val isLoading: Boolean = false,
    val submissions: List<KaprodiSubmissionData> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class KaprodiViewModel : ViewModel() {

    private val repository = KaprodiRepository()

    private val _uiState = MutableStateFlow(KaprodiUiState())
    val uiState: StateFlow<KaprodiUiState> = _uiState.asStateFlow()

    fun loadPendingRequests() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                val data = repository.getPendingSupervisorRequests()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = data
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memuat data pengajuan"
                )
            }
        }
    }

    fun approveRequest(requestId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.approveRequest(requestId)

                val data = repository.getPendingSupervisorRequests()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = data,
                    successMessage = "Pengajuan berhasil disetujui"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menyetujui pengajuan"
                )
            }
        }
    }

    fun rejectRequest(requestId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.rejectRequest(requestId)

                val data = repository.getPendingSupervisorRequests()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = data,
                    successMessage = "Pengajuan berhasil ditolak"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menolak pengajuan"
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