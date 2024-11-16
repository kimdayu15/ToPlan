package com.gems.toplan.data

interface TaskRepository {
    suspend fun getTaskId(id: String): Result<TodoItemResponse>
    suspend fun getTasks(): Result<TodoItem>
    suspend fun addTask(todoWork: TodoWorkRequest, revision: Int): Result<TodoWorkRequest>
    suspend fun updateTask(
        id: String,
        todoWork: TodoWorkRequest,
        revision: Int
    ): Result<TodoWorkRequest>

    suspend fun deleteTask(
        id: String,
        revision: Int
    ): Result<TodoWorkRequest>

    suspend fun updateTasks(todoUpdate: UpdateTodoRequest, revision: Int): Result<TodoItem>

}