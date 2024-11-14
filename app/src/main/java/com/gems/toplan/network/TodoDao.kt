package com.gems.toplan.network

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.gems.toplan.data.TodoItem

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(Todo: TodoItem)

    @Delete
    suspend fun delete(Todo: TodoItem)

//    @Query("SELECT * FROM todo_tasks")
    suspend fun getAllTodo(): List<TodoItem>
}