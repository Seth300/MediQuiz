package com.example.mediquiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediquiz.data.model.QuizStatistics
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {

    @Query("SELECT * FROM quiz_statistics WHERE examId = :examId")
    fun getStatisticsForExam(examId: String): Flow<QuizStatistics?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(statistics: QuizStatistics)

    @Query("SELECT * FROM quiz_statistics WHERE examId = :examId")
    suspend fun getStatisticsSnapshotForExam(examId: String): QuizStatistics?

    suspend fun ensureStatisticsForExamExist(examId: String) {
        if (getStatisticsSnapshotForExam(examId) == null) {
            insertOrUpdate(QuizStatistics(examId = examId))
        }
    }
}
