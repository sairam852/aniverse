package com.example.aniverse.data.remote.dto

import com.squareup.moshi.Json

/**
 * Core Anime DTO used by both top list and details endpoints.
 *
 * This follows Jikan v4's anime model closely but only includes
 * the fields that are likely to be needed by the app.
 */
data class AnimeDto(
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
    val season: String?,
    val year: Int?,
    val images: ImagesDto?,
    val trailer: TrailerDto?,
    val genres: List<GenreDto>?,
    val aired: AiredDto?
)

data class AiredDto(
    val from: String?,
    val to: String?,
    val prop: AiredPropDto?,
    val string: String?
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

/**
 * Image DTOs
 * See: https://docs.jikan.moe/objects/common/images
 */
data class ImagesDto(
    val jpg: ImageFormatDto?,
    val webp: ImageFormatDto?
)

data class ImageFormatDto(
    @Json(name = "image_url")
    val imageUrl: String?,
    @Json(name = "small_image_url")
    val smallImageUrl: String?,
    @Json(name = "large_image_url")
    val largeImageUrl: String?
)

/**
 * Trailer DTOs
 * See: https://docs.jikan.moe/objects/common/trailer
 */
data class TrailerDto(
    @Json(name = "youtube_id")
    val youtubeId: String?,
    val url: String?,
    @Json(name = "embed_url")
    val embedUrl: String?,
    val images: TrailerImagesDto?
)

data class TrailerImagesDto(
    @Json(name = "image_url")
    val imageUrl: String?,
    @Json(name = "small_image_url")
    val smallImageUrl: String?,
    @Json(name = "medium_image_url")
    val mediumImageUrl: String?,
    @Json(name = "large_image_url")
    val largeImageUrl: String?,
    @Json(name = "maximum_image_url")
    val maximumImageUrl: String?
)

/**
 * Genre DTO
 * See: https://docs.jikan.moe/objects/common/genre
 */
data class GenreDto(
    @Json(name = "mal_id")
    val malId: Int,
    val type: String?,
    val name: String?,
    val url: String?
)

