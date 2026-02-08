package com.example.aniverse.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity representing an anime item stored locally.
 *
 * This is designed to contain the fields needed by both the
 * home (top anime list) and detail screens, acting as the
 * single source of truth for cached anime data.
 */
@Entity(tableName = "anime")
data class AnimeEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "title_english")
    val titleEnglish: String?,

    @ColumnInfo(name = "title_japanese")
    val titleJapanese: String?,

    @ColumnInfo(name = "type")
    val type: String?,

    @ColumnInfo(name = "source")
    val source: String?,

    @ColumnInfo(name = "episodes")
    val episodes: Int?,

    @ColumnInfo(name = "status")
    val status: String?,

    @ColumnInfo(name = "score")
    val score: Double?,

    @ColumnInfo(name = "rank")
    val rank: Int?,

    @ColumnInfo(name = "popularity")
    val popularity: Int?,

    @ColumnInfo(name = "synopsis")
    val synopsis: String?,

    @ColumnInfo(name = "season")
    val season: String?,

    @ColumnInfo(name = "year")
    val year: Int?,

    /**
     * Primary poster image URL (e.g. JPG/WebP).
     */
    @ColumnInfo(name = "image_url")
    val imageUrl: String?,

    /**
     * Higher resolution / large poster if available.
     */
    @ColumnInfo(name = "large_image_url")
    val largeImageUrl: String?,

    /**
     * URL or embeddable URL for trailer video (e.g. YouTube).
     */
    @ColumnInfo(name = "trailer_url")
    val trailerUrl: String?,

    /**
     * Comma-separated genre names (simple storage for now).
     */
    @ColumnInfo(name = "genres")
    val genres: String?,

    /**
     * Unix timestamp (ms) of the last time this row was updated
     * from the network. Used for offline freshness.
     */
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long,

    @ColumnInfo(name = "aired_string")
    val airedString: String?
)

