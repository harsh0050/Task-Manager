package com.example.taskmanager

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.adapter.CustomOnClickListener
import com.example.taskmanager.adapter.TaskListRecyclerViewAdapter
import com.example.taskmanager.dao.ClientDao
import com.example.taskmanager.dao.TaskDao
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.models.Task
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.Calendar

@OptIn(DelicateCoroutinesApi::class)
class MainActivity : AppCompatActivity(), CustomOnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var roomDB: RoomDB
    private lateinit var taskDao: TaskDao
    private lateinit var clientDao: ClientDao
    private var tasksData: List<Task> = ArrayList()

    private var sendNotification: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val context = this

        val permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                sendNotification = it
            }

        //Setting-up Notification Channel
        val notificationChannel = NotificationChannelCompat.Builder(
            "TASK_DUES_CHANNEL",
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        ).apply {
            setName("Due Tasks")
            setDescription("Remainder for tasks that are due")
        }.build()
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)

        //exp
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
//            putExtra("taskId",3)
//            putExtra("message", "hellow guys, chai pilo")
//            putExtra("title", "this is title")
//        }
//        val pendingIntent = PendingIntent.getBroadcast(applicationContext,10, intent,
//            PendingIntent.FLAG_IMMUTABLE)
//        alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+10000,pendingIntent)


//        NotificationCompat.Builder(applicationContext, CHANNEL_ID).apply {
//            setSmallIcon(R.drawable.notification_small_icon)
//            setContentTitle("Task is due")
//            setContentText("harsh bhikadiya application makes ")
//            priority = NotificationCompat.PRIORITY_MAX
//            setAutoCancel(true)
//        }.build().let {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
//            } else {
//                sendNotification = true
//            }
//            if (sendNotification) {
//                try{
//                    NotificationManagerCompat.from(applicationContext).notify(1, it)
//                }catch(e: SecurityException){
//                    Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
//                }
//
//            }

//        }


        //room db
        roomDB = RoomDB.getInstance(this)
        taskDao = roomDB.getTaskDao()
        clientDao = roomDB.getClientDao()

        //app bar
        val appbar = binding.mainActivityAppBar
        setSupportActionBar(appbar)
        supportActionBar?.title = "Task Manager"

        //recycler view
        binding.taskListRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = TaskListRecyclerViewAdapter(this)
        binding.taskListRecyclerView.adapter = adapter


        //showcasing tasks
        taskDao.getAllTasks().observe(context) {
            tasksData = it
            println("current time : " + System.currentTimeMillis())
            it.forEach { t ->
                println(t.taskId.toString() + " -> " + t.service + " : " + t.dueDateInMillis)
            }
//            it.forEach{t->
//                println("taskId: " + t.taskId + ", service: " + t.service)
//            }
            adapter.updateData(tasksData)
        }


//        supportActionBar?.
//        startActivity(Intent(this, AddClientActivity::class.java))
//        startActivity(Intent(this,AddTaskActivity::class.java))
        binding.seeLog.setOnClickListener {
            startActivity(Intent(applicationContext, ViewLogActivity::class.java))
        }
//        println("future: " + (System.currentTimeMillis() + 19020000))
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
//            putExtra("message","This one is manual (time setted) alarm")
//            putExtra("title","meth is bad")
//            putExtra("taskId",3415)
//        }
//        val pendingIntent = PendingIntent.getBroadcast(applicationContext,3415,intent,PendingIntent.FLAG_IMMUTABLE)
//        Toast.makeText(applicationContext, "setting up alarm", Toast.LENGTH_SHORT).show()
//        alarmManager.set(AlarmManager.RTC,1692556200000,pendingIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_appbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.addClientButton -> {
                val intent = Intent(applicationContext, AddClientActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.addTaskButton -> {
                val intent = Intent(applicationContext, AddTaskActivity::class.java)
                startActivity(intent)
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }

    override fun onClick(position: Int) {
        val task = tasksData[position]
        val intent = Intent(applicationContext, AddTaskActivity::class.java)
        intent.putExtra("isEdit", true)
        intent.putExtra("taskId", task.taskId)
        intent.putExtra("clientName", task.client)
        intent.putExtra("service", task.service)
        intent.putExtra("note", task.extraNote)

        val cal = Calendar.getInstance().apply { timeInMillis = task.dueDateInMillis }
        intent.putExtra("year", cal.get(Calendar.YEAR))
        intent.putExtra("month", cal.get(Calendar.MONTH))
        intent.putExtra("day", cal.get(Calendar.DAY_OF_MONTH))
        startActivity(intent)
    }


    companion object {
        const val CHANNEL_ID = "TASK_DUES_CHANNEL"
    }
}