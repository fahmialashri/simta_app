package com.project.bimbingan

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.data.model.ChapterSubmission
import com.project.data.model.ThesisChapter
import com.project.data.repository.BimbinganRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BimbinganUiState(
    val isLoading: Boolean = false,
    val chapters: List<ThesisChapter> = emptyList(),
    val submissions: List<ChapterSubmission> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class BimbinganViewModel : ViewModel() {

    private val repository = BimbinganRepository()

    private val _uiState = MutableStateFlow(BimbinganUiState())
    val uiState: StateFlow<BimbinganUiState> = _uiState

    fun loadChapters(
        studentId: String?,
        supervisorRequestId: Long?
    ) {
        viewModelScope.launch {
            try {
                if (studentId.isNullOrBlank() || supervisorRequestId == null) {
                    _uiState.value = BimbinganUiState(
                        errorMessage = "Bimbingan belum tersedia. Pengajuan harus disetujui dulu."
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

                val chapters = repository.getChapters(
                    studentId = studentId,
                    supervisorRequestId = supervisorRequestId
                )

                _uiState.value = BimbinganUiState(
                    isLoading = false,
                    chapters = chapters
                )
            } catch (e: Exception) {
                _uiState.value = BimbinganUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengambil data bimbingan"
                )
            }
        }
    }

    fun loadSubmissions(chapterId: Long) {
        viewModelScope.launch {
            try {
                val submissions = repository.getSubmissions(chapterId)

                _uiState.value = _uiState.value.copy(
                    submissions = submissions,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Gagal mengambil log file"
                )
            }
        }
    }

    fun submitChapter(
        context: Context,
        studentId: String?,
        supervisorRequestId: Long?,
        chapterId: Long,
        fileUri: Uri?,
        fileName: String?,
        mimeType: String?,
        driveUrl: String?,
        note: String?
    ) {
        viewModelScope.launch {
            try {
                if (studentId.isNullOrBlank() || supervisorRequestId == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "User atau data pengajuan belum valid."
                    )
                    return@launch
                }

                if (fileUri == null && driveUrl.isNullOrBlank()) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Upload file atau isi link Google Drive terlebih dahulu."
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null,
                    successMessage = null
                )

                var uploadedFileName: String? = null
                var uploadedFilePath: String? = null
                var uploadedFileUrl: String? = null

                if (fileUri != null) {
                    val bytes = context.contentResolver
                        .openInputStream(fileUri)
                        ?.use { it.readBytes() }
                        ?: throw IllegalStateException("Gagal membaca file")

                    val finalFileName = fileName ?: "dokumen_${System.currentTimeMillis()}"

                    val result = repository.uploadChapterFile(
                        studentId = studentId,
                        chapterId = chapterId,
                        fileName = finalFileName,
                        bytes = bytes,
                        mimeType = mimeType
                    )

                    uploadedFileName = finalFileName
                    uploadedFilePath = result.first
                    uploadedFileUrl = result.second
                }

                repository.createSubmission(
                    chapterId = chapterId,
                    studentId = studentId,
                    supervisorRequestId = supervisorRequestId,
                    fileName = uploadedFileName,
                    filePath = uploadedFilePath,
                    fileUrl = uploadedFileUrl,
                    driveUrl = driveUrl?.trim()?.ifBlank { null },
                    note = note?.trim()?.ifBlank { null }
                )

                val chapters = repository.getChapters(studentId, supervisorRequestId)
                val submissions = repository.getSubmissions(chapterId)

                _uiState.value = BimbinganUiState(
                    isLoading = false,
                    chapters = chapters,
                    submissions = submissions,
                    successMessage = "Dokumen bimbingan berhasil dikirim."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mengirim dokumen"
                )
            }
        }
    }
}