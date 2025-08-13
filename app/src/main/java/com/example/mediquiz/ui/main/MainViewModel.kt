package com.example.mediquiz.ui.main

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediquiz.AppConstants
import com.example.mediquiz.R
import com.example.mediquiz.data.model.Exam
import com.example.mediquiz.data.model.Question
import com.example.mediquiz.data.model.QuestionSubject
import com.example.mediquiz.data.repository.QuestionRepository
import com.example.mediquiz.data.repository.UserPreferencesRepository
import com.example.mediquiz.domain.usecase.EnsureInitialStatsUseCase
import com.example.mediquiz.domain.usecase.GetQuizDataUseCase
import com.example.mediquiz.domain.usecase.QuizSetupData
import com.example.mediquiz.domain.usecase.SubmissionResult
import com.example.mediquiz.domain.usecase.SubmitQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val getQuizDataUseCase: GetQuizDataUseCase,
    private val submitQuizUseCase: SubmitQuizUseCase,
    private val ensureInitialStatsUseCase: EnsureInitialStatsUseCase,
    private val questionRepository: QuestionRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val TAG = "MainViewModel"

    val selectedCount: StateFlow<Int> = userPreferencesRepository.selectedCountFlow
        .map { it ?: AppConstants.DEFAULT_QUIZ_COUNT }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AppConstants.DEFAULT_QUIZ_COUNT
        )

    fun updateSelectedCount(count: Int) {
        viewModelScope.launch {
            userPreferencesRepository.updateSelectedCount(count)
            Log.d(TAG, "Selected count updated to: $count ")
        }
    }

    val selectedExam: StateFlow<Exam> = userPreferencesRepository.selectedExamIdFlow
        .map { examIdString ->
            Exam.fromId(examIdString) ?: AppConstants.DEFAULT_EXAM
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AppConstants.DEFAULT_EXAM
        )

    fun updateSelectedExam(exam: Exam) {
        viewModelScope.launch {
            userPreferencesRepository.updateSelectedExamId(exam.id)
            Log.d(TAG, "Selected exam updated to: ${exam.id} (${application.getString(exam.displayNameResId)})")
        }
    }

    private val _questionSet = MutableStateFlow<List<Question>>(emptyList())
    val questionSet: StateFlow<List<Question>> = _questionSet.asStateFlow()

    private val _selectedQuestionIndex = MutableStateFlow(0)
    val selectedQuestionIndex: StateFlow<Int> = _selectedQuestionIndex.asStateFlow()

    private val _quizSubmitted = MutableStateFlow(false)
    val quizSubmitted: StateFlow<Boolean> = _quizSubmitted.asStateFlow()

    private val _appliedSubjectFilters = MutableStateFlow<Set<QuestionSubject>>(emptySet())
    val appliedSubjectFilters: StateFlow<Set<QuestionSubject>> = _appliedSubjectFilters.asStateFlow()

    private var _selectedAnswers = mutableStateListOf<String?>()

    private val _isReviewQuizMode = MutableStateFlow(false)
    val isReviewQuizMode: StateFlow<Boolean> = _isReviewQuizMode.asStateFlow()
    private var reviewQuestionIdsInternal: List<Int>? = null

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private val _syncStatusMessage = MutableStateFlow<String?>(null)
    val syncStatusMessage: StateFlow<String?> = _syncStatusMessage.asStateFlow()

    init {
        Log.d(TAG, "init: ViewModel instance CREATED.")
        viewModelScope.launch {
            selectedExam.collect { currentExam ->
                Log.d(TAG, "init (observing selectedExam): Current selected exam is ${currentExam.id}. Ensuring stats.")
                try {
                    ensureInitialStatsUseCase(currentExam.id)
                    Log.d(TAG, "init: Initial statistics row ensured for exam ${currentExam.id}.")
                } catch (e: Exception) {
                    Log.e(TAG, "init: Error ensuring initial statistics row for exam ${currentExam.id} via UseCase", e)
                }
            }
        }

        viewModelScope.launch {
            combine(
                userPreferencesRepository.selectedSubjectFiltersFlow,
                selectedExam
            ) { persistedSubjectNames, currentExam ->
                Log.d(TAG, "Persisted subject names: $persistedSubjectNames for exam: ${currentExam.id}")
                val validPersistedSubjects = persistedSubjectNames
                    .mapNotNull { name ->
                        try {
                            QuestionSubject.valueOf(name)
                        } catch (e: IllegalArgumentException) {
                            Log.w(TAG, "Invalid subject name '$name' in preferences.", e)
                            null
                        }
                    }
                    .filter { subject -> currentExam.subjects.contains(subject) }
                    .toSet()

                Log.d(TAG, "Validated persisted subjects for exam ${currentExam.id}: $validPersistedSubjects")
                _appliedSubjectFilters.value = validPersistedSubjects
            }.collect() // Oppure .launchIn(viewModelScope),considerare differenze
        }
    }

    fun syncDatabase() {
        selectedExam.value.id
        viewModelScope.launch {
            _syncError.value = null
            _syncStatusMessage.value = application.getString(R.string.home_sync_button_syncing_text)
            _isSyncing.value = true
            Log.d(TAG, "syncDatabase: Starting database synchronization.")
            try {
                questionRepository.refreshQuestionsFromRemoteSource()
                Log.d(TAG, "syncDatabase: Database synchronization successful.")
                _syncStatusMessage.value = application.getString(R.string.db_sync_success)
            } catch (e: Exception) {
                Log.e(TAG, "syncDatabase: Error during database synchronization.", e)
                val errorMessageDetail = e.localizedMessage ?: application.getString(R.string.main_view_model_unknown_error)
                _syncError.value = application.getString(R.string.main_view_model_error_syncing_database, errorMessageDetail)
                _syncStatusMessage.value = application.getString(R.string.db_sync_failed)
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun clearSyncError() {
        _syncError.value = null
    }

    fun clearSyncStatusMessage() {
        _syncStatusMessage.value = null
    }

    fun prepareQuiz(questionIdsString: String?, useAllSubjectsFromNav: Boolean) {
        val currentExamId = selectedExam.value.id
        Log.d(TAG, "prepareQuiz called for exam: $currentExamId. questionIdsString: '$questionIdsString', useAllSubjectsFromNav: $useAllSubjectsFromNav, appliedFilters: ${_appliedSubjectFilters.value.joinToString { it.name }}")
        viewModelScope.launch {
            try {
                val quizSetupData: QuizSetupData = getQuizDataUseCase(
                    examId = currentExamId,
                    questionIdsString = questionIdsString,
                    useAllSubjectsFlag = useAllSubjectsFromNav,
                    currentAppliedFilters = _appliedSubjectFilters.value
                )

                _questionSet.value = quizSetupData.questions
                _isReviewQuizMode.value = quizSetupData.isReviewMode
                reviewQuestionIdsInternal = quizSetupData.reviewQuestionIds

                _selectedAnswers.clear()
                if (quizSetupData.questions.isNotEmpty()) {
                    _selectedAnswers.addAll(List(quizSetupData.questions.size) { null })
                    _selectedQuestionIndex.value = 0
                } else {
                    _selectedQuestionIndex.value = 0
                }
                _quizSubmitted.value = false

                Log.d(TAG, "prepareQuiz: UseCase processed for exam $currentExamId. ReviewMode: ${quizSetupData.isReviewMode}, Questions: ${quizSetupData.questions.size}")

            } catch (e: Exception) {
                Log.e(TAG, "prepareQuiz: Error calling GetQuizDataUseCase or processing its result for exam $currentExamId", e)
                _questionSet.value = emptyList()
                _isReviewQuizMode.value = false
                reviewQuestionIdsInternal = null
                _selectedAnswers.clear()
                _selectedQuestionIndex.value = 0
                _quizSubmitted.value = false
            }
        }
    }

    fun applySubjectFilters(selectedSubjects: Set<QuestionSubject>) {
        if (_isReviewQuizMode.value) {
            Log.d(TAG, "applySubjectFilters called in review quiz mode. Filters will be ignored.")
            return
        }
        val currentExamId = selectedExam.value.id
        Log.d(TAG, "applySubjectFilters called with ${selectedSubjects.size} subjects for exam: $currentExamId.")
        _appliedSubjectFilters.value = selectedSubjects

        viewModelScope.launch {
            userPreferencesRepository.updateSelectedSubjectFilters(selectedSubjects.map { it.name }.toSet())
            Log.d(TAG, "Persisted selected subjects: ${selectedSubjects.map { it.name }}")
        }
        prepareQuiz(questionIdsString = null, useAllSubjectsFromNav = false)
    }

    fun refreshQuestions() {
        val currentExamId = selectedExam.value.id
        Log.d(TAG, "refreshQuestions called for exam: $currentExamId.")
        val currentQuestionIdsString = if (_isReviewQuizMode.value) reviewQuestionIdsInternal?.joinToString(",") else null
        val useAllSubjects = !_isReviewQuizMode.value && _appliedSubjectFilters.value.isEmpty()
        prepareQuiz(currentQuestionIdsString, useAllSubjects)
    }

    fun getSelectedAnswer(index: Int): String? {
        return _selectedAnswers.getOrNull(index)
    }

    fun getSelectedAnswersList(): List<String?> {
        return _selectedAnswers.toList()
    }

    fun onAnswerSelected(questionIndex: Int, answer: String) {
        if (questionIndex >= 0 && questionIndex < _selectedAnswers.size) {
            _selectedAnswers[questionIndex] = answer
        }
    }

    fun onQuestionSelected(newIndex: Int) {
        if (newIndex >= 0 && newIndex < _questionSet.value.size) {
            _selectedQuestionIndex.value = newIndex
        }
    }

    fun submitQuiz() {
        val currentExamId = selectedExam.value.id
        viewModelScope.launch {
            try {
                Log.d(TAG, "Submitting quiz for exam $currentExamId. Questions: ${_questionSet.value.size}, ReviewMode: ${_isReviewQuizMode.value}")
                val result: SubmissionResult = submitQuizUseCase(
                    examId = currentExamId,
                    currentQuestions = _questionSet.value,
                    selectedAnswers = _selectedAnswers.toList(),
                    isReviewQuizMode = _isReviewQuizMode.value
                )
                Log.d(TAG, "Quiz submission processed by UseCase for exam $currentExamId. Score: ${result.correctAnswers}/${result.totalQuestions}")
                _quizSubmitted.value = true
            } catch (e: Exception) {
                Log.e(TAG, "submitQuiz: Error calling SubmitQuizUseCase or processing its result for exam $currentExamId", e)
                _quizSubmitted.value = false
            }
        }
    }

    fun retakeQuiz() {
        Log.d(TAG, "retakeQuiz called for exam: ${selectedExam.value.id}.")
        _quizSubmitted.value = false
        refreshQuestions()
    }
}
