package com.example.gameficando_tarefas.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class CounterActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_INCREMENT -> {
                val current = intent.getIntExtra(EXTRA_COUNT, 0)
                showCounterNotification(context, current + 1)
            }
            ACTION_RESET -> {
                showCounterNotification(context, 0)
            }
        }
    }
}
