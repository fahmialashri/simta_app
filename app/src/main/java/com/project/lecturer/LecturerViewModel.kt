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

    fun loadLecturers() {
        viewModelScope.launch {
            try {
                _uiState.value = LecturerUiState(isLoading = true)

                val lecturers = repository.getInformatikaLecturers()

                _uiState.value = LecturerUiState(
                    isLoading = false,
                    lecturers = lecturers
                )
            } catch (e: Exception) {
                _uiState.value = LecturerUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengambil data dosen"
                )
            }
        }
    }
}