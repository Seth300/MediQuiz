package com.example.mediquiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediquiz.data.model.IncorrectAnswerLog
import kotlinx.coroutines.flow.Flow

@Dao
interface IncorrectAnswerLogDao {

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertLog(log: IncorrectAnswerLog): Long

    @Query("SELECT * FROM incorrect_answer_log WHERE questionId = :questionId ORDER BY timestamp DESC")
    fun getLogsForQuestion(questionId: Int): Flow<List<IncorrectAnswerLog>>

    @Query("SELECT * FROM incorrect_answer_log ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<IncorrectAnswerLog>>

    @Query("SELECT DISTINCT questionId FROM incorrect_answer_log ORDER BY questionId ASC")
    fun getDistinctIncorrectQuestionIds(): Flow<List<Int>>

    @Query("DELETE FROM incorrect_answer_log")
    suspend fun clearAllLogs()

    @Query("DELETE FROM incorrect_answer_log WHERE questionId = :questionId")
    suspend fun clearLogsForQuestion(questionId: Int)
}