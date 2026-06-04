package com.project.lecturer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.repository.LecturerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LecturerViewModel : ViewModel() {

    private val repository = LecturerRepository()

    private val _uiState = MutableStateFlow(LecturerUiState())
    val uiState: StateFlow<LecturerUiState> = _uiState

    private var currentKaprodiDepartmentId: Long? = null

    fun loadLecturersByDepartment(departmentId: Long?) {
        viewModelScope.launch {
            try {
                if (departmentId == null) {
                    _uiState.value = LecturerUiState(
                        isLoading = false,
                        lecturers = emptyList(),
                        errorMessage = "Program studi mahasiswa belum ditemukan."
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    lecturers = emptyList(),
                    errorMessage = null,
                    successMessage = null
                )

                val lecturers = repository.getLecturersByDepartment(departmentId)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = lecturers,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = emptyList(),
                    errorMessage = e.message ?: "Gagal mengambil data dosen"
                )
            }
        }
    }

    fun loadAllLecturersForKaprodi(
        departmentId: Long? = null
    ) {
        currentKaprodiDepartmentId = departmentId

        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                val lecturers = repository.getAllLecturersForKaprodi(
                    departmentId = departmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = lecturers,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengambil data dosen"
                )
            }
        }
    }

    fun loadLecturersByDepartmentAndExpertise(
        departmentId: Long?,
        expertise: String?
    ) {
        viewModelScope.launch {
            try {
                if (departmentId == null) {
                    _uiState.value = LecturerUiState(
                        isLoading = false,
                        lecturers = emptyList(),
                        errorMessage = "Program studi mahasiswa belum ditemukan."
                    )
                    return@launch
                }

                if (expertise.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        lecturers = emptyList(),
                        errorMessage = null
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    lecturers = emptyList(),
                    errorMessage = null,
                    successMessage = null
                )

                val lecturers = repository.getLecturersByDepartmentAndExpertise(
                    departmentId = departmentId,
                    expertise = expertise
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = lecturers,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = emptyList(),
                    errorMessage = e.message ?: "Gagal mengambil data dosen"
                )
            }
        }
    }

    fun loadRecommendations(
        departmentId: Long?,
        expertise: String?,
        excludeLecturerId: Long? = null
    ) {
        viewModelScope.launch {
            try {
                if (departmentId == null) {
                    _uiState.value = _uiState.value.copy(
                        recommendations = emptyList(),
                        errorMessage = "Program studi belum ditemukan.",
                        successMessage = null
                    )
                    return@launch
                }

                val recommendations = repository.getAvailableRecommendations(
                    departmentId = departmentId,
                    expertise = expertise,
                    excludeLecturerId = excludeLecturerId
                )

                _uiState.value = _uiState.value.copy(
                    recommendations = recommendations,
                    errorMessage = null,
                    successMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    recommendations = emptyList(),
                    errorMessage = e.message ?: "Gagal mengambil rekomendasi dosen",
                    successMessage = null
                )
            }
        }
    }

    fun addLecturer(
        departmentId: Long,
        name: String,
        title: String?,
        expertise: String?,
        quota: Int
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.addLecturer(
                    departmentId = departmentId,
                    name = name,
                    title = title,
                    expertise = expertise,
                    quota = quota
                )

                currentKaprodiDepartmentId = departmentId

                val lecturers = repository.getAllLecturersForKaprodi(
                    departmentId = departmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = lecturers,
                    errorMessage = null,
                    successMessage = "Dosen berhasil ditambahkan"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menambahkan dosen",
                    successMessage = null
                )
            }
        }
    }

    fun updateQuota(
        lecturerId: Long,
        quota: Int
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.updateLecturerQuota(
                    lecturerId = lecturerId,
                    quota = quota
                )

                val lecturers = repository.getAllLecturersForKaprodi(
                    departmentId = currentKaprodiDepartmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = lecturers,
                    errorMessage = null,
                    successMessage = "Kuota dosen berhasil diperbarui"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memperbarui kuota",
                    successMessage = null
                )
            }
        }
    }

    fun updateCurrentStudents(
        lecturerId: Long,
        currentStudents: Int
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.updateLecturerCurrentStudents(
                    lecturerId = lecturerId,
                    currentStudents = currentStudents
                )

                val lecturers = repository.getAllLecturersForKaprodi(
                    departmentId = currentKaprodiDepartmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = lecturers,
                    errorMessage = null,
                    successMessage = "Jumlah mahasiswa bimbingan berhasil diperbarui"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memperbarui jumlah mahasiswa",
                    successMessage = null
                )
            }
        }
    }

    fun deactivateLecturer(lecturerId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.deactivateLecturer(lecturerId)

                val lecturers = repository.getAllLecturersForKaprodi(
                    departmentId = currentKaprodiDepartmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = lecturers,
                    errorMessage = null,
                    successMessage = "Dosen berhasil dinonaktifkan"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal menonaktifkan dosen",
                    successMessage = null
                )
            }
        }
    }

    fun reactivateLecturer(lecturerId: Long) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                repository.reactivateLecturer(lecturerId)

                val lecturers = repository.getAllLecturersForKaprodi(
                    departmentId = currentKaprodiDepartmentId
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    lecturers = lecturers,
                    errorMessage = null,
                    successMessage = "Dosen berhasil diaktifkan kembali"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengaktifkan dosen",
                    successMessage = null
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

    fun loadLecturers() {
        loadLecturersByDepartment(1L)
    }
}