package id.ac.pnj.resortcampus.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

object NotificationHelper {
    private const val CHANNEL_ID = "resort_general"

    fun hasPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

    private fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(CHANNEL_ID, "ReSort Campus", NotificationManager.IMPORTANCE_DEFAULT)
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun show(context: Context, type: NotifType, title: String, text: String) {
        if (!SettingsStore.enabled(type) || !hasPermission(context)) return
        ensureChannel(context)
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pending = PendingIntent.getActivity(
            context,
            type.ordinal,
            launch,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(100 + type.ordinal, notification)
    }

    fun reminder(context: Context) {
        val s = appStrings()
        show(context, NotifType.REMINDER, s.reminderNotifTitle, s.reminderNotifBody)
    }

    fun competition(context: Context) {
        val s = appStrings()
        show(context, NotifType.COMPETITION, s.competitionNotifTitle, s.competitionNotifBody)
    }

    fun achievement(context: Context) {
        val s = appStrings()
        show(context, NotifType.ACHIEVEMENT, s.achievementNotifTitle, s.achievementNotifBody)
    }

    fun enable(context: Context, type: NotifType) {
        SettingsStore.setEnabled(type, true)
        when (type) {
            NotifType.REMINDER -> {
                ReminderScheduler.schedule(context)
                reminder(context)
            }
            NotifType.COMPETITION -> competition(context)
            NotifType.ACHIEVEMENT -> achievement(context)
        }
    }

    fun disable(context: Context, type: NotifType) {
        SettingsStore.setEnabled(type, false)
        NotificationManagerCompat.from(context).cancel(100 + type.ordinal)
        if (type == NotifType.REMINDER) ReminderScheduler.cancel(context)
    }
}

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        SettingsStore.init(applicationContext)
        NotificationHelper.reminder(applicationContext)
        return Result.success()
    }
}

object ReminderScheduler {
    private const val WORK_NAME = "resort_reminder"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}