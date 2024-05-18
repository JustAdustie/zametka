package com.example.zametka

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserDatabase.db"
        private const val TABLE_USER = "User"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"

        private const val TABLE_TASK = "Task"
        private const val COLUMN_TASK_ID = "task_id"
        private const val COLUMN_TASK_NAME = "task_name"
        private const val COLUMN_TASK_DESCRIPTION = "task_description"
        private const val COLUMN_USER_ID = "user_id" // Foreign key to User table
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = ("CREATE TABLE $TABLE_USER ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_USERNAME TEXT,"
                + "$COLUMN_PASSWORD TEXT" + ")")

        val createTaskTable = ("CREATE TABLE $TABLE_TASK ("
                + "$COLUMN_TASK_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$COLUMN_TASK_NAME TEXT,"
                + "$COLUMN_TASK_DESCRIPTION TEXT,"
                + "$COLUMN_USER_ID INTEGER,"
                + "FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USER($COLUMN_ID) ON DELETE CASCADE" + ")")

        db.execSQL(createUserTable)
        db.execSQL(createTaskTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TASK")
        onCreate(db)
    }

    fun addUser(username: String, password: String) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USERNAME, username)
        values.put(COLUMN_PASSWORD, password)
        db.insert(TABLE_USER, null, values)
        db.close()
    }

    fun checkUser(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USER, arrayOf(COLUMN_ID), "$COLUMN_USERNAME=? AND $COLUMN_PASSWORD=?",
            arrayOf(username, password), null, null, null
        )
        val count = cursor.count
        cursor.close()
        db.close()
        return count > 0
    }

    fun getUserId(username: String): Int {
        val db = this.readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USER, arrayOf(COLUMN_ID), "$COLUMN_USERNAME=?",
            arrayOf(username), null, null, null
        )
        var userId = -1
        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        }
        cursor.close()
        db.close()
        return userId
    }

    fun addTask(task: Task, userId: Int) {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_TASK_NAME, task.name)
        values.put(COLUMN_TASK_DESCRIPTION, task.description)
        values.put(COLUMN_USER_ID, userId)
        db.insert(TABLE_TASK, null, values)
        db.close()
    }

    fun deleteTask(taskId: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_TASK, "$COLUMN_TASK_ID=?", arrayOf(taskId.toString()))
        db.close()
    }

    fun getTasksForUser(userId: Int): List<Task> {
        val taskList = ArrayList<Task>()
        val selectQuery = "SELECT * FROM $TABLE_TASK WHERE $COLUMN_USER_ID=?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val task = Task(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DESCRIPTION))
                )
                taskList.add(task)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return taskList
    }
}
