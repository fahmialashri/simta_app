package com.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThesisChapter(
    val id: Long,

    @SerialName("student_id")
    val studentId: String,

    @SerialName("supervisor_request_id")
    val supervisorRequestId: Long,

    @SerialName("chapter_number")
    val chapterNumber: Int,

    val title: String,
    val status: String,

    @SerialName("file_url")
    val fileUrl: String? = null,

    @SerialName("student_note")
    val studentNote: String? = null,

    @SerialName("lecturer_note")
    val lecturerNote: String? = null
)