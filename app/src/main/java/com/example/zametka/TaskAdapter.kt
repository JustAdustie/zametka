package com.example.zametka

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val taskList: MutableList<Task>,
    private val onDeleteClickListener: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView = view.findViewById(R.id.taskName)
        val taskDescription: TextView = view.findViewById(R.id.taskDescription)
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.taskName.text = task.name
        holder.taskDescription.text = task.description
        holder.buttonDelete.setOnClickListener {
            onDeleteClickListener(task)
        }
    }

    override fun getItemCount() = taskList.size
}