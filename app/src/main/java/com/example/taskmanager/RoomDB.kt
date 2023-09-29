package com.example.taskmanager

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.taskmanager.dao.ClientDao
import com.example.taskmanager.dao.LogDao
import com.example.taskmanager.dao.TaskDao
import com.example.taskmanager.models.Client
import com.example.taskmanager.models.Log
import com.example.taskmanager.models.Task
import com.example.taskmanager.typeconverters.ServicesArrayTypeConverter

@Database(entities = [Client::class, Task::class, Log::class], version = 4)
@TypeConverters(ServicesArrayTypeConverter::class)
abstract class RoomDB : RoomDatabase() {

    abstract fun getClientDao(): ClientDao
    abstract fun getTaskDao(): TaskDao
    abstract fun getLogDao() : LogDao

    companion object {
        private const val DATABASE_NAME = "task_manager.db"
        private var INSTANCE: RoomDB? = null

        fun getInstance(context: Context): RoomDB {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDB::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}

class MigrateFrom1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        println("we ran")
        val query =
            "CREATE TABLE IF NOT EXISTS 'tasks_table' " +
                    "(" +
                    "'due_date' TEXT NOT NULL, " +
                    "'client' TEXT NOT NULL, " +
                    "'date_added' TEXT NOT NULL, " +
                    "'extra_note' TEXT NOT NULL, " +
                    "'service' TEXT NOT NULL, " +
                    "'taskId' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" +
                    ")"
        database.execSQL(query)
    }
}