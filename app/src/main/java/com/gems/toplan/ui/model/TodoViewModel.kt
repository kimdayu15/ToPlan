import android.content.Context
import androidx.compose.material3.Snackbar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gems.toplan.data.TaskSyncManager
import com.gems.toplan.data.TodoItem
import com.gems.toplan.data.TodoItemsRepository
import com.gems.toplan.network.TodoApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class TodoViewModel(val repository: TodoItemsRepository, context: Context) : ViewModel() {
    private val taskSyncManager = TaskSyncManager(context)

    private val _tasks = MutableStateFlow<List<TodoItem.Task>>(emptyList())
    val tasks: StateFlow<List<TodoItem.Task>> get() = _tasks

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing

    init {
        viewModelScope.launch {
            repository.fetchTasks().collect { fetchedTasks ->
                _tasks.value = fetchedTasks
            }
        }
        observeNetworkAndSync()
    }

    private fun observeNetworkAndSync() {
        taskSyncManager.syncTasks(
            onSuccess = {
                refreshTasks()
            },
            onError = { error ->
                println("Sync error: $error")
            }
        )
    }

    fun addTask(task: TodoItem.Task) {
        viewModelScope.launch {
            val success = repository.addTask(task)
            if (success) {
                _tasks.value += task
            }
        }
    }

    private fun refreshTasks() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                repository.fetchTasks().collect { fetchedTasks ->
                    _tasks.value = fetchedTasks
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun toggleTaskCompletion(task: TodoItem.Task) {
        viewModelScope.launch {
            try {
                val updatedTask = task.copy(done = !task.done)
                _tasks.value = _tasks.value.map {
                    if (it.id == updatedTask.id) updatedTask else it
                }
                updateTask(updatedTask)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun updateTask(task: TodoItem.Task) {
        try {
            val response: Response<Unit> = TodoApi.updateTask(task.id, task)
            if (response.isSuccessful) {
                println("Task updated successfully!")
            } else {
                println("Error updating task: ${response.message()}")
            }
        } catch (e: Exception) {
            println("Failed to update task: ${e.message}")
        }
    }
}
