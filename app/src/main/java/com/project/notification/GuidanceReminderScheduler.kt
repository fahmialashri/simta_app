// GuidanceReminderScheduler.kt
package com.project.notification

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

object GuidanceReminderScheduler {
    private const val UNIQUE_WORK_NAME = "guidance_reminder_work"

    fun schedule(
        context: Context,
        studentId: String
    ) {
        if (studentId.isBlank()) return

        val request = PeriodicWorkRequestBuilder<GuidanceReminderWorker>(
            1,
            TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setInputData(
                workDataOf(
                    GuidanceReminderWorker.KEY_STUDENT_ID to studentId
                )
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}