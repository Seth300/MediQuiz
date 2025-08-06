package com.example.mediquiz.ui.review

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mediquiz.R
//import androidx.hilt.navigation.compose.hiltViewModel // passato implicitamente da ReviewViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewIncorrectListScreen(
    reviewViewModel: ReviewViewModel,
    onNavigateToDetail: (questionId: Int) -> Unit,
    onStartReviewQuiz: (questionIds: List<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val incorrectlyAnsweredQuestions by reviewViewModel.incorrectlyAnsweredQuestions.collectAsState()
    var showClearAllDialog by remember { mutableStateOf(false) }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text(stringResource(id = R.string.review_confirm_clear_title)) },
            text = { Text(stringResource(id = R.string.review_confirm_clear_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        reviewViewModel.clearAllHistory()
                        showClearAllDialog = false
                    }
                ) {
                    Text(stringResource(id = R.string.review_clear_all_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text(stringResource(id = R.string.cancel_button))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.review_incorrect_answers_title)) },
                 modifier = Modifier,//.statusBarsPadding(),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    if (incorrectlyAnsweredQuestions.isNotEmpty()) {
                        IconButton(onClick = {
                            showClearAllDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.DeleteSweep,
                                contentDescription = stringResource(id = R.string.review_clear_all_history_icon_description)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (incorrectlyAnsweredQuestions.isNotEmpty()) {
                FloatingActionButton(onClick = {
                    reviewViewModel.prepareReviewQuiz { ids ->
                        onStartReviewQuiz(ids)
                    }
                }) {
                    Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(id = R.string.review_start_quiz_fab_description))
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        if (incorrectlyAnsweredQuestions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(id = R.string.review_no_questions_message),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp)
            ) {
                items(incorrectlyAnsweredQuestions, key = { it.question.id }) { reviewableQuestion ->
                    ReviewableQuestionItem(
                        reviewableQuestion = reviewableQuestion,
                        onClick = { onNavigateToDetail(reviewableQuestion.question.id) }
                    )
                }
            }
        }
    }
}
