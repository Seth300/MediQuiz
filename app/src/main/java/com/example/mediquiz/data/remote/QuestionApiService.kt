package com.example.mediquiz.data.remote

import com.example.mediquiz.data.remote.dto.QuestionServerModel
import retrofit2.http.GET

interface QuestionApiService {
    // Indica l'URL da cui ottenere le domande
    @GET("https://seth300.github.io/MediQuiz/questions/question.json")
    suspend fun getRemoteQuestions(): List<QuestionServerModel>
}
