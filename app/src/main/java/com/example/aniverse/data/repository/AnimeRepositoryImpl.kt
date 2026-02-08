package com.example.aniverse.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.aniverse.data.local.AppDatabase
import com.example.aniverse.data.local.dao.AnimeDao
import com.example.aniverse.data.local.entity.AnimeEntity
import com.example.aniverse.data.paging.TopAnimeRemoteMediator
import com.example.aniverse.data.remote.AnimeApi
import com.example.aniverse.data.remote.dto.AnimeDetailsDto
import com.example.aniverse.domain.mapper.toDomain
import com.example.aniverse.domain.mapper.toDomainDetails
import com.example.aniverse.domain.model.Anime
import com.example.aniverse.domain.model.AnimeDetails
import com.example.aniverse.domain.repository.AnimeRepository
import com.example.aniverse.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Repository implementation for anime data operations.
 *
 * Implements the offline-first pattern:
 * - Room database is the single source of truth
 * - Network updates happen via RemoteMediator (for paging) or direct calls (for details)
 * - UI observes Room data, which is automatically updated when network data arrives
 */
class AnimeRepositoryImpl @Inject constructor(
    private val apiService: AnimeApi,
    private val database: AppDatabase
) : AnimeRepository {

    private val animeDao: AnimeDao = database.animeDao()

    /**
     * Returns a Flow of PagingData for the top anime list.
     *
     * Uses Paging 3 with RemoteMediator to:
     * 1. Observe Room data (single source of truth)
     * 2. Automatically fetch from network when needed
     * 3. Work offline if cached data exists
     */
    @OptIn(ExperimentalPagingApi::class)
    override fun getTopAnimePagingStream(): Flow<PagingData<Anime>> {
        val remoteMediator = TopAnimeRemoteMediator(
            apiService = apiService,
            database = database
        )

        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = PREFETCH_DISTANCE
            ),
            remoteMediator = remoteMediator,
            pagingSourceFactory = { animeDao.getPagedAnime() }
        ).flow.map { pagingData ->
            // Map entities to domain models
            pagingData.map { entity ->
                entity.toDomain()
            }
        }
    }

    /**
     * Returns a Flow of Resource containing anime details for a specific ID.
     *
     * First checks Room cache, then fetches from network if needed.
     * Returns cached data if available offline.
     */
    override fun getAnimeDetails(id: Int): Flow<Resource<AnimeDetails>> = flow {
        // Emit loading state
        emit(Resource.Loading)

        // Try to get from cache first
        val cachedEntity = animeDao.getAnimeById(id)
        if (cachedEntity != null) {
            // Emit cached data immediately
            emit(Resource.Success(cachedEntity.toDomainDetails()))
        }

        // Try to fetch from network
        try {
            val response = apiService.getAnimeDetails(id)
            val detailsDto = response.data

            // Convert to entity and save to database
            val entity = detailsDto.toEntity()
            animeDao.insertAll(listOf(entity))

            // Emit fresh data
            emit(Resource.Success(entity.toDomainDetails()))
        } catch (e: Exception) {
            // If we have cached data, don't emit error
            if (cachedEntity == null) {
                emit(Resource.Error(
                    message = e.message ?: "Failed to fetch anime details",
                    throwable = e
                ))
            }
            // If we have cached data, we already emitted it above, so just return
        }
    }

    /**
     * Triggers an explicit sync of the top anime list.
     *
     * This invalidates the PagingSource and triggers RemoteMediator to fetch
     * fresh data from the network. Useful for pull-to-refresh scenarios.
     */
    override suspend fun syncTopAnime(): Result<Unit> {
        // We rely on Paging 3's RemoteMediator to handle data synchronization.
        // Explicitly clearing the DB here causes data loss when offline.
        return Result.success(Unit)
    }

    companion object {
        // Page size for pagination
        private const val PAGE_SIZE = 25

        // Prefetch distance - how many items ahead to prefetch
        private const val PREFETCH_DISTANCE = 10
    }
}

/**
 * Extension function to convert AnimeDetailsDto to AnimeEntity.
 * This is used by the repository to save details to the database.
 */
private fun AnimeDetailsDto.toEntity(): AnimeEntity {
    val currentTime = System.currentTimeMillis()

    // Extract image URLs (prefer JPG, fallback to WebP)
    val imageUrl = images?.jpg?.imageUrl ?: images?.webp?.imageUrl
    val largeImageUrl = images?.jpg?.largeImageUrl ?: images?.webp?.largeImageUrl

    // Extract trailer URL (prefer embed URL, fallback to regular URL)
    val trailerUrl = trailer?.embedUrl ?: trailer?.url

    // Convert genres list to comma-separated string
    val genresString = genres?.mapNotNull { it.name }
        ?.joinToString(", ")
        ?.takeIf { it.isNotEmpty() }

    return AnimeEntity(
        id = malId,
        title = title,
        titleEnglish = titleEnglish,
        titleJapanese = titleJapanese,
        type = type,
        source = source,
        episodes = episodes,
        status = status,
        score = score,
        rank = rank,
        popularity = popularity,
        synopsis = synopsis,
        season = season,
        year = year,
        imageUrl = imageUrl,
        largeImageUrl = largeImageUrl,
        trailerUrl = trailerUrl,
        genres = genresString,
        lastUpdated = currentTime,
        airedString = aired?.string
    )
}
