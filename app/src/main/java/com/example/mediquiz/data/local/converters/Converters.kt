package com.example.mediquiz.data.local.converters

import androidx.room.TypeConverter
import com.example.mediquiz.data.model.QuestionSubject
//I converter sono utilizzati per passare i dati al database Room in un formato comprensibile
object Converters {
    // Converter per List<String>
    @TypeConverter
    @JvmStatic
    fun fromString(value: String?): List<String>? {
        return value?.split("\",\"")
    }

    @TypeConverter
    @JvmStatic
    fun fromList(list: List<String>?): String? {
        return list?.joinToString("\",\"")
    }

    // Converters per QuestionSubject
    @TypeConverter
    @JvmStatic
    fun fromQuestionSubject(subject: QuestionSubject?): String? {
        return subject?.name
    }

    @TypeConverter
    @JvmStatic
    fun toQuestionSubject(name: String?): QuestionSubject? {
        return name?.let { enumName ->
            try {
                QuestionSubject.valueOf(enumName)
            } catch (e: IllegalArgumentException) {
                null // Todo: Aggiungere log, gestire l'errore riportando un valore di default
            }
        }
    }
}