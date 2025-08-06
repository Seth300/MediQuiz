package com.example.mediquiz.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediquiz.R
import com.example.mediquiz.data.model.Question

@Composable
fun MakeQuizScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit
) {
    val questionSet by viewModel.questionSet.collectAsStateWithLifecycle()
    val selectedQuestionIndex by viewModel.selectedQuestionIndex.collectAsStateWithLifecycle()
    val quizSubmitted by viewModel.quizSubmitted.collectAsStateWithLifecycle()

    if (questionSet.isEmpty()) {
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) { // <<< ADDED SURFACE
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.quiz_no_questions_loading))
            }
        }
        return
    }

    if (quizSubmitted) {
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            QuizResultsScreen(
                viewModel = viewModel,
                onNavigateHome = onNavigateHome
            )
        }
    } else {
        val currentQuestion = questionSet.getOrNull(selectedQuestionIndex)

        if (currentQuestion == null) {
            Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(id = R.string.quiz_error_loading_question))
                }
            }
            return
        }

        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.navigationBars.asPaddingValues())
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    MakeQuizQuestion(
                        question = currentQuestion,
                        selectedAnswerForThisQuestion = viewModel.getSelectedAnswer(selectedQuestionIndex),
                        onAnswerSelected = {
                            answer ->
                            viewModel.onAnswerSelected(selectedQuestionIndex, answer)
                        }
                    )
                }

                QuestionNavigationBar(
                    questionCount = questionSet.size,
                    selectedQuestionIndex = selectedQuestionIndex,
                    onQuestionSelected = { newIndex ->
                        viewModel.onQuestionSelected(newIndex)
                    },
                    isQuestionAnswered = { index ->
                        viewModel.getSelectedAnswer(index) != null
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            if (selectedQuestionIndex > 0) {
                                viewModel.onQuestionSelected(selectedQuestionIndex - 1)
                            }
                        },
                        enabled = selectedQuestionIndex > 0
                    ) {
                        Text(stringResource(id = R.string.quiz_button_previous))
                    }
                    Button(
                        onClick = {
                            if (selectedQuestionIndex < questionSet.size - 1) {
                                viewModel.onQuestionSelected(selectedQuestionIndex + 1)
                            }
                        },
                        enabled = selectedQuestionIndex < questionSet.size - 1
                    ) {
                        Text(stringResource(id = R.string.quiz_button_next))
                    }
                }

                Button(
                    onClick = { viewModel.submitQuiz() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 8.dp) // Adjusted padding
                ) {
                    Text(stringResource(id = R.string.quiz_button_submit))
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeQuizQuestion(
    question: Question,
    selectedAnswerForThisQuestion: String?,
    onAnswerSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = question.questionText,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }

        val answers = question.listOfAnswers
        val answerRows = answers.chunked(2)
        val spacing = 8.dp

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            answerRows.forEach { rowAnswers ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = if (rowAnswers.size == 1) Arrangement.Center else Arrangement.spacedBy(
                        spacing,
                        Alignment.Start
                    )
                ) {
                    rowAnswers.forEach { answerText ->
                        val cardModifier = if (rowAnswers.size == 2) {
                            Modifier.weight(1f)
                        } else {
                            Modifier
                                .fillMaxWidth(0.5f)
                                .padding(horizontal = spacing / 2)
                        }
                        AnswerCard(
                            text = answerText,
                            isSelected = selectedAnswerForThisQuestion == answerText,
                            onClick = {
                                onAnswerSelected(answerText)
                            },
                            modifier = cardModifier
                        )
                    }
                }
            }
        }

        //if (selectedAnswerForThisQuestion != null) {
            //Text(
                //text = stringResource(id = R.string.quiz_you_selected, selectedAnswerForThisQuestion),
                //modifier = Modifier.padding(top = 16.dp),
                //style = MaterialTheme.typography.labelLarge
            //)
        //}
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswerCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.heightIn(min = 72.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(text = text, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun QuestionNavigationBar(
    questionCount: Int,
    selectedQuestionIndex: Int,
    onQuestionSelected: (Int) -> Unit,
    isQuestionAnswered: (index: Int) -> Boolean,
    modifier: Modifier = Modifier
) {
    if (questionCount == 0) return

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items(questionCount) { index ->
            val questionNumberForDisplay = index + 1
            val isQuestionSelected = selectedQuestionIndex == index

            val containerColor = when {
                isQuestionSelected -> MaterialTheme.colorScheme.primaryContainer
                isQuestionAnswered(index) -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            Card(
                onClick = { onQuestionSelected(index) },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .heightIn(min = 48.dp)
                    .width(48.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isQuestionSelected) 8.dp else 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = containerColor
                )
            ) {
                Box(
                    modifier = Modifier
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = questionNumberForDisplay.toString(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMakeQuizScreen() {
    // La logica per le preview andrebbe modificata per tenere conto del passaggio a Hilt
    MakeQuizScreen(onNavigateHome = {})
}

@Preview(showBackground = true)
@Composable
fun PreviewQuizResultsScreen() {
    QuizResultsScreen(onNavigateHome = {})
}
