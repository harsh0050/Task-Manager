package com.example.taskmanager.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_table")
data class Log(@ColumnInfo(name = "time_stamp")val timeStamp : Long, @ColumnInfo(name="log") val log: String){
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "log_id")var logId: Long = 0

}
