package com.example.mediquiz.data.model

import androidx.annotation.StringRes
import com.example.mediquiz.R

//enum class rappresentante i possibili argomenti delle domande, utilizzati per filtri e statistiche
enum class QuestionSubject(@StringRes val subjectDisplayNameResId: Int) {
    CAVO_ORALE(subjectDisplayNameResId= R.string.subject_oral_cavity),
    FARINGE(subjectDisplayNameResId= R.string.subject_faringe),
    PAROTIDE(subjectDisplayNameResId= R.string.subject_parotide),
    TIROIDE(subjectDisplayNameResId= R.string.subject_tiroide),
    ESOFAGO(subjectDisplayNameResId= R.string.subject_esofago),
    STOMACO(subjectDisplayNameResId= R.string.subject_stomaco),
    INTESTINO_TENUE(subjectDisplayNameResId= R.string.subject_intestino_tenue),
    INTESTINO_CRASSO(subjectDisplayNameResId= R.string.subject_intestino_crasso),
    FEGATO(subjectDisplayNameResId= R.string.subject_fegato),
    PANCREAS(subjectDisplayNameResId= R.string.subject_pancreas),
    RENI(subjectDisplayNameResId= R.string.subject_reni),
    SURRENE(subjectDisplayNameResId= R.string.subject_surrene),
    VESCICA(subjectDisplayNameResId= R.string.subject_vescica),
    APP_GEN_MASC(subjectDisplayNameResId= R.string.subject_app_gen_masc),
    APP_GEN_FEMM(subjectDisplayNameResId= R.string.subject_app_gen_femm),
    SNP(subjectDisplayNameResId= R.string.subject_snp),
    SNC(subjectDisplayNameResId= R.string.subject_snc),
    MIDOLLO_SPINALE(subjectDisplayNameResId= R.string.subject_midollo_spinale),
    BULBO_SPINALE(subjectDisplayNameResId= R.string.subject_bulbo_spinale),
    CERVELLETTO(subjectDisplayNameResId= R.string.subject_cervelletto),
    MESENCEFALO(subjectDisplayNameResId= R.string.subject_mesencefalo),
    TRONCO_ENCEFALICO(subjectDisplayNameResId= R.string.subject_tronco_encefalico),
    TALAMO(subjectDisplayNameResId= R.string.subject_talamo),
    TELENCEFALO(subjectDisplayNameResId= R.string.subject_telencefalo),
    IPOFISI(subjectDisplayNameResId= R.string.subject_ipofisi),
    VISTA(subjectDisplayNameResId= R.string.subject_vista),
    UDITO(subjectDisplayNameResId= R.string.subject_udito),
    NERVI_CRANICI(subjectDisplayNameResId= R.string.subject_nervi_cranici),
    IMMUNOLOGY(subjectDisplayNameResId= R.string.subject_immunology),
    MICROBIOLOGY(subjectDisplayNameResId= R.string.subject_microbiology),
    VIROLOGY(subjectDisplayNameResId= R.string.subject_virology),
    BIOPHYSICS(subjectDisplayNameResId= R.string.subject_biophysics),
    PHYSIOLOGY(subjectDisplayNameResId= R.string.subject_physiology),
    NOT_ASSIGNED(subjectDisplayNameResId= R.string.subject_not_assigned)
}