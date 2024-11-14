package com.gems.toplan.data

import com.gems.toplan.network.RetrofitHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TodoItemsRepository {

    private val retrofitHolder = RetrofitHolder()

    fun fetchTasks(): Flow<List<TodoItem.Task>> = flow {
        try {
            val todoItem = retrofitHolder.TodoApi.getTasks()
            emit(todoItem.list)
        } catch (e: Exception) {
            throw RuntimeException("Failed to fetch tasks. Please try again later.")
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addTask(task: TodoItem.Task): Boolean {
        return try {
            val response = retrofitHolder.TodoApi.addTask(task)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}
