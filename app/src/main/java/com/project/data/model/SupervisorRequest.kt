package com.project.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SupervisorRequest(
    val id: Long,

    @SerialName("student_id")
    val studentId: String,

    @SerialName("lecturer_id")
    val lecturerId: Long,

    val title: String? = null,
    val topic: String? = null,
    val message: String? = null,
    val status: String = "pending",

    @SerialName("lecturer_note")
    val lecturerNote: String? = null
)

@Serializable
data class SupervisorRequestInsert(
    @SerialName("student_id")
    val studentId: String,

    @SerialName("lecturer_id")
    val lecturerId: Long,

    val title: String,
    val topic: String,
    val message: String? = null,
    val status: String = "pending"
)