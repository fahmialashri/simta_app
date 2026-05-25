package com.project.data.repository

import com.project.core.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class KaprodiRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getPendingSupervisorRequests(): List<KaprodiSubmissionData> {
        val requests = supabase
            .from("supervisor_requests")
            .select {
                filter {
                    eq("status", "pending")
                }
            }
            .decodeList<SupervisorRequestRow>()

        return requests.map { request ->
            val student = getProfileById(request.studentId)
            val lecturer = getLecturerById(request.lecturerId)

            KaprodiSubmissionData(
                id = request.id,
                studentId = request.studentId,
                studentName = student?.fullName ?: "Mahasiswa tidak ditemukan",
                nim = student?.nim ?: "-",
                title = request.title,
                lecturerId = request.lecturerId,
                lecturerName = lecturer?.fullName ?: "Dosen tidak ditemukan",
                status = request.status,
                note = request.lecturerNote
            )
        }
    }

    suspend fun approveRequest(requestId: Long) {
        supabase
            .from("supervisor_requests")
            .update(
                mapOf(
                    "status" to "accepted",
                    "lecturer_note" to "Disetujui oleh Kaprodi"
                )
            ) {
                filter {
                    eq("id", requestId)
                }
            }
    }

    suspend fun rejectRequest(
        requestId: Long,
        note: String = "Ditolak oleh Kaprodi"
    ) {
        supabase
            .from("supervisor_requests")
            .update(
                mapOf(
                    "status" to "rejected",
                    "lecturer_note" to note
                )
            ) {
                filter {
                    eq("id", requestId)
                }
            }
    }

    private suspend fun getProfileById(profileId: String): ProfileMiniRow? {
        return try {
            supabase
                .from("profiles")
                .select {
                    filter {
                        eq("id", profileId)
                    }
                }
                .decodeSingle<ProfileMiniRow>()
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getLecturerById(lecturerId: Long): LecturerMiniRow? {
        return try {
            supabase
                .from("lecturers")
                .select {
                    filter {
                        eq("id", lecturerId)
                    }
                }
                .decodeSingle<LecturerMiniRow>()
        } catch (e: Exception) {
            null
        }
    }
}

data class KaprodiSubmissionData(
    val id: Long,
    val studentId: String,
    val studentName: String,
    val nim: String,
    val title: String,
    val lecturerId: Long,
    val lecturerName: String,
    val status: String,
    val note: String?
)

@Serializable
private data class SupervisorRequestRow(
    val id: Long,

    @SerialName("student_id")
    val studentId: String,

    @SerialName("lecturer_id")
    val lecturerId: Long,

    val title: String,
    val status: String,

    @SerialName("lecturer_note")
    val lecturerNote: String? = null
)

@Serializable
private data class ProfileMiniRow(
    val id: String,

    @SerialName("full_name")
    val fullName: String,

    val nim: String? = null
)

@Serializable
private data class LecturerMiniRow(
    val id: Long,

    @SerialName("full_name")
    val fullName: String
)