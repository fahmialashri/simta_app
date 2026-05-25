package com.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThesisChapterUpdate(
    val status: String,

    @SerialName("lecturer_note")
    val lecturerNote: String? = null
)