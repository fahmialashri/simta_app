package com.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Lecturer(
    val id: Long,

    @SerialName("department_id")
    val departmentId: Long,

    val name: String,
    val title: String? = null,
    val expertise: String? = null,
    val quota: Int = 5,

    @SerialName("current_students")
    val currentStudents: Int = 0
) {
    val fullName: String
        get() = if (title.isNullOrBlank()) name else "$name, $title"

    val remainingQuota: Int
        get() = quota - currentStudents

    val isAvailable: Boolean
        get() = remainingQuota > 0
}