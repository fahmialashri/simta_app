package com.project.data.repository


import com.project.core.SupabaseClientProvider
import com.project.data.model.Profile
import io.github.jan.supabase.postgrest.from

class ProfileRepository {

    private val supabase = SupabaseClientProvider.client

    suspend fun getMyProfile(userId: String): Profile {
        return supabase
            .from("profiles")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<Profile>()
    }
}