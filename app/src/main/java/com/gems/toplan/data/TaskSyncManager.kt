package com.gems.toplan.data

import android.content.Context
import android.app.Service
import android.content.Intent
import android.os.IBinder

class TaskSyncManager(private val context: Context) {

    private val repository = TodoItemsRepository()

    fun syncTasks(onSuccess: () -> Unit, onError: (String) -> Unit) {
        NetworkUtils.observeNetworkChanges(context) {
            try {
                val tasks = repository.fetchTasks()
                onSuccess()
            } catch (e: Exception) {
                onError("Sync failed: ${e.message}")
            }
        }
    }
}



class TaskSyncService : Service() {
    private lateinit var taskSyncManager: TaskSyncManager

    override fun onCreate() {
        super.onCreate()
        taskSyncManager = TaskSyncManager(applicationContext)
        taskSyncManager.syncTasks(
            onSuccess = {
                println("Background sync completed.")
            },
            onError = { error ->
                println("Background sync error: $error")
            }
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

