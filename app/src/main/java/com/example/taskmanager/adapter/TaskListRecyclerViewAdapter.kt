package com.example.taskmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.models.Task
import java.util.concurrent.TimeUnit

class TaskListRecyclerViewAdapter(private val listener: CustomOnClickListener) : RecyclerView.Adapter<TaskListRecyclerViewHolder>() {
    private var taskData: List<Task> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListRecyclerViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.task_item_view, parent, false)
        val viewHolder = TaskListRecyclerViewHolder(view)
        view.setOnClickListener {
            listener.onClick(viewHolder.adapterPosition)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: TaskListRecyclerViewHolder, position: Int) {
        val task = taskData[position]
        holder.taskName.text = task.service
        holder.clientName.text = "(${task.client})"
        holder.dueDate.text = task.dueDate
        val temp = task.dueDateInMillis - System.currentTimeMillis()
        TimeUnit.MILLISECONDS.toDays(temp).let {days->
            val it = days+1
            if (it <= 0) {
                holder.countDown.text = "Time Out"
                holder.urgencyDetector.setImageResource(R.drawable.black_circle)
            } else if (it <= 2) {
                if (it <= 1) {
                    holder.countDown.text = it.toString() + " day left"
                } else {
                    holder.countDown.text = it.toString() + " days left"
                }
                holder.urgencyDetector.setImageResource(R.drawable.red_circle)
            } else if (it <= 5) {
                holder.countDown.text = it.toString() + " days left"
                holder.urgencyDetector.setImageResource(R.drawable.yellow_circle)
            } else {

                holder.countDown.text = it.toString() + " days left"
                holder.urgencyDetector.setImageResource(R.drawable.green_circle)
            }

        }

    }

    override fun getItemCount(): Int {
        return taskData.size
    }

    fun updateData(data: List<Task>) {
        taskData = data
        notifyDataSetChanged()
    }
}

interface CustomOnClickListener {
    fun onClick(position: Int)
}

class TaskListRecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val taskName: TextView = itemView.findViewById(R.id.taskItemViewTaskName)
    val clientName: TextView = itemView.findViewById(R.id.taskItemViewClientName)
    val dueDate: TextView = itemView.findViewById(R.id.taskItemViewDueDate)
    val countDown: TextView = itemView.findViewById(R.id.taskItemViewCountDown)
    val urgencyDetector: ImageView = itemView.findViewById(R.id.urgencyIndicator)
}
