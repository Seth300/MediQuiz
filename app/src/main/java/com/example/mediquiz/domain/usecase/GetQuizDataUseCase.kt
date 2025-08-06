package com.example.mediquiz.domain.usecase

import com.example.mediquiz.data.model.Question
import com.example.mediquiz.data.model.QuestionSubject
import com.example.mediquiz.data.repository.QuestionRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

// Data class che definisce i dati da passare al ViewModel per il quiz
data class QuizSetupData(
    val questions: List<Question>,
    val isReviewMode: Boolean,
    val reviewQuestionIds: List<Int>?
)

class GetQuizDataUseCase @Inject constructor(
    private val questionRepository: QuestionRepository
) {
    suspend operator fun invoke(
        examId: String,
        questionIdsString: String?,
        useAllSubjectsFlag: Boolean,
        currentAppliedFilters: Set<QuestionSubject>,
        questionCount: Int
    ): QuizSetupData {
        // 1. Verifica se è una revisione errori
        //  Verifica che gli ID siano validi e non siano vuoti.
        val reviewIds = questionIdsString?.split(',')
            ?.mapNotNull { it.trim().toIntOrNull() }
            ?.filter { it != 0 }
            ?.takeIf { it.isNotEmpty() }

        val isReviewMode = reviewIds != null

        // 2. Recupera le domande
        val questionsToLoad: List<Question>
        if (isReviewMode) {
            questionsToLoad = questionRepository.getQuestionsByIdsAndExamAndCount(examId, reviewIds,questionCount) // <<< MODIFIED to use getQuestionsByIdsAndExam
                .firstOrNull() ?: emptyList()
        } else {
            // quiz normale
            val filtersToUse = if (useAllSubjectsFlag) {
                emptySet()
            } else {
                currentAppliedFilters
            }
            questionsToLoad = questionRepository.getRandomQuestions(
                count = questionCount,
                examId = examId,
                subjects = filtersToUse
            )
        }

        return QuizSetupData(
            questions = questionsToLoad,
            isReviewMode = isReviewMode,
            reviewQuestionIds = reviewIds
        )
    }
}
