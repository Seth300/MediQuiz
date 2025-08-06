package com.example.mediquiz.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediquiz.AppConstants
import com.example.mediquiz.data.model.QuizStatistics
import com.example.mediquiz.data.model.SubjectStatDetail
import com.example.mediquiz.data.repository.StatisticsRepository
import com.example.mediquiz.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val statisticsRepository: StatisticsRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val currentExamId: StateFlow<String> = userPreferencesRepository.selectedExamIdFlow
        .map { it ?: AppConstants.DEFAULT_EXAM_ID }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AppConstants.DEFAULT_EXAM_ID
        )

    val statistics: StateFlow<QuizStatistics?> = currentExamId.flatMapLatest { examId ->
        statisticsRepository.getStatistics(examId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = null
    )

    val safeStatistics: StateFlow<QuizStatistics> = currentExamId.flatMapLatest { examId ->
        statisticsRepository.getStatistics(examId)
            .map { it ?: QuizStatistics(examId = examId, totalQuizzesCompleted = 0, subjectStats = emptyMap()) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = QuizStatistics(examId = AppConstants.DEFAULT_EXAM_ID, totalQuizzesCompleted = 0, subjectStats = emptyMap())
    )

    fun getTotalQuizzesCompleted(): Int {
        return statistics.value?.totalQuizzesCompleted ?: 0
    }

    fun getSubjectStatsMap(): Map<String, SubjectStatDetail> {
        return statistics.value?.subjectStats ?: emptyMap()
    }

    fun getOverallCorrectAnswers(): Int {
        return statistics.value?.subjectStats?.values?.sumOf { it.totalCorrectAnswers } ?: 0
    }

    fun getOverallQuestionsAnswered(): Int {
        return statistics.value?.subjectStats?.values?.sumOf { it.totalQuestionsAnswered } ?: 0
    }
}
