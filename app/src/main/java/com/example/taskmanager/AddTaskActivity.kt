package com.example.taskmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentResultListener
import com.example.taskmanager.broadcast_receivers.NotificationBroadcastReceiver
import com.example.taskmanager.broadcast_receivers.TaskCreationBroadcastReceiver
import com.example.taskmanager.dao.ClientDao
import com.example.taskmanager.dao.LogDao
import com.example.taskmanager.dao.TaskDao
import com.example.taskmanager.databinding.ActivityAddTaskBinding
import com.example.taskmanager.dialoguefragments.DatePickerFragment
import com.example.taskmanager.dialoguefragments.IDateSetter
import com.example.taskmanager.models.Client
import com.example.taskmanager.models.Log
import com.example.taskmanager.models.Task
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone


@OptIn(DelicateCoroutinesApi::class)
class AddTaskActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, IDateSetter,
    FragmentResultListener {
    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var roomDB: RoomDB
    private lateinit var clientDao: ClientDao
    private lateinit var taskDao: TaskDao
    private lateinit var logDao: LogDao
    private lateinit var context: AddTaskActivity


    private var tempClientsData = ArrayList<Client>()
    private var tempServices = ArrayList<String>()
    private val calender = Calendar.getInstance().apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
        }
    }


    //isEdit thingies
    private var year = calender.get(Calendar.YEAR)
    private var month = calender.get(Calendar.MONTH)
    private var day = calender.get(Calendar.DAY_OF_MONTH)

    private var isEdit = false
    private var isEditTaskId = -1
    private var isEditClientName = ""
    private var isEditService = ""
    private var isEditNote = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this

