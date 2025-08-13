package com.example.mediquiz.data.model

import androidx.annotation.StringRes
import com.example.mediquiz.R

/**
 * Esami selezionabili dall'utente.
 *
 * @property id identificatore unico per l'esame.
 * @property displayNameResId ResId per il nome dell'esame da utilizzare nella UI.
 * @property subjects Gli argomenti definiti in QuestionSubject da utilizzare per l'esame.
 */
enum class Exam(
    val id: String,
    @StringRes val displayNameResId: Int,
    val subjects: List<QuestionSubject>
) {
    ANATOMIA_UMANA_DUE(
        id = "au2",
        displayNameResId = R.string.exam_name_anatomia_umana_due,
        subjects = listOf(
            QuestionSubject.CAVO_ORALE,
            QuestionSubject.FARINGE,
            QuestionSubject.PAROTIDE,
            QuestionSubject.TIROIDE,
            QuestionSubject.ESOFAGO,
            QuestionSubject.STOMACO,
            QuestionSubject.INTESTINO_TENUE,
            QuestionSubject.INTESTINO_CRASSO,
            QuestionSubject.FEGATO,
            QuestionSubject.PANCREAS,
            QuestionSubject.RENI,
            QuestionSubject.SURRENE,
            QuestionSubject.VESCICA,
            QuestionSubject.APP_GEN_MASC,
            QuestionSubject.APP_GEN_FEMM,
            QuestionSubject.SNP,
            QuestionSubject.SNC,
            QuestionSubject.MIDOLLO_SPINALE,
            QuestionSubject.BULBO_SPINALE,
            QuestionSubject.CERVELLETTO,
            QuestionSubject.MESENCEFALO,
            QuestionSubject.TRONCO_ENCEFALICO,
            QuestionSubject.TALAMO,
            QuestionSubject.TELENCEFALO,
            QuestionSubject.IPOFISI,
            QuestionSubject.VISTA,
            QuestionSubject.UDITO,
            QuestionSubject.NERVI_CRANICI,)
    ),
    MICROBIOLOGIA_E_IMMUNOLOGIA(
        id = "mei",
        displayNameResId = R.string.exam_name_microbiologia_e_immunologia,
        subjects = listOf(QuestionSubject.MICROBIOLOGY, QuestionSubject.IMMUNOLOGY, QuestionSubject.VIROLOGY) // <<< EXAMPLE
    ),
    FISIOLOGIA_UMANA_DUE(
        id = "fu2",
        displayNameResId = R.string.exam_name_fisiologia_umana_due,
        subjects = listOf(QuestionSubject.PHYSIOLOGY, QuestionSubject.BIOPHYSICS) // <<< EXAMPLE
    );

    companion object {
        /**
         * Identifica un esame tramite il suo [id].
         * Se non ci sono corrispondenze, restituisce null.
         */
        fun fromId(id: String?): Exam? {
            return entries.find { it.id == id }
        }
    }
}