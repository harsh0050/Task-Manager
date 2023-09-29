package com.example.taskmanager.typeconverters

import androidx.room.TypeConverter
import com.example.taskmanager.models.Service
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ServicesArrayTypeConverter {
    @TypeConverter
    fun fromService(servicesArray: ArrayList<Service>): String?{
        val arrayType = object: TypeToken<ArrayList<Service>>(){}.type
        return Gson().toJson(servicesArray,arrayType)
    }

    @TypeConverter
    fun toService(json: String): ArrayList<Service>? {
        val arrayType = object: TypeToken<ArrayList<Service>>(){}.type
        return Gson().fromJson(json,arrayType)
    }
}