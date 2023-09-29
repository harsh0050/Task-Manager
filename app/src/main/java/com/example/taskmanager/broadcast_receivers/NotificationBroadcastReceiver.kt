package com.example.taskmanager.broadcast_receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.taskmanager.MainActivity
import com.example.taskmanager.R
import com.example.taskmanager.RoomDB
import com.example.taskmanager.models.Log
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class NotificationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context!=null){
            val logDao = RoomDB.getInstance(context).getLogDao()
            GlobalScope.launch {
                logDao.addLog(Log(System.currentTimeMillis(),"alarm broadcast for notification received"))
            }
        }

        intent?.let{ _ ->
            val message = intent.getStringExtra("message")
            val title = intent.getStringExtra("title")
            val id = intent.getIntExtra("taskId",0)
            context?.let {
                val notification = NotificationCompat.Builder(it, MainActivity.CHANNEL_ID).apply {
                    setContentText(message)
                    setContentTitle(title)
                    setSmallIcon(R.drawable.notification_small_icon)
                    priority = NotificationCompat.PRIORITY_DEFAULT
                }.build()


                val logDao = RoomDB.getInstance(context).getLogDao()

                GlobalScope.launch {
                    logDao.addLog(Log(System.currentTimeMillis(),"notification sending: '$message'"))
                }

                NotificationManagerCompat.from(context).let { notificationManager ->
                    if(notificationManager.areNotificationsEnabled()){
                        try{
                            notificationManager.notify(id,notification)

                            GlobalScope.launch {
                                logDao.addLog(Log(System.currentTimeMillis(),"notification sent successfully"))
                            }
                        }catch (e: SecurityException){
                            Toast.makeText(context, "can't send notification", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        }
    }
}