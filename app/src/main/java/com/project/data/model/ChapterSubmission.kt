package com.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChapterSubmission(
    val id: Long,

    @SerialName("chapter_id")
    val chapterId: Long,

    @SerialName("student_id")
    val studentId: String,

    @SerialName("supervisor_request_id")
    val supervisorRequestId: Long,

    @SerialName("file_name")
    val fileName: String? = null,

    @SerialName("file_path")
    val filePath: String? = null,

    @SerialName("file_url")
    val fileUrl: String? = null,

    @SerialName("drive_url")
    val driveUrl: String? = null,

    val note: String? = null
)

@Serializable
data class ChapterSubmissionInsert(
    @SerialName("chapter_id")
    val chapterId: Long,

    @SerialName("student_id")
    val studentId: String,

    @SerialName("supervisor_request_id")
    val supervisorRequestId: Long,

    @SerialName("file_name")
    val fileName: String? = null,

    @SerialName("file_path")
    val filePath: String? = null,

    @SerialName("file_url")
    val fileUrl: String? = null,

    @SerialName("drive_url")
    val driveUrl: String? = null,

    val note: String? = null
)