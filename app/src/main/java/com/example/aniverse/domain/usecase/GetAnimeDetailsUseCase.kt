package com.example.aniverse.domain.usecase

import com.example.aniverse.domain.model.AnimeDetails
import com.example.aniverse.domain.repository.AnimeRepository
import com.example.aniverse.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving detailed information about a specific anime.
 *
 * This encapsulates the business logic for fetching anime details,
 * delegating to the repository which handles offline-first caching.
 */
class GetAnimeDetailsUseCase @Inject constructor(
    private val repository: AnimeRepository
) {
    /**
     * Executes the use case and returns a Flow of Resource containing anime details.
     *
     * @param id The MAL ID of the anime to fetch
     * @return Flow of Resource containing AnimeDetails or error state
     */
    operator fun invoke(id: Int): Flow<Resource<AnimeDetails>> {
        return repository.getAnimeDetails(id)
    }
}
