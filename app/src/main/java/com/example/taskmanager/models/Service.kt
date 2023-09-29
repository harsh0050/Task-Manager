package com.example.taskmanager.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverter

@Entity
data class Service(
    @ColumnInfo(name="service") val service: String,
    @ColumnInfo(name="sub_services") val subServices : ArrayList<String>,
    @ColumnInfo(name="tenure") val tenure : String,
    @ColumnInfo(name="charge") val charge: Int
)
