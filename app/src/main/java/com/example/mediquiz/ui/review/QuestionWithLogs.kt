package com.example.mediquiz.ui.review

import com.example.mediquiz.data.model.IncorrectAnswerLog
import com.example.mediquiz.data.model.Question

data class QuestionWithLogs(
    val question: Question,
    val logs: List<IncorrectAnswerLog>
)