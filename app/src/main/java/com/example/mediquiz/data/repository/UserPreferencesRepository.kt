package com.example.mediquiz.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val context: Context
) {

    private object PreferencesKeys {
        val SELECTED_EXAM_ID = stringPreferencesKey("selected_exam_id")
        val SELECTED_COUNT = intPreferencesKey("selected_count")
    }

    val selectedExamIdFlow: Flow<String?> = context.dataStore.data
        .map {
            preferences ->
            preferences[PreferencesKeys.SELECTED_EXAM_ID]
        }
    val selectedCountFlow: Flow<Int?> = context.dataStore.data
        .map {
                preferences ->
            preferences[PreferencesKeys.SELECTED_COUNT]
        }
    suspend fun updateSelectedExamId(examId: String) {
        context.dataStore.edit {
            preferences ->
            preferences[PreferencesKeys.SELECTED_EXAM_ID] = examId
        }
    }
    suspend fun updateSelectedCount(count: Int) {
        context.dataStore.edit {
                preferences ->
            preferences[PreferencesKeys.SELECTED_COUNT] = count
        }
    }
    suspend fun clearSelectedExamId() {
        context.dataStore.edit {
            preferences ->
            preferences.remove(PreferencesKeys.SELECTED_EXAM_ID)
        }
    }
}
