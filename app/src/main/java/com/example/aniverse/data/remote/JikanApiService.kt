package com.example.aniverse.data.remote

import com.example.aniverse.data.remote.dto.AnimeDetailsResponseDto
import com.example.aniverse.data.remote.dto.TopAnimeResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Jikan API service definition.
 *
 * See docs: https://docs.api.jikan.moe/
 */
interface JikanApiService {

    /**
     * Get list of top anime with pagination.
     *
     * Example: GET /v4/top/anime?page=1
     */
    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int
    ): TopAnimeResponseDto

    /**
     * Get details for a single anime.
     *
     * Example: GET /v4/anime/{id}
     */
    @GET("anime/{id}")
    suspend fun getAnimeDetails(
        @Path("id") id: Int
    ): AnimeDetailsResponseDto
}

