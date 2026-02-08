package com.example.aniverse.domain.model

/**
 * Domain model representing detailed anime information.
 *
 * This extends the basic Anime model with additional fields
 * needed for the detail screen, such as background, duration, rating, etc.
 */
data class AnimeDetails(
    val id: Int,
    val title: String?,
    val titleEnglish: String?,
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
    val imageUrl: String?,
    val largeImageUrl: String?,
    val trailerUrl: String?,
    val genres: List<String>,
    val airedFrom: String?,
    val airedTo: String?,
    val airedString: String?,
    val cast: List<Character> = emptyList()
) {
    /**
     * Display title: prefers English, falls back to Japanese title, then default title.
     */
    val displayTitle: String
        get() = titleEnglish ?: titleJapanese ?: title ?: "Unknown"

    /**
     * Formatted score string for display (e.g., "8.5" or "N/A").
     */
    val formattedScore: String
        get() = score?.let { String.format("%.1f", it) } ?: "N/A"

    /**
     * Formatted episodes string (e.g., "24 episodes" or "Ongoing").
     */
    val formattedEpisodes: String
        get() = when {
            episodes != null -> "$episodes ${if (episodes == 1) "episode" else "episodes"}"
            status == "Currently Airing" -> "Ongoing"
            else -> "Unknown"
        }

    /**
     * Formatted genres string (e.g., "Action, Adventure, Drama").
     */
    val formattedGenres: String
        get() = genres.joinToString(", ").takeIf { it.isNotEmpty() } ?: "Unknown"

    /**
     * Formatted airing period (e.g., "Jan 2020 - Mar 2020" or "Jan 2020 - Present").
     */
    val formattedAiringPeriod: String
        get() = airedString ?: when {
            airedFrom != null && airedTo != null -> "$airedFrom - $airedTo"
            airedFrom != null -> "$airedFrom - Present"
            else -> "Unknown"
        }
}

data class Character(
    val id: Int,
    val name: String,
    val imageUrl: String?
)
