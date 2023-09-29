package com.example.taskmanager.broadcast_receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.taskmanager.RoomDB
import com.example.taskmanager.models.Task
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class TaskCreationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            val roomDB = RoomDB.getInstance(context)
            val taskDao = roomDB.getTaskDao()

            val task = Task(
                "Manual Client",
                "Un named",
                "16-08-2023",
                System.currentTimeMillis() + 1000000,
                "now",
                "no note found here"
            )

            GlobalScope.launch(Dispatchers.IO){
                taskDao.addTask(task)
            }

        }

    }
}