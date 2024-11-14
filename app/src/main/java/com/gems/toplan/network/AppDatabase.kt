package com.gems.toplan.network

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gems.toplan.data.TodoItem

@Database(entities = [TodoItem::class], version = 1)  //local memory(not remote one)
abstract class AppDatabase : RoomDatabase() {
    abstract fun TodoDao(): TodoDao

    companion object {

    }
}