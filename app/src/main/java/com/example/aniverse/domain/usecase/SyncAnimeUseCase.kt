package com.example.aniverse.domain.usecase

import com.example.aniverse.domain.repository.AnimeRepository
import javax.inject.Inject

/**
 * Use case for triggering an explicit sync of the top anime list.
 *
 * This encapsulates the business logic for refreshing anime data,
 * useful for pull-to-refresh scenarios or manual sync triggers.
 */
class SyncAnimeUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    /**
     * Executes the use case and triggers a sync operation.
     *
     * @return Result indicating success or failure of the sync operation
     */
    suspend operator fun invoke(): Result<Unit> {
        return repository.syncTopAnime()
    }
}
