package com.example.mediquiz.ui.review

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediquiz.AppConstants
import com.example.mediquiz.data.repository.QuestionRepository
import com.example.mediquiz.data.repository.StatisticsRepository
import com.example.mediquiz.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val questionRepository: QuestionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _isLoadingSummaries = MutableStateFlow(false)
    val isLoadingSummaries: StateFlow<Boolean> = _isLoadingSummaries.asStateFlow()

    val currentExamId: StateFlow<String> = userPreferencesRepository.selectedExamIdFlow
        .map { it ?: AppConstants.DEFAULT_EXAM_ID }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AppConstants.DEFAULT_EXAM_ID
        )

    // Trigger per il refresh della lista di domande sbagliate
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1)

    val incorrectlyAnsweredQuestions: StateFlow<List<ReviewableQuestion>> =
        combine(currentExamId, refreshTrigger) { examId, _ ->
            examId
        }.flatMapLatest { examId ->
            _isLoadingSummaries.value = true
            Log.d("ReviewViewModel", "Starting to load summaries for exam: $examId")
            flow {
                try {
                    // 1. Ottieni tutte le domande sbagliate distinte
                    val distinctIds = statisticsRepository.getDistinctIncorrectQuestionIds().firstOrNull() ?: emptyList()
                    Log.d("ReviewViewModel", "All distinct incorrect IDs: $distinctIds")

                    if (distinctIds.isNotEmpty()) {
                        // 2. Filtra per examId
                        questionRepository.getQuestionsByIdsAndExam(examId, distinctIds)
                            .map { questionsForExam ->
                                Log.d("ReviewViewModel", "Questions for exam $examId (ID: ${questionsForExam.map { it.id }}): ${questionsForExam.size}")
                                val reviewableList = mutableListOf<ReviewableQuestion>()
                                for (question in questionsForExam) {
                                    val logs = statisticsRepository.getLogsForQuestion(question.id).firstOrNull() ?: emptyList()
                                    val lastIncorrectAnswer = logs.firstOrNull()?.userAnswer
                                    val incorrectCount = logs.size
                                    if (incorrectCount > 0) {
                                        reviewableList.add(
                                            ReviewableQuestion(
                                                question = question,
                                                lastIncorrectAnswer = lastIncorrectAnswer,
                                                incorrectCount = incorrectCount
                                            )
                                        )
                                    }
                                }
                                Log.d("ReviewViewModel", "Reviewable list for exam $examId: ${reviewableList.size} items")
                                reviewableList
                            }
                            .catch { e ->
                                Log.e("ReviewViewModel", "Error collecting questions for exam $examId", e)
                                emit(emptyList<ReviewableQuestion>())
                            }
                            .collect { reviewableList ->
                                emit(reviewableList)
                            }
                    } else {
                        Log.d("ReviewViewModel", "No distinct incorrect IDs found. Emitting empty list.")
                        emit(emptyList<ReviewableQuestion>())
                    }
                } catch (e: Exception) {
                    Log.e("ReviewViewModel", "Error in incorrectlyAnsweredQuestions flow for exam $examId", e)
                    emit(emptyList<ReviewableQuestion>())
                } finally {
                    _isLoadingSummaries.value = false
                    Log.d("ReviewViewModel", "Finished loading summaries for exam: $examId")
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    private val _selectedQuestionDetail = MutableStateFlow<QuestionWithLogs?>(null)
    val selectedQuestionDetail: StateFlow<QuestionWithLogs?> = _selectedQuestionDetail.asStateFlow()

    init {
        viewModelScope.launch {
            // trigger per il refresh
            statisticsRepository.incorrectLogChanged.collect {
                Log.d("ReviewViewModel", "incorrectLogChanged signal received, triggering refresh.")
                refreshTrigger.tryEmit(Unit)
            }
        }
        // inizializza i dati
        viewModelScope.launch {
            Log.d("ReviewViewModel", "Initial trigger for loading summaries.")
            refreshTrigger.emit(Unit)
        }
    }

    //loading screen, utilizzo improbabile
    fun loadQuestionWithLogs(questionId: Int) {
        viewModelScope.launch {
            _isLoadingSummaries.value = true
            try {
                val question = questionRepository.getQuestionById(questionId).firstOrNull()
                val logs = statisticsRepository.getLogsForQuestion(questionId).firstOrNull() ?: emptyList()
                _selectedQuestionDetail.value = question?.let { QuestionWithLogs(it, logs) }
            } catch (e: Exception) {
                Log.e("ReviewViewModel", "Error loading question with logs for ID $questionId", e)
                _selectedQuestionDetail.value = null
            } finally {
                _isLoadingSummaries.value = false
            }
        }
    }

    fun clearHistoryForQuestion(questionId: Int) {
        viewModelScope.launch {
            statisticsRepository.clearLogsForQuestion(questionId)
            if (_selectedQuestionDetail.value?.question?.id == questionId) {
                _selectedQuestionDetail.value = null
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            statisticsRepository.clearAllIncorrectAnswerLogs()
            _selectedQuestionDetail.value = null
        }
    }

    fun prepareReviewQuiz(navigateToQuiz: (questionIds: List<Int>) -> Unit) {
        val ids = incorrectlyAnsweredQuestions.value.map { it.question.id }
        if (ids.isNotEmpty()) {
            navigateToQuiz(ids)
        } else {
            Log.w("ReviewViewModel", "Prepare review quiz called with no incorrect questions for current exam.")
        }
    }
}
