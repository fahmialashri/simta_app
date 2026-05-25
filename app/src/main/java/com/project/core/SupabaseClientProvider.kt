package com.project.core

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.ktor.client.engine.android.Android

object SupabaseClientProvider {

    private const val SUPABASE_URL = "https://vqbdqllofwtkxepoydzq.supabase.co"

    private const val SUPABASE_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZxYmRxbGxvZnd0a3hlcG95ZHpxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzczNzk4MjMsImV4cCI6MjA5Mjk1NTgyM30.O8dcNdDXVP-1gmUbfgzRvnXMUrgYQNgsF3HKZO9cW-M"

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)

            httpEngine = Android.create()
        }
    }
}