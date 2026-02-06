package com.example.aniverse.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity storing pagination keys for each anime item.
 *
 * Used by Paging 3's RemoteMediator to know which page to
 * load next for append/prepend operations.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey
    @ColumnInfo(name = "anime_id")
    val animeId: Int,

    @ColumnInfo(name = "prev_key")
    val prevKey: Int?,

    @ColumnInfo(name = "next_key")
    val nextKey: Int?,

    /**
     * Unix timestamp (ms) of the last time the pagination keys
     * were updated. Useful for cache invalidation / freshness.
     */
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long
)

