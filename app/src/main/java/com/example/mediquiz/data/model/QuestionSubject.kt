package com.example.mediquiz.data.model

import androidx.annotation.StringRes
import com.example.mediquiz.R

//enum class rappresentante i possibili argomenti delle domande, utilizzati per filtri e statistiche
enum class QuestionSubject(@StringRes val subjectDisplayNameResId: Int) {
    GENERAL_KNOWLEDGE(subjectDisplayNameResId= R.string.subject_general_knowledge),
    SCIENCE(subjectDisplayNameResId= R.string.subject_science),
    HISTORY(subjectDisplayNameResId= R.string.subject_history),
    GEOGRAPHY(subjectDisplayNameResId= R.string.subject_geography),
    ARTS(subjectDisplayNameResId= R.string.subject_arts),
    MATHEMATICS(subjectDisplayNameResId= R.string.subject_mathematics),
    ANATOMY(subjectDisplayNameResId= R.string.subject_anatomy),
    HISTOLOGY(subjectDisplayNameResId= R.string.subject_histology),
    EMBRYOLOGY(subjectDisplayNameResId= R.string.subject_embriology),
    IMMUNOLOGY(subjectDisplayNameResId= R.string.subject_immunology),
    MICROBIOLOGY(subjectDisplayNameResId= R.string.subject_microbiology),
    VIROLOGY(subjectDisplayNameResId= R.string.subject_virology),
    BIOPHYSICS(subjectDisplayNameResId= R.string.subject_biophysics),
    PHYSIOLOGY(subjectDisplayNameResId= R.string.subject_physiology)
}