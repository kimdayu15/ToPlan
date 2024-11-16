package com.gems.toplan.data

import com.gems.toplan.network.RetrofitHolder

class TodoItemsRepository() : TaskRepository {

    private val retrofitApi = RetrofitHolder.api
    override suspend fun getTaskId(id: String): Result<TodoItemResponse> {
        return try {
            val response = retrofitApi.getTask(id)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                Result.failure(Exception("Error"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTasks(): Result<TodoItem> {
        return try {
            val response = retrofitApi.getTasks()
            if (response.status == "ok") {
                Result.success(response)
            } else {
                Result.failure(Exception("Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    override suspend fun addTask(
        todoWork: TodoWorkRequest,
        revision: Int
    ): Result<TodoWorkRequest> {
        return try {
            val response = retrofitApi.addTask(todoWork, revision)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                Result.failure(Exception("Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTask(
        id: String,
        todoWork: TodoWorkRequest,
        revision: Int
    ): Result<TodoWorkRequest> {
        return try {
            val response = retrofitApi.updateTask(id, todoWork, revision)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                Result.failure(Exception("Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun deleteTask(
        id: String,
        revision: Int
    ): Result<TodoWorkRequest> {
        return try {
            val response = retrofitApi.deleteTask(id,revision)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                Result.failure(Exception("Error"))
        } }catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTasks(
        todoUpdate: UpdateTodoRequest,
        revision: Int
    ): Result<TodoItem> {
        return try {
            val response = retrofitApi.updateTasks(todoUpdate, revision)
            if (response.status == "ok") {
                Result.success(response)
            } else {
                Result.failure(Exception("Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
