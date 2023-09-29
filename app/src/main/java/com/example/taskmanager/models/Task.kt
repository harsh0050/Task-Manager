package com.example.taskmanager.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks_table")
data class Task(
    @ColumnInfo(name = "client") val client: String,
    @ColumnInfo(name = "service") val service: String,
    @ColumnInfo(name = "due_date") val dueDate: String,
    @ColumnInfo(name = "due_date_in_millis") val dueDateInMillis: Long,
    @ColumnInfo(name = "date_added") val dateAdded: String,
    @ColumnInfo(name = "extra_note") val extraNote: String,
) {
    @PrimaryKey(autoGenerate = true)
    var taskId: Int = 0

    //for deletion
    constructor(taskId: Int) : this("","","",0L,"",""){
        this.taskId = taskId
    }

}
