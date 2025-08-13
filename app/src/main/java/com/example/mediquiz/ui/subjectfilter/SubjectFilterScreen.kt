package com.example.mediquiz.ui.subjectfilter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mediquiz.R
import com.example.mediquiz.data.model.Exam
import com.example.mediquiz.data.model.QuestionSubject
import com.example.mediquiz.ui.components.ExamSelectorDropdown
import com.example.mediquiz.ui.components.QuestionCountSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectFilterScreen(
    modifier: Modifier = Modifier,
    subjectFilterViewModel: SubjectFilterViewModel = viewModel(),
    selectedExam: Exam,
    onExamSelected: (Exam) -> Unit,
    currentSelectedCount: Int,
    onCountChanged: (Int) -> Unit,
    initialSelectedFilters: Set<QuestionSubject>,
    onApplyFilters: (Set<QuestionSubject>) -> Unit,
) {
    val uiState by subjectFilterViewModel.uiState.collectAsState()

    // gestiscee il passaggio dei filtri da MainViewModel a SubjectFilterViewModel
    LaunchedEffect(initialSelectedFilters, uiState.currentExam) {
        // Garantisce che se il filtro iniziale non è settato si usa quello dell'esame scelto
        if (uiState.currentExam != null) {
            subjectFilterViewModel.setCurrentFilters(initialSelectedFilters)
        }
    }

    // Osserva l'esame scelto da MainViewmodel
    LaunchedEffect(selectedExam) {
        // Serve per sicurezza, non dovrebbe essere chiamato
        //TODO: Al momento abbiamo molti viewmodel che osservano le stesse variabili, operazioni ridondanti da eliminare
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(id = R.string.subject_filter_screen_title)) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ExamSelectorDropdown(
                selectedExam = selectedExam,
                onExamSelected = { newExam ->
                    onExamSelected(newExam)
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            QuestionCountSelector(
                currentCount = currentSelectedCount,
                onCountSelected = onCountChanged,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Text(
                stringResource(id = R.string.subject_filter_instruction),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Text("Loading subjects...")
            } else if (uiState.error != null) {
                Text("Error: ${uiState.error}")
            } else if (uiState.allSubjects.isEmpty() && uiState.currentExam != null) {
                Text(stringResource(id = R.string.subject_filter_no_subjects_for_exam, uiState.currentExam?.let { stringResource(it.displayNameResId)} ?: "selected exam"))
            } else if (uiState.allSubjects.isEmpty()) {
                 Text(stringResource(id = R.string.subject_filter_no_subjects))
            }else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.allSubjects) { subject ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(stringResource(subject.subjectDisplayNameResId))
                            Checkbox(
                                checked = uiState.selectedSubjects.contains(subject),
                                onCheckedChange = { subjectFilterViewModel.toggleSubjectSelection(subject) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onApplyFilters(subjectFilterViewModel.getSelectedSubjects())
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(id = R.string.subject_filter_apply_button))
            }
        }
    }
}
