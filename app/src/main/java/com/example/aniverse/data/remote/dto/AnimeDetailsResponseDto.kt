package com.example.aniverse.data.remote.dto

import com.squareup.moshi.Json

/**
 * DTO for `/v4/anime/{id}` response.
 *
 * The response wraps a single anime details object in `data`.
 */
data class AnimeDetailsResponseDto(
    val data: AnimeDetailsDto
)

/**
 * More detailed anime information.
 * This reuses the common nested DTOs where possible.
 */
data class AnimeDetailsDto(
    @Json(name = "mal_id")
    val malId: Int,
    val url: String?,
    val title: String?,
    @Json(name = "title_english")
    val titleEnglish: String?,
    @Json(name = "title_japanese")
    val titleJapanese: String?,
    val type: String?,
    val source: String?,
    val episodes: Int?,
    val status: String?,
    val score: Double?,
    val rank: Int?,
    val popularity: Int?,
    val synopsis: String?,
    val background: String?,
    val duration: String?,
    val rating: String?,
    val season: String?,
    val year: Int?,
    val images: ImagesDto?,
    val trailer: TrailerDto?,
    val genres: List<GenreDto>?,
    val aired: AiredDto?
)

/**
 * Airing date information.
 * See: https://docs.jikan.moe/objects/common/aired
 */
data class AiredDto(
    val from: String?,
    val to: String?,
    val prop: AiredPropDto?
)

data class AiredPropDto(
    val from: AiredDateDto?,
    val to: AiredDateDto?
)

data class AiredDateDto(
    val day: Int?,
    val month: Int?,
    val year: Int?
)

