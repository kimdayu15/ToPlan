package com.gems.toplan.data.network

import com.gems.toplan.data.TodoItem
import com.gems.toplan.data.TodoItemResponse
import com.gems.toplan.data.TodoWorkRequest
import com.gems.toplan.data.UpdateTodoRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoApi {
    @GET("list")
    suspend fun getTasks(): TodoItem

    @GET("list/{id}")
    suspend fun getTask(@Path("id") id: String): TodoItemResponse

    @POST("list/{id}")
    suspend fun addTask(
        @Body task: TodoWorkRequest,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int
    ): TodoWorkRequest

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") id: String,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int
    ): TodoWorkRequest

    @PUT("tasks/{id}")
    suspend fun updateTask(
        @Path("id") id: String,
        @Body task: TodoWorkRequest,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int
    ): TodoWorkRequest


    @PATCH
    suspend fun updateTasks(
        @Body task: UpdateTodoRequest,
        @Header("X-Last-Known-Revision") lastKnownRevision: Int
    ): TodoItem
}
