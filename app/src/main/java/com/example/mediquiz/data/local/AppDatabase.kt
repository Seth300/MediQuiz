package com.example.mediquiz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mediquiz.data.local.converters.Converters
import com.example.mediquiz.data.local.converters.QuizStatisticsConverters
import com.example.mediquiz.data.local.dao.IncorrectAnswerLogDao
import com.example.mediquiz.data.local.dao.QuestionDao
import com.example.mediquiz.data.local.dao.StatisticsDao
import com.example.mediquiz.data.model.IncorrectAnswerLog
import com.example.mediquiz.data.model.Question
import com.example.mediquiz.data.model.QuizStatistics

// Migration da 5 a 6 (questions table)
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE questions ADD COLUMN examId TEXT NOT NULL DEFAULT 'au2'")
    }
}

// Migration da 6 a 7 ( quiz_statistics table)
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS quiz_statistics")
        db.execSQL("CREATE TABLE quiz_statistics (examId TEXT NOT NULL PRIMARY KEY, totalQuizzesCompleted INTEGER NOT NULL DEFAULT 0, subjectStats TEXT NOT NULL DEFAULT '{}')")
    }
}

@Database(entities = [Question::class, QuizStatistics::class, IncorrectAnswerLog::class], version = 7, exportSchema = false)
@TypeConverters(Converters::class, QuizStatisticsConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun statisticsDao(): StatisticsDao
    abstract fun incorrectAnswerLogDao(): IncorrectAnswerLogDao

    companion object {
    }
}