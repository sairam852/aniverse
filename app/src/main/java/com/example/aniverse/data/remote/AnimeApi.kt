package com.example.aniverse.data.remote

import com.example.aniverse.data.remote.dto.AnimeDetailsResponseDto
import com.example.aniverse.data.remote.dto.TopAnimeResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API for Jikan v4.
 *
 * Base URL is provided via DI: `https://api.jikan.moe/v4/`
 */
interface AnimeApi {

    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int
    ): TopAnimeResponseDto

    @GET("anime/{id}")
    suspend fun getAnimeDetails(
        @Path("id") id: Int
    ): AnimeDetailsResponseDto

    @GET("anime/{id}/characters")
    suspend fun getAnimeCharacters(
        @Path("id") id: Int
    ): com.example.aniverse.data.remote.dto.CharactersResponseDto
}

