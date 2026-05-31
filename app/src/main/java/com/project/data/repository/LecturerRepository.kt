package com.project.data.repository

import android.util.Log
import com.project.core.SupabaseClientProvider
import com.project.data.model.Lecturer
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
                    }
                }
                .decodeList<Lecturer>()
                .sortedBy { it.id }

            Log.d(
                "LecturerRepository",
                "Jumlah dosen department_id=$departmentId: ${lecturers.size}"
            )

            lecturers
        } catch (e: Exception) {
            Log.e("LecturerRepository", "Gagal ambil dosen department_id=$departmentId", e)
            throw e
        }
    }

    suspend fun getInformatikaLecturers(): List<Lecturer> {
        return getLecturersByDepartment(1L)
    }
}