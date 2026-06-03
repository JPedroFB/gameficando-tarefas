package com.example.gameficando_tarefas.data.db

import androidx.room.TypeConverter
import com.example.gameficando_tarefas.domain.model.TaskFrequency

class Converters {
    @TypeConverter
    fun fromFrequency(value: TaskFrequency): String = value.name

    @TypeConverter
    fun toFrequency(value: String): TaskFrequency = TaskFrequency.valueOf(value)
}
