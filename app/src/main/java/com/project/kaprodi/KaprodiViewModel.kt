package com.project.kaprodi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.repository.KaprodiRepository
import com.project.data.repository.KaprodiStudentTrackingData
import com.project.data.repository.KaprodiSubmissionData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class KaprodiUiState(
    val isLoading: Boolean = false,
    val submissions: List<KaprodiSubmissionData> = emptyList(),
    val studentTrackings: List<KaprodiStudentTrackingData> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class KaprodiViewModel : ViewModel() {

    private val repository = KaprodiRepository()

    private val _uiState = MutableStateFlow(KaprodiUiState())
    val uiState: StateFlow<KaprodiUiState> = _uiState.asStateFlow()

    fun loadRequests(departmentId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                val data = repository.getSupervisorRequestsByDepartment(
                    departmentId = departmentId
                )

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

    fun loadStudentTracking(departmentId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                val data = repository.getStudentTrackingByDepartment(
                    departmentId = departmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    studentTrackings = data
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memuat tracking mahasiswa"
                )
            }
        }
    }

    fun saveLecturerRecommendation(
        requestId: Long,
        lecturerId: Long,
        note: String?,
        departmentId: Long
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.saveLecturerRecommendation(
                    requestId = requestId,
                    lecturerId = lecturerId,
                    note = note
                )

                val data = repository.getSupervisorRequestsByDepartment(
                    departmentId = departmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = data,
                    successMessage = "Rekomendasi dosen berhasil disimpan"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menyimpan rekomendasi dosen"
                )
            }
        }
    }

    fun approveRequest(
        requestId: Long,
        departmentId: Long
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.approveRequest(requestId)

                val data = repository.getSupervisorRequestsByDepartment(
                    departmentId = departmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = data,
                    successMessage = "Pengajuan berhasil disetujui dan kuota dosen diperbarui"
                )
            } catch (e: Exception) {
                val data = runCatching {
                    repository.getSupervisorRequestsByDepartment(departmentId)
                }.getOrDefault(_uiState.value.submissions)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = data,
                    errorMessage = e.message ?: "Gagal menyetujui pengajuan"
                )
            }
        }
    }

    fun rejectRequest(
        requestId: Long,
        note: String?,
        departmentId: Long
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.rejectRequest(
                    requestId = requestId,
                    note = note
                )

                val data = repository.getSupervisorRequestsByDepartment(
                    departmentId = departmentId
                )

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

    fun archiveRequest(
        requestId: Long,
        departmentId: Long
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.archiveRequest(requestId)

                val data = repository.getSupervisorRequestsByDepartment(
                    departmentId = departmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    submissions = data,
                    successMessage = "Pengajuan berhasil dihapus dari tampilan"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menghapus pengajuan"
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