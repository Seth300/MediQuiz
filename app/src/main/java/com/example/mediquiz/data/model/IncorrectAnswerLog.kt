package com.example.mediquiz.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "incorrect_answer_log",
    foreignKeys = [
        ForeignKey(
            entity = Question::class,
            parentColumns = ["id"],
            childColumns = ["questionId"],
            onDelete = ForeignKey.CASCADE // Quando si cancella una domanda si cancellano anche i log degli errori
        )
    ],
    indices = [Index(value = ["questionId"])] // Presente per permettere la ricerca rapida tramite ID
)
data class IncorrectAnswerLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Long utilizzato perché standard
    val questionId: Int,
    val userAnswer: String,
    val timestamp: Long // Presente per future implementazioni di sistemi SRS
)
