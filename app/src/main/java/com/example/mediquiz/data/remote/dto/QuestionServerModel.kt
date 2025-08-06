package com.example.mediquiz.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QuestionServerModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("examId")
    val examId: String,
    @SerializedName("questionText")
    val questionText: String,
    @SerializedName("questionAnswers")
    val questionAnswers: List<String>,
    @SerializedName("correctAnswer")
    val correctAnswer: String,
    @SerializedName("subject")
    val subject: String
)