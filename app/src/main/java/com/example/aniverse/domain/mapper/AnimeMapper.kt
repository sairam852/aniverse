package com.example.aniverse.domain.mapper

import com.example.aniverse.data.local.entity.AnimeEntity
import com.example.aniverse.data.remote.dto.AnimeDto
import com.example.aniverse.data.remote.dto.AnimeDetailsDto
import com.example.aniverse.domain.model.Anime
import com.example.aniverse.domain.model.AnimeDetails

/**
 * Mapper functions to convert between data layer models (DTOs, Entities)
 * and domain models.
 *
 * These functions handle data transformation and formatting,
 * keeping the domain layer clean and UI-ready.
 */

/**
 * Converts an AnimeDto (from API) to a domain Anime model.
 */
fun AnimeDto.toDomain(): Anime {
    return Anime(
        id = malId,
        title = title,
        titleEnglish = titleEnglish,
        titleJapanese = titleJapanese,
        type = type,
        episodes = episodes,
        status = status,
        score = score,
        rank = rank,
        popularity = popularity,
        synopsis = synopsis,
        season = season,
        year = year,
        imageUrl = images?.jpg?.imageUrl ?: images?.webp?.imageUrl,
        largeImageUrl = images?.jpg?.largeImageUrl ?: images?.webp?.largeImageUrl,
        trailerUrl = trailer?.embedUrl ?: trailer?.url,
        genres = genres?.mapNotNull { it.name } ?: emptyList()
    )
}

/**
 * Converts an AnimeEntity (from Room database) to a domain Anime model.
 */
fun AnimeEntity.toDomain(): Anime {
    return Anime(
        id = id,
        title = title,
        titleEnglish = titleEnglish,
        titleJapanese = titleJapanese,
        type = type,
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
        genres = genres?.split(", ")?.filter { it.isNotEmpty() } ?: emptyList()
    )
}

/**
 * Converts an AnimeDetailsDto (from API) to a domain AnimeDetails model.
 */
fun AnimeDetailsDto.toDomain(): AnimeDetails {
    return AnimeDetails(
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
        background = background,
        duration = duration,
        rating = rating,
        season = season,
        year = year,
        imageUrl = images?.jpg?.imageUrl ?: images?.webp?.imageUrl,
        largeImageUrl = images?.jpg?.largeImageUrl ?: images?.webp?.largeImageUrl,
        trailerUrl = trailer?.embedUrl ?: trailer?.url,
        genres = genres?.mapNotNull { it.name } ?: emptyList(),
        airedFrom = aired?.from,
        airedTo = aired?.to,
        airedString = aired?.string
    )
}

/**
 * Converts an AnimeEntity (from Room database) to a domain AnimeDetails model.
 * Note: Some details fields may be null if not stored in the entity.
 */
fun AnimeEntity.toDomainDetails(): AnimeDetails {
    return AnimeDetails(
        id = id,
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
        background = null,
        duration = null,
        rating = null,
        season = season,
        year = year,
        imageUrl = imageUrl,
        largeImageUrl = largeImageUrl,
        trailerUrl = trailerUrl,
        genres = genres?.split(", ")?.filter { it.isNotEmpty() } ?: emptyList(),
        airedFrom = null,
        airedTo = null,
        airedString = airedString
    )
}
