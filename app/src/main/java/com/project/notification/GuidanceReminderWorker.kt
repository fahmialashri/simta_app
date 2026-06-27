// GuidanceReminderWorker.kt
package com.project.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.project.data.repository.BimbinganRepository
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime

class GuidanceReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val repository = BimbinganRepository()

    override suspend fun doWork(): Result {
        val studentId = inputData.getString(KEY_STUDENT_ID)?.trim().orEmpty()
        if (studentId.isBlank()) return Result.success()

        return try {
            val latestSubmissionAt = repository.getLatestSubmissionAt(studentId)
                ?: return Result.success()

            val latestInstant = parseInstantOrNull(latestSubmissionAt)
                ?: return Result.success()

            val now = Instant.now()
            val daysSinceLastBimbingan = Duration.between(latestInstant, now).toDays()

            val previousTimestamp = GuidanceReminderPreferences.getLastGuidanceTimestamp(applicationContext)
            if (previousTimestamp != latestSubmissionAt) {
                GuidanceReminderPreferences.setLastGuidanceTimestamp(
                    context = applicationContext,
                    timestamp = latestSubmissionAt
                )
            }

            val level = when {
                daysSinceLastBimbingan >= 21 -> 3
                daysSinceLastBimbingan >= 14 -> 2
                daysSinceLastBimbingan >= 7 -> 1
                else -> 0
            }

            if (level == 0) return Result.success()

            val lastNotifiedLevel = GuidanceReminderPreferences.getLastNotifiedLevel(applicationContext)
            if (level <= lastNotifiedLevel && previousTimestamp == latestSubmissionAt) {
                return Result.success()
            }

            val (title, message) = reminderMessage(level, daysSinceLastBimbingan)
            NotificationHelper.showGuidanceReminder(
                context = applicationContext,
                title = title,
                message = message
            )

            GuidanceReminderPreferences.setLastNotifiedLevel(
                context = applicationContext,
                level = level
            )

            Result.success()
        } catch (_: Exception) {
            Result.retry()
        }
    }

    private fun reminderMessage(
        level: Int,
        daysSinceLastBimbingan: Long
    ): Pair<String, String> {
        return when (level) {
            3 -> "Pengingat Bimbingan" to
                    "Sudah $daysSinceLastBimbingan hari sejak bimbingan terakhir. Segera hubungi dosen pembimbing agar progress TA tidak terhambat."
            2 -> "Pengingat Bimbingan" to
                    "Kamu sudah $daysSinceLastBimbingan hari tidak bimbingan. Jangan sampai progress skripsi tertunda."
            else -> "Pengingat Bimbingan" to
                    "Sudah $daysSinceLastBimbingan hari sejak bimbingan terakhir. Yuk segera atur jadwal dengan dosen."
        }
    }

    private fun parseInstantOrNull(raw: String): Instant? {
        return try {
            Instant.parse(raw)
        } catch (_: Exception) {
            try {
                OffsetDateTime.parse(raw).toInstant()
            } catch (_: Exception) {
                null
            }
        }
    }

    companion object {
        const val KEY_STUDENT_ID = "key_student_id"
    }
}