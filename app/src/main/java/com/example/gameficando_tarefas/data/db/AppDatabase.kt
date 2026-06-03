package com.example.gameficando_tarefas.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gameficando_tarefas.data.db.dao.GoalDao
import com.example.gameficando_tarefas.data.db.dao.GoalRedemptionDao
import com.example.gameficando_tarefas.data.db.dao.TaskDao
import com.example.gameficando_tarefas.data.db.dao.TaskExecutionDao
import com.example.gameficando_tarefas.data.db.entity.GoalEntity
import com.example.gameficando_tarefas.data.db.entity.GoalRedemptionEntity
import com.example.gameficando_tarefas.data.db.entity.TaskEntity
import com.example.gameficando_tarefas.data.db.entity.TaskExecutionEntity

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS goal_redemptions (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                goalId INTEGER NOT NULL,
                goalDescription TEXT NOT NULL,
                pointsCost INTEGER NOT NULL,
                redeemedAt INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE tasks ADD COLUMN sortOrder INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(
    entities = [GoalEntity::class, TaskEntity::class, TaskExecutionEntity::class, GoalRedemptionEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun taskDao(): TaskDao
    abstract fun taskExecutionDao(): TaskExecutionDao
    abstract fun goalRedemptionDao(): GoalRedemptionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gameficando_tarefas.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build().also { INSTANCE = it }
            }
    }
}

