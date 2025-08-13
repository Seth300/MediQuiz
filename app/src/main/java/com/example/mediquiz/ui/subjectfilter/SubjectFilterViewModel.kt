package com.example.mediquiz.ui.subjectfilter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediquiz.AppConstants
import com.example.mediquiz.data.model.Exam
import com.example.mediquiz.data.model.QuestionSubject
import com.example.mediquiz.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SubjectFilterUiState(
    val allSubjects: List<QuestionSubject> = emptyList(),
    val selectedSubjects: Set<QuestionSubject> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentExam: Exam? = null
)

@HiltViewModel
class SubjectFilterViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectFilterUiState(isLoading = false))
    val uiState: StateFlow<SubjectFilterUiState> = _uiState.asStateFlow()

    private val TAG = "SubjectFilterViewModel"

    val selectedExamFlow: StateFlow<Exam> = userPreferencesRepository.selectedExamIdFlow
        .map { examIdString ->
            Exam.fromId(examIdString) ?: AppConstants.DEFAULT_EXAM
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AppConstants.DEFAULT_EXAM
        )


    init {
        viewModelScope.launch {
            selectedExamFlow.collect { exam ->
                Log.d(TAG, "Current exam selected: ${exam.id}, subjects: ${exam.subjects.joinToString { it.name }}")
                _uiState.update {
                    it.copy(
                        currentExam = exam,
                        allSubjects = exam.subjects,
                        selectedSubjects = emptySet(),
                        isLoading = false,
                        error = null
                    )
                }
            }
        }
    }

    fun toggleSubjectSelection(subject: QuestionSubject) {
        _uiState.update { currentState ->
            if (currentState.currentExam?.subjects?.contains(subject) == true) {
                val newSelectedSubjects = currentState.selectedSubjects.toMutableSet()
                if (newSelectedSubjects.contains(subject)) {
                    newSelectedSubjects.remove(subject)
                } else {
                    newSelectedSubjects.add(subject)
                }
                currentState.copy(selectedSubjects = newSelectedSubjects)
            } else {
                Log.w(TAG, "Attempted to toggle subject ($subject) not in current exam (${currentState.currentExam?.id}) subjects.")
                currentState
            }
        }
    }

    fun getSelectedSubjects(): Set<QuestionSubject> {
        return _uiState.value.selectedSubjects
    }

    fun setCurrentFilters(currentFilters: Set<QuestionSubject>) {
        val validFilters = _uiState.value.currentExam?.subjects?.let { validSubjects ->
            currentFilters.filter { validSubjects.contains(it) }.toSet()
        } ?: emptySet()
        Log.d(TAG, "setCurrentFilters - Initial: ${currentFilters.joinToString { it.name }}, Validated against ${uiState.value.currentExam?.id}: ${validFilters.joinToString { it.name }}")
        _uiState.update {
            it.copy(selectedSubjects = validFilters)
        }
    }
}
