package com.example.mediquiz.data.repository

import android.util.Log
import com.example.mediquiz.data.local.dao.QuestionDao
import com.example.mediquiz.data.model.Question
import com.example.mediquiz.data.model.QuestionSubject
import com.example.mediquiz.data.remote.QuestionApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class QuestionRepository(
    private val questionDao: QuestionDao,
    private val questionApiService: QuestionApiService
) {

    private val TAG = "QuestionRepository"

    fun getAllQuestionsStream(): Flow<List<Question>> {
        Log.d(TAG, "getAllQuestionsStream called")
        return questionDao.getAllQuestions()
    }

    suspend fun getRandomQuestions(
        count: Int,
        examId: String,
        subjects: Set<QuestionSubject> = emptySet()
    ): List<Question> {
        Log.d(
            TAG,
            "getRandomQuestions called with examId: $examId, count: $count, subjects: ${subjects.joinToString { it.name }}"
        )
        try {
            val questions: List<Question>
            if (subjects.isEmpty()) {
                Log.d(
                    TAG,
                    "getRandomQuestions: No subject filters applied for exam $examId. Fetching from all subjects for this exam."
                )
                questions = questionDao.getRandomQuestionsForExam(examId, count)
            } else {
                val subjectNames = subjects.map { it.name }.toSet()
                Log.d(TAG, "getRandomQuestions: Applying subject filters for exam $examId: $subjectNames")
                questions = questionDao.getRandomQuestionsForExamFilteredBySubject(examId, count, subjectNames)
            }
            Log.d(TAG, "getRandomQuestions DAO returned ${questions.size} questions for exam $examId.")
            return questions
        } catch (e: Exception) {
            Log.e(TAG, "getRandomQuestions error for exam $examId: ", e)
            return emptyList()
        }
    }

    suspend fun refreshQuestionsFromRemoteSource() {
        withContext(Dispatchers.IO) { // Utilizza IO dispatcher per le operazioni di rete
            try {
                Log.d(TAG, "refreshQuestionsFromRemoteSource: Fetching remote questions...")
                val remoteQuestionsDto = questionApiService.getRemoteQuestions()
                Log.d(TAG, "refreshQuestionsFromRemoteSource: Fetched ${remoteQuestionsDto.size} questions from remote.")

                val questionsToInsert = remoteQuestionsDto.map { dto ->
                    Question(
                        id = dto.id, // Todo:la primary key è gestita dal server, non dal client, possibile errore di sincronizzazione
                        examId = dto.examId,
                        questionText = dto.questionText,
                        listOfAnswers = dto.questionAnswers,
                        correctAnswer = dto.correctAnswer,
                        subject = try {
                            QuestionSubject.valueOf(dto.subject.uppercase())
                        } catch (e: IllegalArgumentException) {
                            Log.w(TAG, "Unknown subject from server: ${dto.subject}, defaulting to Not Assigned")
                            QuestionSubject.NOT_ASSIGNED
                        }
                    )
                }

                Log.d(TAG, "refreshQuestionsFromRemoteSource: Clearing all local questions.")
                questionDao.clearAllQuestions()
                Log.d(TAG, "refreshQuestionsFromRemoteSource: Inserting ${questionsToInsert.size} new questions.")
                questionDao.insertAll(questionsToInsert)
                Log.d(TAG, "refreshQuestionsFromRemoteSource: Sync complete.")

            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing questions from remote source", e)
                // Todo: mostra testo di errore tramite l'UI
            }
        }
    }

    suspend fun insertQuestions(questions: List<Question>) {
        Log.d(TAG, "insertQuestions called with ${questions.size} questions.")
        questionDao.insertAll(questions)
    }

    suspend fun clearAllQuestions() {
        Log.d(TAG, "clearAllQuestions called. This will clear the entire questions table.")
        questionDao.clearAllQuestions() // Presente per debug
    }

    fun getQuestionById(id: Int): Flow<Question?> {
        return questionDao.getQuestionById(id).distinctUntilChanged().flowOn(Dispatchers.IO)
    }

    fun getQuestionsByIds(ids: List<Int>): Flow<List<Question>> {
        return questionDao.getQuestionsByIds(ids).distinctUntilChanged().flowOn(Dispatchers.IO)
    }

    fun getQuestionsByIdsAndExam(examId: String,ids: List<Int>): Flow<List<Question>> {
        return questionDao.getQuestionsByIdsAndExam(examId,ids).distinctUntilChanged().flowOn(Dispatchers.IO)
    }

    fun getQuestionsByIdsAndExamAndCount(examId: String,ids: List<Int>,count: Int): Flow<List<Question>> {
        return questionDao.getQuestionsByIdsAndExamAndCount(examId,ids,count).distinctUntilChanged().flowOn(Dispatchers.IO)
    }
    suspend fun getAllQuestionsList(): List<Question> {
        return withContext(Dispatchers.IO) {
            questionDao.getAllQuestions().firstOrNull() ?: emptyList()
        }
    }
}
