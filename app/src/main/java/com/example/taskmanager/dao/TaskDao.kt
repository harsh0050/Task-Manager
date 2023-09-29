package com.example.taskmanager.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.taskmanager.models.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task : Task) : Long //primary key

    @Update
    suspend fun updateTask(task:Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT * FROM tasks_table ORDER BY due_date_in_millis ASC")
    fun getAllTasks() : LiveData<List<Task>>
}