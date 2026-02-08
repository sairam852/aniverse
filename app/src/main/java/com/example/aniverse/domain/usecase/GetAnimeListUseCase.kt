package com.example.aniverse.domain.usecase

import androidx.paging.PagingData
import com.example.aniverse.domain.model.Anime
import com.example.aniverse.domain.repository.AnimeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving the top anime list as a paging stream.
 *
 * This encapsulates the business logic for fetching anime data,
 * delegating to the repository which handles offline-first caching.
 */
class GetAnimeListUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    /**
     * Executes the use case and returns a Flow of PagingData.
     *
     * @return Flow of PagingData containing Anime domain models
     */
    operator fun invoke(): Flow<PagingData<Anime>> {
        return repository.getTopAnimePagingStream()
    }
}
