package com.example.mediquiz.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters // Keep this import for List<String>
import com.example.mediquiz.data.local.converters.Converters

// Question per il database Room
@Entity(tableName = "questions")
data class Question(
    @PrimaryKey
    val id: Int,
    var questionText: String,
    val examId: String,
    @field:TypeConverters(Converters::class)
    var listOfAnswers: List<String>,
    var correctAnswer: String,
    var subject: QuestionSubject
)


