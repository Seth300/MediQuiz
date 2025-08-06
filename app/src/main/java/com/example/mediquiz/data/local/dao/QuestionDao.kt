package com.example.mediquiz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mediquiz.data.model.Question
import com.example.mediquiz.data.model.QuestionSubject // Assuming QuestionSubject is still relevant
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Query("SELECT * FROM questions")
    fun getAllQuestions(): Flow<List<Question>>

    // Preleva una quantità specifica di domande casuali senza applicare filtri
    @Query("SELECT * FROM questions ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomQuestions(count: Int): List<Question>

    // Preleva una quantità specifica di domande casuali, applicando i filtri
    @Query("SELECT * FROM questions WHERE subject IN (:subjects) ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomQuestionsFilteredBySubject(count: Int, subjects: Set<String>): List<Question>

    @Query("DELETE FROM questions")
    suspend fun clearAllQuestions()

    // Seleziona una domanda tramite l'ID
    @Query("SELECT * FROM questions WHERE id = :id")
    fun getQuestionById(id: Int): Flow<Question?>

    // Selezione multipla tramite id
    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    fun getQuestionsByIds(ids: List<Int>): Flow<List<Question>>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int
    //I metodi successivi ripetono i precedenti filtrando in base all'esame
    @Query("SELECT * FROM questions WHERE examId = :examId")
    fun getQuestionsForExam(examId: String): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE examId = :examId AND subject = :subject")
    fun getQuestionsForExamAndSubject(examId: String, subject: QuestionSubject): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE examId = :examId AND id IN (:ids)")
    fun getQuestionsByIdsAndExam(examId: String, ids: List<Int>): Flow<List<Question>>
    @Query("SELECT * FROM questions WHERE examId = :examId AND id IN (:ids) LIMIT :count")
    fun getQuestionsByIdsAndExamAndCount(examId: String, ids: List<Int>,count: Int): Flow<List<Question>>

    @Query("SELECT DISTINCT subject FROM questions WHERE examId = :examId")
    fun getSubjectsForExam(examId: String): Flow<List<QuestionSubject>>

    @Query("SELECT * FROM questions WHERE examId = :examId AND subject IN (:selectedSubjects)")
    fun getQuestionsForExamWithSelectedSubjects(examId: String, selectedSubjects: List<QuestionSubject>): Flow<List<Question>>

    @Query("SELECT * FROM questions WHERE examId = :examId ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomQuestionsForExam(examId: String, count: Int): List<Question>

    @Query("SELECT * FROM questions WHERE examId = :examId AND subject IN (:subjects) ORDER BY RANDOM() LIMIT :count")
    suspend fun getRandomQuestionsForExamFilteredBySubject(examId: String, count: Int, subjects: Set<String>): List<Question>

    @Query("DELETE FROM questions WHERE examId = :examId")
    suspend fun deleteQuestionsForExam(examId: String)
}
