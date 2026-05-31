package com.project.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SiskaVerifyRequest(
    val nim: String
)

@Serializable
data class SiskaVerifyResponse(
    val success: Boolean = false,
    val valid: Boolean = false,
    val message: String = "Validasi NIM gagal.",
    val code: String? = null,

    @SerialName("debug_status")
    val debugStatus: Int? = null,

    @SerialName("debug_url")
    val debugUrl: String? = null
)