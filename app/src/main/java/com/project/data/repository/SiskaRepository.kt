package com.project.data.repository

import com.project.core.SupabaseClientProvider
import com.project.data.model.SiskaVerifyRequest
import com.project.data.model.SiskaVerifyResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class SiskaRepository {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                    explicitNulls = false
                }
            )
        }
    }

    suspend fun validateNim(nim: String): SiskaVerifyResponse {
        return try {
            client.post("${SupabaseClientProvider.SUPABASE_URL}/functions/v1/validate-siska-nim") {
                contentType(ContentType.Application.Json)

                header("apikey", SupabaseClientProvider.SUPABASE_KEY)
                bearerAuth(SupabaseClientProvider.SUPABASE_KEY)

                setBody(
                    SiskaVerifyRequest(
                        nim = nim
                    )
                )
            }.body()
        } catch (e: ClientRequestException) {
            try {
                e.response.body()
            } catch (_: Exception) {
                SiskaVerifyResponse(
                    success = false,
                    valid = false,
                    message = "Validasi NIM ditolak oleh server."
                )
            }
        } catch (e: ServerResponseException) {
            try {
                e.response.body()
            } catch (_: Exception) {
                SiskaVerifyResponse(
                    success = false,
                    valid = false,
                    message = "Server validasi SISKA sedang bermasalah."
                )
            }
        } catch (e: Exception) {
            SiskaVerifyResponse(
                success = false,
                valid = false,
                message = "Gagal menghubungi validasi SISKA."
            )
        }
    }
}