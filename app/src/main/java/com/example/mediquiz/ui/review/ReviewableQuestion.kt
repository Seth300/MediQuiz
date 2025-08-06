package com.example.mediquiz.ui.review

import com.example.mediquiz.data.model.Question

data class ReviewableQuestion(
    val question: Question,
    val lastIncorrectAnswer: String?,
    val incorrectCount: Int // Numero di risposte sbagliate, presente per futuri tipi di review
)
