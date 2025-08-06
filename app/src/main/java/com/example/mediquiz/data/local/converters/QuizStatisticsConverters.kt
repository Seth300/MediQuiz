package com.example.mediquiz.data.local.converters

import androidx.room.TypeConverter
import com.example.mediquiz.data.model.SubjectStatDetail
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
//I converter sono utilizzati per passare i dati al database Room in un formato comprensibile
class QuizStatisticsConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromSubjectStatsMap(stats: Map<String, SubjectStatDetail>?): String? {
        if (stats == null) {
            return null
        }
        return gson.toJson(stats)
    }

    @TypeConverter
    fun toSubjectStatsMap(data: String?): Map<String, SubjectStatDetail>? {
        if (data == null) {
            return null
        }
        val mapType = object : TypeToken<Map<String, SubjectStatDetail>>() {}.type
        return gson.fromJson(data, mapType)
    }
}