package com.gems.toplan.network

import com.gems.toplan.data.TodoItem
import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TodoApi {
    @GET("tasks")
    suspend fun getTasks(): TodoItem

    @POST("tasks")
    suspend fun addTask(@Body task: TodoItem.Task): retrofit2.Response<Unit>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): retrofit2.Response<Unit>

    @PUT("tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body task: TodoItem.Task): Response
}
