package com.example.aniverse.domain.repository

import androidx.paging.PagingData
import com.example.aniverse.domain.model.Anime
import com.example.aniverse.domain.model.AnimeDetails
import com.example.aniverse.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for anime data operations.
 *
 * This defines the contract for accessing anime data in an offline-first manner.
 * The implementation will use Room as the single source of truth, with network
 * updates happening via RemoteMediator and repository methods.
 */
interface AnimeRepository {

    /**
     * Returns a Flow of PagingData for the top anime list.
     *
     * This observes Room data, which is updated by RemoteMediator when network
     * is available. Works offline if cached data exists.
     *
     * @return Flow of PagingData containing Anime domain models
     */
    fun getTopAnimePagingStream(): Flow<PagingData<Anime>>

    /**
     * Returns a Flow of Resource containing anime details for a specific ID.
     *
     * This observes Room data first, then attempts to fetch from network
     * if needed. Returns cached data if available offline.
     *
     * @param id The MAL ID of the anime
     * @return Flow of Resource containing AnimeDetails or error state
     */
    fun getAnimeDetails(id: Int): Flow<Resource<AnimeDetails>>

    /**
     * Triggers an explicit sync of the top anime list.
     *
     * This invalidates the PagingSource and triggers RemoteMediator to fetch
     * fresh data from the network. Useful for pull-to-refresh scenarios.
     *
     * @return Result indicating success or failure
     */
    suspend fun syncTopAnime(): Result<Unit>
}
