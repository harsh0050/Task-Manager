package com.example.taskmanager.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.taskmanager.typeconverters.ServicesArrayTypeConverter

@Entity(tableName = "clients_data")
data class Client(
    @ColumnInfo(name = "client_name") val clientName: String,
    @ColumnInfo(name = "client_number") val clientNumber : String,
    @ColumnInfo(name = "reference_name") val referenceName: String,
    @ColumnInfo(name = "contact_person_name") val contactPersonName : String,
    @ColumnInfo(name = "address") val address: String,
    @TypeConverters(ServicesArrayTypeConverter::class)
    @ColumnInfo(name = "services") val services : ArrayList<Service>
    ){
    @PrimaryKey(autoGenerate = true) var clientId: Long = 0

}
