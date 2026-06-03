package com.example.gameficando_tarefas.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.gameficando_tarefas.R

const val CHANNEL_ID = "counter_channel"
const val NOTIFICATION_ID = 1001

const val ACTION_INCREMENT = "com.example.gameficando_tarefas.ACTION_INCREMENT"
const val ACTION_RESET = "com.example.gameficando_tarefas.ACTION_RESET"
const val EXTRA_COUNT = "extra_count"

fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        CHANNEL_ID,
        "Contador",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        description = "Notificação com contador interativo"
    }
    val manager = context.getSystemService(NotificationManager::class.java)
    manager.createNotificationChannel(channel)
}

fun showCounterNotification(context: Context, count: Int) {
    val incrementIntent = PendingIntent.getBroadcast(
        context,
        0,
        Intent(ACTION_INCREMENT).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_COUNT, count)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val resetIntent = PendingIntent.getBroadcast(
        context,
        1,
        Intent(ACTION_RESET).apply {
            setPackage(context.packageName)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("Contador: $count")
        .setContentText("Toque +1 para incrementar ou Resetar para zerar.")
        .addAction(0, "+1", incrementIntent)
        .addAction(0, "Resetar", resetIntent)
        .setOngoing(true)       // não some ao deslizar
        .setOnlyAlertOnce(true) // não vibra/toca a cada atualização
        .build()

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
}
