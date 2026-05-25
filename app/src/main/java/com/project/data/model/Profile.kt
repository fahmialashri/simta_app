package com.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,

    @SerialName("full_name")
    val fullName: String,

    val email: String,
    val role: String,

    @SerialName("faculty_id")
    val facultyId: Long? = null,

    @SerialName("department_id")
    val departmentId: Long? = null,

    @SerialName("lecturer_id")
    val lecturerId: Long? = null,

    val nim: String? = null,
    val nidn: String? = null,
    val phone: String? = null,

    @SerialName("avatar_url")
    val avatarUrl: String? = null
)