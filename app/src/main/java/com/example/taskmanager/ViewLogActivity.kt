package com.example.taskmanager

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class ViewLogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_log)
        val logDao = RoomDB.getInstance(applicationContext).getLogDao()
        val logListView = findViewById<ListView>(R.id.logListView)
        val calendar = Calendar.getInstance()
        logDao.getAllLogs().observe(this){
            val list = ArrayList<String>()
            it.forEach {ls->
                calendar.timeInMillis = ls.timeStamp
                val readableTime =
                    "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}:${
                        calendar.get(Calendar.SECOND)
                    } - ${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH)+1}/${
                        calendar.get(Calendar.YEAR)}"
                list.add("$readableTime: ${ls.log}")
            }
            val arrayAdapter = ArrayAdapter(applicationContext,android.R.layout.simple_list_item_1,list)
            logListView.adapter = arrayAdapter
        }

    }
}