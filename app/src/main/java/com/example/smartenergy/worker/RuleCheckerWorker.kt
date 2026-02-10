package com.example.smartenergy.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.smartenergy.domain.usecase.EvaluateRulesUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RuleCheckerWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val evaluateRulesUseCase: EvaluateRulesUseCase
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        evaluateRulesUseCase()
        return Result.success()
    }
}
