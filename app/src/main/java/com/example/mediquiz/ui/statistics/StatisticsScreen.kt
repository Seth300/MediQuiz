package com.example.mediquiz.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mediquiz.R
import com.example.mediquiz.data.local.converters.Converters.toQuestionSubject

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val statistics by viewModel.statistics.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            stringResource(id = R.string.statistics_screen_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val currentStats = statistics
        val overallAttempted = viewModel.getOverallQuestionsAnswered()

        if (currentStats == null) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 32.dp))
            Text(stringResource(id = R.string.statistics_loading), modifier = Modifier.padding(top = 8.dp))
        } else {
            // Statistiche generali per esame
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(stringResource(id = R.string.statistics_overall_performance_title), style = MaterialTheme.typography.titleLarge)
                    Text(stringResource(id = R.string.statistics_total_quizzes_completed, currentStats.totalQuizzesCompleted))

                    Text(stringResource(id = R.string.statistics_overall_correct_answers, viewModel.getOverallCorrectAnswers(), viewModel.getOverallQuestionsAnswered()))
                    if (overallAttempted > 0) {
                        val progress = viewModel.getOverallCorrectAnswers().toFloat() / viewModel.getOverallQuestionsAnswered().toFloat()
                        LinearProgressIndicator(
                            progress =  {progress},
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                        )
                    } else {
                        Text(stringResource(id = R.string.statistics_no_quizzes_attempted))
                    }
                }
            }

            // Statistiche per argomento
            Text(
                stringResource(id = R.string.statistics_performance_by_subject_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (currentStats.subjectStats.isEmpty()) {
                Text(
                    stringResource(id = R.string.statistics_no_subject_data),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currentStats.subjectStats.toList()) { (subject, detail) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(stringResource(toQuestionSubject(subject)!!.subjectDisplayNameResId) , style = MaterialTheme.typography.titleMedium)
                                Text(stringResource(id = R.string.statistics_subject_correct_answers, detail.totalCorrectAnswers, detail.totalQuestionsAnswered))
                                if (detail.totalQuestionsAnswered > 0) {
                                    val subjectProgress = detail.totalCorrectAnswers.toFloat() / detail.totalQuestionsAnswered.toFloat()
                                    LinearProgressIndicator(
                                        progress = {subjectProgress},
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .padding(top = 4.dp)
                                    )
                                } else {
                                     Text(stringResource(id = R.string.statistics_no_questions_for_subject))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
