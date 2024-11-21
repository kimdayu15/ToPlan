package com.gems.toplan.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gems.toplan.data.network.NetworkUtils
import com.gems.toplan.data.TodoItem
import com.gems.toplan.data.TodoItemsRepository
import com.gems.toplan.data.TodoWorkRequest
import com.gems.toplan.data.UpdateTodoRequest
import com.gems.toplan.ui.components.Importance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.text.SimpleDateFormat
import kotlinx.coroutines.cancelChildren
import java.util.Date
import java.util.UUID

class TodoViewModel() : ViewModel() {
    val isNetworkAvailable = MutableStateFlow(false)
    val repository = TodoItemsRepository()

    fun observeNetworkChanges(context: Context) {
        NetworkUtils.observeNetworkChanges(context) {
            isNetworkAvailable.value = true
        }
    }

    private val _tasks = MutableStateFlow<List<TodoItem.Task>>(mutableListOf())
    val tasks: StateFlow<List<TodoItem.Task>> get() = _tasks

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> get() = _isRefreshing

    private val _revision = MutableStateFlow<Int>(0)

    private val _checkedTasks = MutableStateFlow<Int>(0)
    val checkedTasks: StateFlow<Int> get() = _checkedTasks

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> get() = _snackbarMessage

    init {
        viewModelScope.launch {
            getTasks()
        }
    }

    fun getTasks() {
        viewModelScope.launch {
            val fetchedTasks = repository.getTasks()
            fetchedTasks.onSuccess {
                _tasks.value = it.list.toMutableList()
                _revision.value = it.revision
                _checkedTasks.value = it.list.count { it.done }
            }.onFailure {
                _snackbarMessage.value = it.message
            }
        }
    }

    fun addTask(text: String, importance: Importance, deadline: Long?) {
        viewModelScope.launch {
            val success = repository.addTask(
                TodoWorkRequest(
                    "ok", TodoItem.Task(
                        id = UUID.randomUUID().toString(),
                        text = text,
                        importance = importance,
                        done = false,
                        deadline = deadline,
                        createdAt = Date().time,
                        changedAt = Date().time,
                        lastUpdatedBy = ""
                    )

                ), _revision.value
            )
            success.onSuccess {
                getTasks()
            }.onFailure {
                when (it) {
                    is HttpException -> {
                        _snackbarMessage.value = it.message
                    }

                    else -> {
                        _snackbarMessage.value = it.message
                    }
                }
            }
        }
    }

    fun getTask(todoId: String, resp: (TodoItem.Task?) -> Unit) {
        viewModelScope.launch {
            repository.getTaskId(todoId).onSuccess {
                resp(it.taskElement)
            }.onFailure {
                when (it) {
                    is HttpException -> {
                        _snackbarMessage.value = it.message
                    }

                    else -> {
                        _snackbarMessage.value = it.message
                    }
                }

            }
        }
    }

    fun updateVisibility() {
        _isRefreshing.value = !_isRefreshing.value
    }

    fun snackError() {
        _snackbarMessage.value = null
    }

    fun countRefresh(added: Boolean = true) {
        if (added) {
            _checkedTasks.value++
        } else {
            _checkedTasks.value--
        }
    }

    fun simpleDateFormatter(date: Date): String {
        val formatter = SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
        return if (date != null) {
            formatter.format(date)
        } else {
            ""
        }
    }

    fun refreshTaskId(id: String, todoWorkRequest: TodoWorkRequest) {
        viewModelScope.launch {
            val success = repository.updateTask(id, todoWorkRequest, _revision.value)
            success.onSuccess {
                getTasks()
            }.onFailure {
                if (it is HttpException && it.code() == 400) {
                    _snackbarMessage.value = "Bad request"
                } else if (it is HttpException && it.code() == 404) {
                    _snackbarMessage.value = "Not found"
                } else {
                    _snackbarMessage.value = it.message
                }
            }
        }
    }

    fun deleteTaskId(taskId: String) {
        viewModelScope.launch {
            val success = repository.deleteTask(taskId, _revision.value)
            success.onSuccess {
                getTasks()
            }.onFailure {
                if (it is HttpException && it.code() == 400) {
                    _snackbarMessage.value = "Bad request"
                } else if (it is HttpException && it.code() == 404) {
                    _snackbarMessage.value = "Not found"
                } else {
                    _snackbarMessage.value = it.message
                }
            }
        }
    }

    fun refreshTasks() {
        viewModelScope.launch {
            val success =
                repository.updateTasks(UpdateTodoRequest("ok", _tasks.value), _revision.value)
            success.onSuccess {
                getTasks()
            }.onFailure {
                _snackbarMessage.value = it.message
            }
        }
    }


    fun refreshTaskUi(todoItem: TodoItem.Task) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.map {
                if (it.id == todoItem.id) {
                    it.copy(changedAt = Date().time)
                } else it
            }.toMutableList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.coroutineContext.cancelChildren()
    }
}