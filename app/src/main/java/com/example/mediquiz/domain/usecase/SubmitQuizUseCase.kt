package com.example.mediquiz.domain.usecase

import android.util.Log
import com.example.mediquiz.data.model.Question
import com.example.mediquiz.data.model.SubjectStatDetail
import com.example.mediquiz.data.repository.StatisticsRepository
import javax.inject.Inject

data class SubmissionResult(
    val correctAnswers: Int,
    val totalQuestions: Int
)

class SubmitQuizUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    private val TAG = "SubmitQuizUseCase"

    suspend operator fun invoke(
        examId: String,
        currentQuestions: List<Question>,
        selectedAnswers: List<String?>,
        isReviewQuizMode: Boolean
    ): SubmissionResult {
        var correctAnswers = 0
        val subjectResultsForCurrentQuiz = mutableMapOf<String, SubjectStatDetail>()

        currentQuestions.forEachIndexed { index, question ->
            val selectedAnswer = selectedAnswers.getOrNull(index)
            val subjectName = question.subject.name

            if (!isReviewQuizMode) {
                val currentSubjectDetail = subjectResultsForCurrentQuiz.getOrPut(subjectName) { SubjectStatDetail() }
                currentSubjectDetail.totalQuestionsAnswered++
                if (selectedAnswer == question.correctAnswer) {
                    currentSubjectDetail.totalCorrectAnswers++
                }
            }

            if (selectedAnswer == question.correctAnswer) {
                correctAnswers++
                if (isReviewQuizMode) {
                    try {
                        Log.d(TAG, "Review quiz - Correct answer for question ID ${question.id}. Clearing from stats for exam $examId.")
                        statisticsRepository.clearLogsForQuestion( question.id)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error clearing reviewed question ID ${question.id} for exam $examId", e)
                    }
                }
            } else {
                if (selectedAnswer != null && !isReviewQuizMode) {
                    try {
                        statisticsRepository.logIncorrectAnswer(
                            questionId = question.id,
                            userAnswer = selectedAnswer
                        )
                        Log.d(TAG, "Logged incorrect answer for question ID ${question.id} for exam $examId")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error logging incorrect answer for question ID ${question.id} for exam $examId", e)
                    }
                }
            }
        }

        if (!isReviewQuizMode) {
            recordQuizCompletionToStats(examId = examId, results = subjectResultsForCurrentQuiz) // <<< MODIFIED
            Log.d(TAG, "Normal Quiz submitted for exam $examId. Score: $correctAnswers/${currentQuestions.size}. Statistics recorded.")
        } else {
            Log.d(TAG, "Review Quiz submitted for exam $examId. Score: $correctAnswers/${currentQuestions.size}.")
        }

        return SubmissionResult(correctAnswers, currentQuestions.size)
    }

    private suspend fun recordQuizCompletionToStats(examId: String, results: Map<String, SubjectStatDetail>) {
        Log.d(TAG, "recordQuizCompletionToStats: Recording quiz completion for exam $examId with ${results.size} subjects.")
        try {
            statisticsRepository.recordQuizCompletion(examId, results)
            Log.d(TAG, "recordQuizCompletionToStats: Statistics updated successfully for exam $examId.")
        } catch (e: Exception) {
            Log.e(TAG, "recordQuizCompletionToStats: Error updating statistics for exam $examId", e)
        }
    }
}
