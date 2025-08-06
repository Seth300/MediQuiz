package com.example.mediquiz.data.repository

import android.util.Log
import com.example.mediquiz.data.local.dao.IncorrectAnswerLogDao
import com.example.mediquiz.data.local.dao.StatisticsDao
import com.example.mediquiz.data.model.IncorrectAnswerLog
import com.example.mediquiz.data.model.QuizStatistics
import com.example.mediquiz.data.model.SubjectStatDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "StatisticsRepository"

@Singleton
class StatisticsRepository @Inject constructor(
    private val statisticsDao: StatisticsDao,
    private val incorrectAnswerLogDao: IncorrectAnswerLogDao
) {

    private val _incorrectLogChanged = MutableSharedFlow<Unit>(replay = 0, extraBufferCapacity = 1)
    val incorrectLogChanged: SharedFlow<Unit> = _incorrectLogChanged.asSharedFlow()

    // Stabilisce Flow statistiche
    fun getStatistics(examId: String = "au2"): Flow<QuizStatistics?> {
        Log.d(TAG, "getStatistics: Accessing statistics flow from DAO.")
        return statisticsDao.getStatisticsForExam(examId )
            .onEach { stats ->
                Log.d(TAG, "getStatistics (from DAO): Emitting new statistics: ${stats != null}")
            }
    }

    // Garantisce l'esistenza delle statistiche globali nel database, se assenti le crea
    suspend fun ensureInitialStatsRowExists(examId: String = "au2") {
        Log.d(TAG, "ensureInitialStatsRowExists: Checking and potentially creating initial stats row.")
        try {
            if (statisticsDao.getStatisticsSnapshotForExam(examId) == null) {
                Log.d(TAG, "ensureInitialStatsRowExists: No stats row found, inserting default.")
                statisticsDao.insertOrUpdate(
                    QuizStatistics(
                        examId = examId,
                        totalQuizzesCompleted = 0,
                        subjectStats = emptyMap(),
                    )
                )
                Log.d(TAG, "ensureInitialStatsRowExists: Default stats row inserted.")
            } else {
                Log.d(TAG, "ensureInitialStatsRowExists: Stats row already exists.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "ensureInitialStatsRowExists: Error during operation", e)
        }
    }

    // Gestisce l'aggiornamento delle statistiche dopo il completamento del quiz
    suspend fun recordQuizCompletion(examId:String, subjectResults: Map<String, SubjectStatDetail>) {
        Log.d(TAG, "recordQuizCompletion: Recording quiz completion for ${subjectResults.size} subjects.")
        try {
            ensureInitialStatsRowExists() // Crea la riga se non esiste già
            val currentStats = statisticsDao.getStatisticsSnapshotForExam(examId) ?: QuizStatistics(examId)
            Log.d(TAG, "recordQuizCompletion: Current total quizzes: ${currentStats.totalQuizzesCompleted}")

            val newSubjectStats = currentStats.subjectStats.toMutableMap()
            subjectResults.forEach { (subject, detail) ->
                val existingDetail = newSubjectStats[subject] ?: SubjectStatDetail()
                newSubjectStats[subject] = existingDetail.copy(
                    totalQuestionsAnswered = existingDetail.totalQuestionsAnswered + detail.totalQuestionsAnswered,
                    totalCorrectAnswers = existingDetail.totalCorrectAnswers + detail.totalCorrectAnswers
                )
            }

            val updatedStats = currentStats.copy(
                totalQuizzesCompleted = currentStats.totalQuizzesCompleted + 1,
                subjectStats = newSubjectStats
            )
            statisticsDao.insertOrUpdate(updatedStats)
            Log.d(TAG, "recordQuizCompletion: Statistics updated. New total quizzes: ${updatedStats.totalQuizzesCompleted}")
        } catch (e: Exception) {
            Log.e(TAG, "recordQuizCompletion: Error recording quiz completion", e)
        }
    }

    // --- IncorrectAnswerLog  ---

    suspend fun logIncorrectAnswer(questionId: Int, userAnswer: String) {
        Log.d(TAG, "logIncorrectAnswer: Logging incorrect answer for questionId $questionId, answer: $userAnswer")
        val logEntry = IncorrectAnswerLog(
            questionId = questionId,
            userAnswer = userAnswer,
            timestamp = System.currentTimeMillis()
        )
        try {
            incorrectAnswerLogDao.insertLog(logEntry)
            Log.d(TAG, "logIncorrectAnswer: Logged incorrect answer successfully for questionId $questionId.")
            _incorrectLogChanged.tryEmit(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "logIncorrectAnswer: Error logging incorrect answer for questionId $questionId", e)
        }
    }

    fun getLogsForQuestion(questionId: Int): Flow<List<IncorrectAnswerLog>> {
        Log.d(TAG, "getLogsForQuestion: Accessing logs for questionId $questionId.")
        return incorrectAnswerLogDao.getLogsForQuestion(questionId)
            .onEach { logs ->
                Log.d(TAG, "getLogsForQuestion (from DAO): Emitting ${logs.size} logs for questionId $questionId.")
            }
    }

    fun getAllIncorrectAnswerLogs(): Flow<List<IncorrectAnswerLog>> {
        Log.d(TAG, "getAllIncorrectAnswerLogs: Accessing all incorrect answer logs.")
        return incorrectAnswerLogDao.getAllLogs()
            .onEach { logs ->
                Log.d(TAG, "getAllIncorrectAnswerLogs (from DAO): Emitting ${logs.size} total logs.")
            }
    }

    fun getDistinctIncorrectQuestionIds(): Flow<List<Int>> {
        Log.d(TAG, "getDistinctIncorrectQuestionIds: Flow is being obtained from DAO.")
        return incorrectAnswerLogDao.getDistinctIncorrectQuestionIds()
            .onEach { ids ->
                Log.d(TAG, "getDistinctIncorrectQuestionIds (from DAO): Emitting new ID list: $ids")
            }
    }

    suspend fun clearAllIncorrectAnswerLogs() {
        Log.d(TAG, "clearAllIncorrectAnswerLogs: Clearing all incorrect answer logs.")
        try {
            incorrectAnswerLogDao.clearAllLogs()
            Log.d(TAG, "clearAllIncorrectAnswerLogs: All logs cleared successfully.")
            _incorrectLogChanged.tryEmit(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "clearAllIncorrectAnswerLogs: Error clearing all logs", e)
        }
    }

    suspend fun clearLogsForQuestion(questionId: Int) {
        Log.d(TAG, "clearLogsForQuestion: Clearing logs for questionId $questionId.")
        try {
            incorrectAnswerLogDao.clearLogsForQuestion(questionId)
            Log.d(TAG, "clearLogsForQuestion: Logs cleared successfully for questionId $questionId.")
            _incorrectLogChanged.tryEmit(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "clearLogsForQuestion: Error clearing logs for questionId $questionId", e)
        }
    }

    // Al momento non utilizzati, possibile aggiungerli alle statistiche
    fun getTotalCorrectAnswers(stats: QuizStatistics?): Int {
        return stats?.subjectStats?.values?.sumOf { it.totalCorrectAnswers } ?: 0
    }

    fun getTotalQuestionsAnswered(stats: QuizStatistics?): Int {
        return stats?.subjectStats?.values?.sumOf { it.totalQuestionsAnswered } ?: 0
    }
}
