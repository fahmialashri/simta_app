package com.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThesisSubmission(
    val id: String,

    @SerialName("student_id")
    val studentId: String,

    val stage: String,

    @SerialName("student_name")
    val studentName: String? = null,

    val nim: String? = null,
    val phone: String? = null,
    val title: String? = null,

    @SerialName("title_english")
    val titleEnglish: String? = null,

    @SerialName("supervisor_1")
    val supervisor1: String? = null,

    @SerialName("supervisor_2")
    val supervisor2: String? = null,

    @SerialName("examiner_1")
    val examiner1: String? = null,

    @SerialName("examiner_2")
    val examiner2: String? = null,

    val status: String = "menunggu_review",

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)

@Serializable
data class ThesisSubmissionInsert(
    @SerialName("student_id")
    val studentId: String,

    val stage: String,

    @SerialName("student_name")
    val studentName: String? = null,

    val nim: String? = null,
    val phone: String? = null,
    val title: String? = null,

    @SerialName("title_english")
    val titleEnglish: String? = null,

    @SerialName("supervisor_1")
    val supervisor1: String? = null,

    @SerialName("supervisor_2")
    val supervisor2: String? = null,

    @SerialName("examiner_1")
    val examiner1: String? = null,

    @SerialName("examiner_2")
    val examiner2: String? = null,

    val status: String = "menunggu_review"
)

@Serializable
data class ThesisSubmissionDocument(
    val id: String,

    @SerialName("submission_id")
    val submissionId: String,

    @SerialName("document_key")
    val documentKey: String,

    @SerialName("document_name")
    val documentName: String,

    @SerialName("file_url")
    val fileUrl: String,

    val status: String = "menunggu_review",

    @SerialName("created_at")
    val createdAt: String? = null
)

@Serializable
data class ThesisSubmissionDocumentInsert(
    @SerialName("submission_id")
    val submissionId: String,

    @SerialName("document_key")
    val documentKey: String,

    @SerialName("document_name")
    val documentName: String,

    @SerialName("file_url")
    val fileUrl: String,

    val status: String = "menunggu_review"
)