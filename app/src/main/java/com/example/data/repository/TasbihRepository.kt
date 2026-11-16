package com.example.data.repository

import com.example.data.local.TasbihHistoryDao
import com.example.data.local.TasbihHistoryEntity
import com.example.domain.PrayerTimeCalculator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TasbihRepository(private val dao: TasbihHistoryDao) {

    fun observeHistory(): Flow<List<TasbihHistoryEntity>> = dao.observeRecentHistory()

    fun observeTodayCount(): Flow<Int> {
        val todayIso = PrayerTimeCalculator.dateIsoFromMillis(System.currentTimeMillis())
        return dao.observeByDate(todayIso).map { it?.totalCount ?: 0 }
    }

    suspend fun recordIncrement(completedRound: Boolean) {
        val todayIso = PrayerTimeCalculator.dateIsoFromMillis(System.currentTimeMillis())
        val existing = dao.getByDate(todayIso)
        val newTotal = (existing?.totalCount ?: 0) + 1
        val newRounds = (existing?.completedRounds ?: 0) + if (completedRound) 1 else 0
        dao.upsert(
            TasbihHistoryEntity(
                dateIso = todayIso,
                totalCount = newTotal,
                completedRounds = newRounds,
                lastUpdatedMillis = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun recordUndo(undidCompletedRound: Boolean) {
        val todayIso = PrayerTimeCalculator.dateIsoFromMillis(System.currentTimeMillis())
        val existing = dao.getByDate(todayIso) ?: return
        val newTotal = (existing.totalCount - 1).coerceAtLeast(0)
        val newRounds = (existing.completedRounds - if (undidCompletedRound) 1 else 0).coerceAtLeast(0)
        dao.upsert(
            existing.copy(
                totalCount = newTotal,
                completedRounds = newRounds,
                lastUpdatedMillis = System.currentTimeMillis(),
            ),
        )
    }
}
