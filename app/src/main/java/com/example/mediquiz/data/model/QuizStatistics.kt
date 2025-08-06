package com.example.mediquiz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.mediquiz.data.local.converters.QuizStatisticsConverters

// data class statistiche
data class SubjectStatDetail(
    var totalQuestionsAnswered: Int = 0,
    var totalCorrectAnswers: Int = 0
)

@Entity(tableName = "quiz_statistics")
@TypeConverters(QuizStatisticsConverters::class)
data class QuizStatistics(
    @PrimaryKey
    val examId: String,
    val totalQuizzesCompleted: Int = 0,
    // i valori totali per le risposte esatte sono ricavabili da quelli per i singoli argomenti
    val subjectStats: Map<String, SubjectStatDetail> = emptyMap()
)