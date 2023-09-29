package com.example.taskmanager.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.taskmanager.models.Log

@Dao
interface LogDao {
    @Insert
    suspend fun addLog(log : Log)

    @Query("SELECT * FROM log_table ORDER BY time_stamp DESC")
    fun getAllLogs() : LiveData<List<Log>>
}