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

                _uiState.value = LecturerUiState(
                    isLoading = true,
                    lecturers = emptyList(),
                    errorMessage = null
                )

                val lecturers = repository.getLecturersByDepartment(departmentId)

                _uiState.value = LecturerUiState(
                    isLoading = false,
                    lecturers = lecturers,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = LecturerUiState(
                    isLoading = false,
                    lecturers = emptyList(),
                    errorMessage = e.message ?: "Gagal mengambil data dosen"
                )
            }
        }
    }

    fun loadLecturers() {
        loadLecturersByDepartment(1L)
    }
}