package com.project.data.repository

import android.util.Log
import com.project.core.SupabaseClientProvider
import com.project.data.model.Lecturer
import com.project.data.model.LecturerInsert
import io.github.jan.supabase.postgrest.from

class LecturerRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getLecturersByDepartment(departmentId: Long): List<Lecturer> {
        return try {
            val lecturers = supabase
                .from("lecturers")
                .select {
                    filter {
                        eq("department_id", departmentId)
                        eq("is_active", true)
                    }
                }
                .decodeList<Lecturer>()
                .sortedWith(
                    compareBy<Lecturer> { !it.isAvailable }
                        .thenBy { it.currentStudents }
                        .thenBy { it.name }
                )

            Log.d(
                "LecturerRepository",
                "Jumlah dosen department_id=$departmentId: ${lecturers.size}"
            )

            lecturers
        } catch (e: Exception) {
            Log.e(
                "LecturerRepository",
                "Gagal ambil dosen department_id=$departmentId",
                e
            )
            throw e
        }
    }

    suspend fun getAllLecturersForKaprodi(
        departmentId: Long? = null
    ): List<Lecturer> {
        return try {
            val lecturers = supabase
                .from("lecturers")
                .select()
                .decodeList<Lecturer>()
                .filter { lecturer ->
                    departmentId == null || lecturer.departmentId == departmentId
                }
                .sortedWith(
                    compareBy<Lecturer> { !it.isActive }
                        .thenBy { it.departmentId }
                        .thenBy { it.name }
                )

            Log.d(
                "LecturerRepository",
                "Jumlah dosen kaprodi department_id=$departmentId: ${lecturers.size}"
            )

            lecturers
        } catch (e: Exception) {
            Log.e(
                "LecturerRepository",
                "Gagal ambil semua dosen kaprodi department_id=$departmentId",
                e
            )
            throw e
        }
    }

    suspend fun getLecturersByDepartmentAndExpertise(
        departmentId: Long,
        expertise: String
    ): List<Lecturer> {
        return try {
            val lecturers = supabase
                .from("lecturers")
                .select {
                    filter {
                        eq("department_id", departmentId)
                        eq("expertise", expertise)
                        eq("is_active", true)
                    }
                }
                .decodeList<Lecturer>()
                .sortedWith(
                    compareBy<Lecturer> { !it.isAvailable }
                        .thenBy { it.currentStudents }
                        .thenBy { it.name }
                )

            Log.d(
                "LecturerRepository",
                "Jumlah dosen department_id=$departmentId expertise=$expertise: ${lecturers.size}"
            )

            lecturers
        } catch (e: Exception) {
            Log.e(
                "LecturerRepository",
                "Gagal ambil dosen department_id=$departmentId expertise=$expertise",
                e
            )
            throw e
        }
    }

    suspend fun getAvailableRecommendations(
        departmentId: Long,
        expertise: String?,
        excludeLecturerId: Long? = null
    ): List<Lecturer> {
        val lecturers = if (expertise.isNullOrBlank()) {
            getLecturersByDepartment(departmentId)
        } else {
            getLecturersByDepartmentAndExpertise(
                departmentId = departmentId,
                expertise = expertise
            )
        }

        return lecturers
            .filter { lecturer ->
                lecturer.isAvailable && lecturer.id != excludeLecturerId
            }
            .sortedWith(
                compareBy<Lecturer> { it.currentStudents }
                    .thenByDescending { it.remainingQuota }
                    .thenBy { it.name }
            )
    }

    suspend fun addLecturer(
        departmentId: Long,
        name: String,
        title: String?,
        expertise: String?,
        quota: Int
    ) {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            throw Exception("Nama dosen wajib diisi.")
        }

        if (quota <= 0) {
            throw Exception("Kuota dosen harus lebih dari 0.")
        }

        val lecturer = LecturerInsert(
            departmentId = departmentId,
            name = cleanName,
            title = title?.trim()?.ifBlank { null },
            expertise = expertise?.trim()?.ifBlank { null },
            quota = quota,
            currentStudents = 0,
            isActive = true
        )

        supabase
            .from("lecturers")
            .insert(lecturer)
    }

    suspend fun updateLecturerQuota(
        lecturerId: Long,
        quota: Int
    ) {
        if (quota <= 0) {
            throw Exception("Kuota dosen harus lebih dari 0.")
        }

        supabase
            .from("lecturers")
            .update(
                mapOf(
                    "quota" to quota
                )
            ) {
                filter {
                    eq("id", lecturerId)
                }
            }
    }

    suspend fun updateLecturerCurrentStudents(
        lecturerId: Long,
        currentStudents: Int
    ) {
        if (currentStudents < 0) {
            throw Exception("Jumlah mahasiswa tidak boleh kurang dari 0.")
        }

        supabase
            .from("lecturers")
            .update(
                mapOf(
                    "current_students" to currentStudents
                )
            ) {
                filter {
                    eq("id", lecturerId)
                }
            }
    }

    suspend fun deactivateLecturer(
        lecturerId: Long
    ) {
        supabase
            .from("lecturers")
            .update(
                mapOf(
                    "is_active" to false
                )
            ) {
                filter {
                    eq("id", lecturerId)
                }
            }
    }

    suspend fun reactivateLecturer(
        lecturerId: Long
    ) {
        supabase
            .from("lecturers")
            .update(
                mapOf(
                    "is_active" to true
                )
            ) {
                filter {
                    eq("id", lecturerId)
                }
            }
    }

    suspend fun incrementCurrentStudents(
        lecturerId: Long,
        currentStudents: Int
    ) {
        supabase
            .from("lecturers")
            .update(
                mapOf(
                    "current_students" to currentStudents + 1
                )
            ) {
                filter {
                    eq("id", lecturerId)
                }
            }
    }

    suspend fun getLecturerById(
        lecturerId: Long
    ): Lecturer? {
        return try {
            supabase
                .from("lecturers")
                .select {
                    filter {
                        eq("id", lecturerId)
                    }
                }
                .decodeSingle<Lecturer>()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getInformatikaLecturers(): List<Lecturer> {
        return getLecturersByDepartment(1L)
    }
}