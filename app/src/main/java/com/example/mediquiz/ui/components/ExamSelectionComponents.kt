package com.example.mediquiz.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.mediquiz.data.model.Exam

@Composable
fun ExamSelectorDropdown(
    selectedExam: Exam,
    onExamSelected: (Exam) -> Unit,
    modifier: Modifier = Modifier
) {
    LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val allExams = remember { Exam.entries.toList() }

    Box(modifier = modifier) {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(id = selectedExam.displayNameResId))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            allExams.forEach { exam ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = exam.displayNameResId)) },
                    onClick = {
                        onExamSelected(exam)
                        expanded = false
                    }
                )
            }
        }
    }
}
