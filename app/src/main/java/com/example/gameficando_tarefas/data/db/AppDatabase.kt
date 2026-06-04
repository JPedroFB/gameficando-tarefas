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
import com.example.gameficando_tarefas.data.db.dao.ProfileDao
import com.example.gameficando_tarefas.data.db.dao.ProfileStateDao
import com.example.gameficando_tarefas.data.db.dao.TaskDao
import com.example.gameficando_tarefas.data.db.dao.TaskExecutionDao
import com.example.gameficando_tarefas.data.db.entity.GoalEntity
import com.example.gameficando_tarefas.data.db.entity.GoalRedemptionEntity
import com.example.gameficando_tarefas.data.db.entity.ProfileEntity
import com.example.gameficando_tarefas.data.db.entity.ProfileStateEntity
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

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE tasks ADD COLUMN iconEmoji TEXT NOT NULL DEFAULT '🎯'")
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS profiles (
                id INTEGER NOT NULL PRIMARY KEY,
                name TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS profile_state (
                singletonId INTEGER NOT NULL PRIMARY KEY,
                activeProfileId INTEGER NOT NULL,
                FOREIGN KEY(activeProfileId) REFERENCES profiles(id) ON DELETE RESTRICT
            )
            """.trimIndent()
        )
        db.execSQL("INSERT OR IGNORE INTO profiles (id, name) VALUES (1, 'Perfil 1')")
        db.execSQL("INSERT OR IGNORE INTO profiles (id, name) VALUES (2, 'Perfil 2')")
        db.execSQL("INSERT OR IGNORE INTO profile_state (singletonId, activeProfileId) VALUES (1, 1)")
        db.execSQL("ALTER TABLE tasks ADD COLUMN profileId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE goals ADD COLUMN profileId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE task_executions ADD COLUMN profileId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE goal_redemptions ADD COLUMN profileId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_tasks_profileId ON tasks(profileId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_goals_profileId ON goals(profileId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_task_executions_profileId ON task_executions(profileId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_goal_redemptions_profileId ON goal_redemptions(profileId)")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_profile_state_activeProfileId ON profile_state(activeProfileId)")
    }
}

@Database(
    entities = [
        GoalEntity::class,
        TaskEntity::class,
        TaskExecutionEntity::class,
        GoalRedemptionEntity::class,
        ProfileEntity::class,
        ProfileStateEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun taskDao(): TaskDao
    abstract fun taskExecutionDao(): TaskExecutionDao
    abstract fun goalRedemptionDao(): GoalRedemptionDao
    abstract fun profileDao(): ProfileDao
    abstract fun profileStateDao(): ProfileStateDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val seedProfilesCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                db.execSQL("INSERT INTO profiles (id, name) VALUES (1, 'Perfil 1')")
                db.execSQL("INSERT INTO profiles (id, name) VALUES (2, 'Perfil 2')")
                db.execSQL("INSERT INTO profile_state (singletonId, activeProfileId) VALUES (1, 1)")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gameficando_tarefas.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .addCallback(seedProfilesCallback)
                    .build().also { INSTANCE = it }
            }
    }
}

