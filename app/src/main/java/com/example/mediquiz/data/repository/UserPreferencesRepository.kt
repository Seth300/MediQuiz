package com.example.mediquiz.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey // Added
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
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
        val SELECTED_SUBJECT_FILTERS = stringSetPreferencesKey("selected_subject_filters") // Added
    }

    val selectedExamIdFlow: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("UserPreferencesRepo", "Error reading exam ID.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_EXAM_ID]
        }

    val selectedCountFlow: Flow<Int?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("UserPreferencesRepo", "Error reading selected count.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_COUNT]
        }

    val selectedSubjectFiltersFlow: Flow<Set<String>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e("UserPreferencesRepo", "Error reading subject filters.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_SUBJECT_FILTERS] ?: emptySet()
        }

    suspend fun updateSelectedExamId(examId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_EXAM_ID] = examId
        }
    }

    suspend fun updateSelectedCount(count: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_COUNT] = count
        }
    }

    suspend fun updateSelectedSubjectFilters(subjectNames: Set<String>) {
        context.dataStore.edit { preferences ->
            if (subjectNames.isEmpty()) {
                preferences.remove(PreferencesKeys.SELECTED_SUBJECT_FILTERS)
            } else {
                preferences[PreferencesKeys.SELECTED_SUBJECT_FILTERS] = subjectNames
            }
        }
    }

    suspend fun clearSelectedExamId() {
        context.dataStore.edit { preferences ->
            preferences.remove(PreferencesKeys.SELECTED_EXAM_ID)
        }
    }
}
