package com.example.gameficando_tarefas.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.gameficando_tarefas.data.db.dao.GoalDao
import com.example.gameficando_tarefas.data.db.dao.TaskDao
import com.example.gameficando_tarefas.data.db.dao.TaskExecutionDao
import com.example.gameficando_tarefas.data.db.entity.GoalEntity
import com.example.gameficando_tarefas.data.db.entity.TaskEntity
import com.example.gameficando_tarefas.data.db.entity.TaskExecutionEntity

@Database(
    entities = [GoalEntity::class, TaskEntity::class, TaskExecutionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun taskDao(): TaskDao
    abstract fun taskExecutionDao(): TaskExecutionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gameficando_tarefas.db"
                ).build().also { INSTANCE = it }
            }
    }
}
