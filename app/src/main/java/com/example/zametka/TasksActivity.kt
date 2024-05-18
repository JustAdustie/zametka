package com.example.zametka

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TasksActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskList: MutableList<Task>
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Получите userId из Intent или другого источника (например, SharedPreferences)
        val username = intent.getStringExtra("username") ?: return
        userId = dbHelper.getUserId(username)

        taskList = dbHelper.getTasksForUser(userId).toMutableList()
        taskAdapter = TaskAdapter(taskList) { task ->
            deleteTask(task)
        }
        recyclerView.adapter = taskAdapter

        val buttonAddTask: Button = findViewById(R.id.buttonAddTask)
        buttonAddTask.setOnClickListener {
            showAddTaskDialog()
        }

        val buttonExit : Button = findViewById(R.id.button)
        buttonExit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Add New Task")
            .setPositiveButton("Add") { _, _ ->
                val editTextTaskName = dialogView.findViewById<EditText>(R.id.editTextTaskName)
                val editTextTaskDescription = dialogView.findViewById<EditText>(R.id.editTextTaskDescription)
                val taskName = editTextTaskName.text.toString()
                val taskDescription = editTextTaskDescription.text.toString()

                if (taskName.isNotEmpty() && taskDescription.isNotEmpty()) {
                    val task = Task(0, taskName, taskDescription)
                    dbHelper.addTask(task, userId)
                    taskList.add(task)
                    taskAdapter.notifyItemInserted(taskList.size - 1)
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
        dialogBuilder.create().show()
    }

    private fun deleteTask(task: Task) {
        dbHelper.deleteTask(task.id)
        val position = taskList.indexOf(task)
        taskList.removeAt(position)
        taskAdapter.notifyItemRemoved(position)
        Toast.makeText(this, "Task deleted successfully", Toast.LENGTH_SHORT).show()
    }
}
