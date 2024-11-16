package com.gems.toplan.ui.model

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.gems.toplan.data.TaskSyncManager
import com.gems.toplan.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val taskSyncManager = TaskSyncManager(applicationContext)

        taskSyncManager.syncTasks(
            onSuccess = {
                println("Tasks synced successfully.")
            },
            onError = { error ->
                println("Sync error: $error")
            }
        )

        val viewModel: TodoViewModel = ViewModelProvider(this)[TodoViewModel::class.java]

        setContent {
            val navController = rememberNavController()
            AppNavHost(navController = navController, viewModel = viewModel)
        }
    }
}

