package com.example.mediquiz

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Al momento utilizzata solo per la DI tramite Hilt

@HiltAndroidApp
class MediQuizApplication : Application()