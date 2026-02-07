package com.example.aniverse.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.aniverse.data.local.AppDatabase
import com.example.aniverse.data.local.dao.AnimeDao
import com.example.aniverse.data.local.dao.RemoteKeysDao
import com.example.aniverse.data.local.entity.AnimeEntity
import com.example.aniverse.data.local.entity.RemoteKeys
import com.example.aniverse.data.remote.AnimeApi
import com.example.aniverse.data.remote.dto.AnimeDto
import retrofit2.HttpException
import java.io.IOException

/**
 * RemoteMediator for the top anime list.
 *
 * Coordinates fetching pages from the Jikan API and storing them in Room.
 * Handles pagination keys to determine which page to load next.
 *
 * This follows the offline-first pattern: Room is the single source of truth,
 * and this mediator updates Room when network data is available.
 */
@OptIn(ExperimentalPagingApi::class)
class TopAnimeRemoteMediator(
    private val apiService: AnimeApi,
    private val database: AppDatabase
) : RemoteMediator<Int, AnimeEntity>() {

    private val animeDao: AnimeDao = database.animeDao()
    private val remoteKeysDao: RemoteKeysDao = database.remoteKeysDao()

    override suspend fun initialize(): InitializeAction {
        // If sync is disabled, don't refresh
        if (!com.example.aniverse.config.AppConfig.enableSync) {
            return InitializeAction.SKIP_INITIAL_REFRESH
        }

        // Check if we have cached data by checking if any anime exist in the database
        val hasCachedData = try {
            animeDao.getAnyAnime() != null
        } catch (e: Exception) {
            // If there's an error, assume no cached data and refresh
            false
        }
        
        return if (hasCachedData) {
            // We have cached data, skip initial refresh
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // No cached data, refresh immediately
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, AnimeEntity>
    ): MediatorResult {
        // If sync is disabled, don't load from network
        if (!com.example.aniverse.config.AppConfig.enableSync) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        return try {
            // Determine which page to load
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    // On refresh, start from page 1
                    1
                }
                LoadType.PREPEND -> {
                    // Prepend is not supported for top anime (we only go forward)
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    // Get the last item's remote key to determine next page
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey
                    if (nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    nextKey
                }
            }

            // Fetch from API
            val response = apiService.getTopAnime(page = page)
            val animeList = response.data
            val pagination = response.pagination

            // Check if we've reached the end
            val endOfPaginationReached = pagination?.hasNextPage == false ||
                    (pagination?.lastVisiblePage != null && page >= pagination.lastVisiblePage)

            // Save to database in a transaction
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    // Clear existing data on refresh
                    animeDao.clearAll()
                    remoteKeysDao.clearRemoteKeys()
                }

                // Convert DTOs to entities
                val entities = animeList.map { it.toEntity() }
                val currentTime = System.currentTimeMillis()

                // Insert anime entities
                animeDao.insertAll(entities)

                // Insert remote keys for pagination
                val remoteKeys = animeList.map { anime ->
                    RemoteKeys(
                        animeId = anime.malId,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1,
                        lastUpdated = currentTime
                    )
                }
                remoteKeysDao.insertAll(remoteKeys)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            // Network error - return cached data if available
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            // HTTP error - return cached data if available
            MediatorResult.Error(e)
        }
    }

    /**
     * Gets the remote key for the last item in the current state.
     * Used to determine the next page to load for APPEND operations.
     */
    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, AnimeEntity>
    ): RemoteKeys? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { anime ->
                remoteKeysDao.remoteKeysAnimeId(anime.id)
            }
    }

    /**
     * Converts an AnimeDto to an AnimeEntity.
     * Handles mapping of nested structures (images, trailer, genres) to flat entity fields.
     */
    private fun AnimeDto.toEntity(): AnimeEntity {
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
            lastUpdated = currentTime
        )
    }
}
