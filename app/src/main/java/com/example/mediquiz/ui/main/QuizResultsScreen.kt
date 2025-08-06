package com.example.mediquiz.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediquiz.R
//com.example.mediquiz.data.model.Question è implicita

@Composable
fun QuizResultsScreen(
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit
) {
    val questionSet by viewModel.questionSet.collectAsStateWithLifecycle()
    val selectedAnswers = viewModel.getSelectedAnswersList()

    var correctAnswersCount = 0
    questionSet.forEachIndexed { index, question ->
        if (selectedAnswers.getOrNull(index) == question.correctAnswer) {
            correctAnswersCount++
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(16.dp), 
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = stringResource(id = R.string.quiz_results_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = stringResource(id = R.string.quiz_results_score, correctAnswersCount, questionSet.size),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            LazyColumn(modifier = Modifier.weight(1f)) {
                itemsIndexed(questionSet) { index, question ->
                    val userAnswer = selectedAnswers.getOrNull(index)
                    val isCorrect = userAnswer == question.correctAnswer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            stringResource(id = R.string.quiz_question_prefix, index + 1) + question.questionText,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCorrect) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                contentDescription = if (isCorrect) stringResource(id = R.string.quiz_answer_correct_desc) else stringResource(id = R.string.quiz_answer_incorrect_desc),
                                tint = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                stringResource(id = R.string.quiz_results_your_answer, userAnswer ?: stringResource(id = R.string.quiz_results_not_answered)),
                                color = if (isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (!isCorrect) {
                            Text(
                                stringResource(id = R.string.quiz_results_correct_answer_was, question.correctAnswer),
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 32.dp)
                            )
                        }
                    }
                }
            }
            Button(
                onClick = { viewModel.retakeQuiz() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(stringResource(id = R.string.quiz_button_retake))
            }
            Button(
                onClick = onNavigateHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text(stringResource(id = R.string.quiz_button_back_to_home))
            }
        }
    }
}
