package com.example.aniverse.data.remote.dto

import com.squareup.moshi.Json

/**
 * DTO for the `/v4/top/anime` response.
 *
 * Only the fields required by the app are modeled here.
 */
data class TopAnimeResponseDto(
    val data: List<AnimeDto>,
    val pagination: PaginationDto?
)

data class PaginationDto(
    @Json(name = "current_page")
    val currentPage: Int?,
    @Json(name = "last_visible_page")
    val lastVisiblePage: Int?,
    @Json(name = "has_next_page")
    val hasNextPage: Boolean?,
    val items: PaginationItemsDto?
)

data class PaginationItemsDto(
    val count: Int?,
    val total: Int?,
    @Json(name = "per_page")
    val perPage: Int?
)

