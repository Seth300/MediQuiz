package com.example.mediquiz.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.mediquiz.data.local.AppDatabase
import com.example.mediquiz.data.local.MIGRATION_5_6
import com.example.mediquiz.data.local.MIGRATION_6_7
import com.example.mediquiz.data.local.dao.IncorrectAnswerLogDao
import com.example.mediquiz.data.local.dao.QuestionDao
import com.example.mediquiz.data.local.dao.StatisticsDao
import com.example.mediquiz.data.remote.QuestionApiService
import com.example.mediquiz.data.repository.QuestionRepository
import com.example.mediquiz.data.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val TAG = "AppModule_DB_Callback"
    private const val BASE_URL = "https://gist.githubusercontent.com/"

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    @Provides
    @Singleton
    fun provideDatabaseCallback(
        questionDaoProvider: Provider<QuestionDao>,
        applicationScope: CoroutineScope
    ): RoomDatabase.Callback {
        Log.d(TAG, "provideDatabaseCallback: Creating RoomDatabase.Callback instance.")
        return object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d(TAG, "onCreate: Database is being created. Pre-populating questions...")

                applicationScope.launch {
                    Log.d(TAG, "onCreate: Coroutine launched for data population.")
                    try {
                        questionDaoProvider.get()
                        Log.d(TAG, "onCreate: QuestionDao obtained.")
                        Log.d(TAG, "onCreate: Database will be populated from remote source via repository.")
                    } catch (e: Exception) {
                        Log.e(TAG, "onCreate: Error during database pre-population", e)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                Log.d(TAG, "onOpen: Database is being opened.")
            }
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callback: RoomDatabase.Callback
    ): AppDatabase {
        Log.d(TAG, "provideAppDatabase: Building database instance.")
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "quiz_database"
        )
            .addCallback(callback)
            .addMigrations(MIGRATION_5_6, MIGRATION_6_7)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideQuestionDao(appDatabase: AppDatabase): QuestionDao {
        return appDatabase.questionDao()
    }

    @Provides
    @Singleton
    fun provideStatisticsDao(appDatabase: AppDatabase): StatisticsDao {
        return appDatabase.statisticsDao()
    }

    @Provides
    @Singleton
    fun provideIncorrectAnswerLogDao(appDatabase: AppDatabase): IncorrectAnswerLogDao {
        return appDatabase.incorrectAnswerLogDao()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideQuestionApiService(retrofit: Retrofit): QuestionApiService {
        return retrofit.create(QuestionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(
        questionDao: QuestionDao,
        questionApiService: QuestionApiService
    ): QuestionRepository {
        return QuestionRepository(questionDao, questionApiService)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    // StatisticsRepository è fornito implicitamente.
}
