package com.example.mediquiz.domain.usecase

import com.example.mediquiz.AppConstants // Added import
import com.example.mediquiz.data.model.Question
import com.example.mediquiz.data.model.QuestionSubject
import com.example.mediquiz.data.repository.QuestionRepository
import com.example.mediquiz.data.repository.UserPreferencesRepository // Added import
import kotlinx.coroutines.flow.first // Added import
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map // Added import
import javax.inject.Inject

// Data class che definisce i dati da passare al ViewModel per il quiz
data class QuizSetupData(
    val questions: List<Question>,
    val isReviewMode: Boolean,
    val reviewQuestionIds: List<Int>?
)

class GetQuizDataUseCase @Inject constructor(
    private val questionRepository: QuestionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(
        examId: String,
        questionIdsString: String?,
        useAllSubjectsFlag: Boolean,
        currentAppliedFilters: Set<QuestionSubject>
    ): QuizSetupData {
        // Recupera il numero di domande selezionato dall'utente
        val actualQuestionCount = userPreferencesRepository.selectedCountFlow
            .map { it ?: AppConstants.DEFAULT_QUIZ_COUNT }
            .first() // Attende la prima emissione

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
            questionsToLoad = questionRepository.getQuestionsByIdsAndExamAndCount(examId, reviewIds, actualQuestionCount)
                .firstOrNull() ?: emptyList()
        } else {
            // quiz normale
            val filtersToUse = if (useAllSubjectsFlag) {
                emptySet()
            } else {
                currentAppliedFilters
            }
            questionsToLoad = questionRepository.getRandomQuestions(
                count = actualQuestionCount,
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
