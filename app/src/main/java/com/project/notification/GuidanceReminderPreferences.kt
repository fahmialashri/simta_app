// GuidanceReminderPreferences.kt
package com.project.notification

import android.content.Context

internal object GuidanceReminderPreferences {
    private const val PREF_NAME = "guidance_reminder_prefs"
    private const val KEY_LAST_GUIDANCE_TIMESTAMP = "last_guidance_timestamp"
    private const val KEY_LAST_NOTIFIED_LEVEL = "last_notified_level"

    fun getLastGuidanceTimestamp(context: Context): String? {
        return context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LAST_GUIDANCE_TIMESTAMP, null)
    }

    fun setLastGuidanceTimestamp(
        context: Context,
        timestamp: String?
    ) {
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LAST_GUIDANCE_TIMESTAMP, timestamp)
            .putInt(KEY_LAST_NOTIFIED_LEVEL, 0)
            .apply()
    }

    fun getLastNotifiedLevel(context: Context): Int {
        return context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_LAST_NOTIFIED_LEVEL, 0)
    }

    fun setLastNotifiedLevel(
        context: Context,
        level: Int
    ) {
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_LAST_NOTIFIED_LEVEL, level)
            .apply()
    }
}