//        Toast.makeText(applicationContext, month, Toast.LENGTH_SHORT).show()
        println("month: $month")
        //checking if this is not edit
        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            intent.getStringExtra("clientName")?.let {
                isEditClientName = it
            }
            intent.getStringExtra("service")?.let {
                isEditService = it
            }
            intent.getStringExtra("note")?.let {
                isEditNote = it
            }
            isEditTaskId = intent.getIntExtra("taskId", isEditTaskId)
            year = intent.getIntExtra("year", year)
            month = intent.getIntExtra("month", month)
            day = intent.getIntExtra("day", day)
        }

        //initializing ActionBar
        setSupportActionBar(binding.addTaskActivityAppbar)
        supportActionBar?.let {
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
            it.title = "Add Task"
        }

        //initializing RoomDB
        roomDB = RoomDB.getInstance(this)
        clientDao = roomDB.getClientDao()
        taskDao = roomDB.getTaskDao()
        logDao = roomDB.getLogDao()

        //setting today's date in Due Date Edit Text
        val thisDueDate = String.format(
            "%02d/%02d/%04d",
            day,
            month+1,//month is zero indexed
            year
        )
        binding.taskDueDateTextView.text = thisDueDate
        calender.set(year, month, day)

        //populating client name spinner
        populateFields()


        //due date picker launcher mechanism
        binding.dueDatePicker.setOnClickListener {
            val manager = supportFragmentManager.beginTransaction()
            supportFragmentManager.setFragmentResultListener("DATE_PICKER", context, context)
            val datePicker = DatePickerFragment.newInstance(context, context, year, month, day)
            manager.add(datePicker, "null").commitNow()
        }

        //add task handling
        binding.aAddTaskButton.setOnClickListener {
            val clientName =
                tempClientsData[binding.addClientActivityClientSpinner.selectedItemPosition].clientName
            val service = tempServices[binding.addClientActivityServiceSpinner.selectedItemPosition]
            val dueDate = binding.taskDueDateTextView.text.toString()
            val dueDateInMillis = calender.timeInMillis
            val extraNote = binding.addTaskExtraNote.text.toString()

            val task = Task(clientName, service, dueDate, dueDateInMillis, dueDate, extraNote)
            GlobalScope.launch {
                var taskId = 0
                if (isEdit) {
                    println("updating")
                    task.taskId = isEditTaskId
                    taskDao.updateTask(task)
                    //for notifications
                    taskId = isEditTaskId
                } else {
                    taskId = taskDao.addTask(task).toInt()
                }
                scheduleNotification(taskId, clientName, service, dueDateInMillis)
//                scheduleTaskCreation()
//                println("task added")
                runOnUiThread {
                    finish()
                }
            }
        }
    }

    private fun scheduleTaskCreation() {

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, TaskCreationBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            160,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notintent =
            Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
                putExtra("message", "un named service of clientName is due in 1 day")
                putExtra("title", "Due Task")
                putExtra("taskId", taskId)
            }
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 20000, pendingIntent)
        alarmManager.set(
            AlarmManager.RTC,
            System.currentTimeMillis() + 20000,
            PendingIntent.getBroadcast(
                applicationContext,
                564,
                notintent,
                PendingIntent.FLAG_IMMUTABLE
            )
        )

    }

    private fun scheduleNotification(
        taskId: Int,
        clientName: String,
        service: String,
        dueDateInMillis: Long,
    ) {
        GlobalScope.launch {
            logDao.addLog(Log(System.currentTimeMillis(), "scheduling notification"))
        }


        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(applicationContext, NotificationBroadcastReceiver::class.java).apply {
            putExtra("message", "$service service of $clientName is due in 1 day")
            putExtra("title", "Due Task")
            putExtra("taskId", taskId)
        }
        val pendingIntent =
            PendingIntent.getBroadcast(
                applicationContext,
                taskId,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        val triggerTime = dueDateInMillis - (24 * 60 * 60 * 1000)
        if (isEdit) {//then first cancel the previous notification
            alarmManager.cancel(pendingIntent)
            //it will cancel the intent with request code as this pendingIntent
        }
        GlobalScope.launch {
            val temp = calender.timeInMillis
            calender.timeInMillis = triggerTime
            val readableTime =
                "${calender.get(Calendar.HOUR_OF_DAY)}:${calender.get(Calendar.MINUTE)}:${
                    calender.get(Calendar.SECOND)
                } - ${calender.get(Calendar.DAY_OF_MONTH)}/${calender.get(Calendar.MONTH)+1}/${
                    calender.get(Calendar.YEAR)}"
            calender.timeInMillis = temp

            logDao.addLog(
                Log(
                    System.currentTimeMillis(),
                    "setting up alarm for $triggerTime ($readableTime)"
                )
            )
        }
        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        println("item $position selected in client spinner")
        if (!isEdit) {
            populateServiceSpinner(tempClientsData[position])
            //other wise, we have already handled that by now.
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        populateServiceSpinner(tempClientsData[0])
    }


    private fun populateFields() {
        GlobalScope.launch(Dispatchers.IO) {
            tempClientsData = clientDao.getAllClients() as ArrayList<Client>

            val arr = ArrayList<String>()
            tempClientsData.forEach {
                arr.add(it.clientName)
            }
            ArrayAdapter(context, R.layout.custom_spinner_item, arr).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                runOnUiThread {
                    binding.addClientActivityClientSpinner.adapter = it
                    binding.addClientActivityClientSpinner.onItemSelectedListener = context

                    if (isEdit) {
                        fillDetails()
                    }
                }
            }

        }
    }

    private fun populateServiceSpinner(client: Client) {
        tempServices = ArrayList()
        client.services.forEach {
            if (it.subServices.isNotEmpty()) {
                tempServices.addAll(it.subServices)
            } else {
                tempServices.add(it.service)
            }
        }
        if (tempServices.isEmpty()) {
            tempServices.add("No Services")
        }
        ArrayAdapter(context, R.layout.custom_spinner_item, tempServices).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.addClientActivityServiceSpinner.adapter = it
        }
    }


    //date setting
    override fun onFragmentResult(requestKey: String, result: Bundle) {
        if (requestKey == "DATE_PICKER") {
            setDate(
                result.getInt("day", day),
                result.getInt("month", month),
                result.getInt("year", year)
            )
        }
    }

    override fun setDate(dayOfMonth: Int, month: Int, year: Int) {
        calender.set(year, month, dayOfMonth)
        this.day = dayOfMonth
        this.month = month
        this.year = year

        val date = String.format("%02d/%02d/%04d", dayOfMonth, month, year)
        binding.taskDueDateTextView.text = date
    }


    //for isEdit
    private fun fillDetails() {
        var clientIdx: Int = -1

        for (i in tempClientsData.indices) {
            if (tempClientsData[i].clientName == isEditClientName) {
                clientIdx = i
                break;
            }
        }
        if (clientIdx > -1) {
            binding.addClientActivityClientSpinner.setSelection(clientIdx, true)
            populateServiceSpinner(tempClientsData[clientIdx])
            val serviceIdx = tempServices.indexOf(isEditService)
            if (serviceIdx > -1) {
                binding.addClientActivityServiceSpinner.setSelection(serviceIdx, true)
            } else {
//                println(tempServices)
            }
            binding.addTaskExtraNote.setText(isEditNote)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (isEdit) {
            menuInflater.inflate(R.menu.edit_task_appbar_menu, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.deleteTaskButton) {
            GlobalScope.launch {
                val task = Task(isEditTaskId)
                taskDao.deleteTask(task)
            }
            finish()
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    }

}