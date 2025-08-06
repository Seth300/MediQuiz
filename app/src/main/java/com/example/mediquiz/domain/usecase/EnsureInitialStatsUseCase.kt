package com.example.mediquiz.domain.usecase

import com.example.mediquiz.data.repository.StatisticsRepository
import javax.inject.Inject

class EnsureInitialStatsUseCase @Inject constructor(
    private val statisticsRepository: StatisticsRepository
) {
    suspend operator fun invoke(examId: String) {
        statisticsRepository.ensureInitialStatsRowExists(examId)
    }
}
