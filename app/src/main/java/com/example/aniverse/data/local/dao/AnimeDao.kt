package com.example.aniverse.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aniverse.data.local.entity.AnimeEntity

/**
 * DAO for accessing anime entities from the local Room database.
 *
 * Exposes a PagingSource for the top anime list and helpers
 * for inserting and clearing cached data.
 */
@Dao
interface AnimeDao {

    /**
     * Paging source for the top anime list.
     *
     * Ordering primarily by rank (ascending) ensures a stable
     * ordering that matches the remote "top" list semantics.
     * Fallback to ID ordering for items without rank.
     */
    @Query(
        """
        SELECT * FROM anime
        ORDER BY 
            CASE WHEN rank IS NULL THEN 1 ELSE 0 END,
            rank ASC,
            id ASC
        """
    )
    fun getPagedAnime(): PagingSource<Int, AnimeEntity>

    @Query("SELECT * FROM anime WHERE id = :id LIMIT 1")
    suspend fun getAnimeById(id: Int): AnimeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(anime: List<AnimeEntity>)

    @Query("DELETE FROM anime")
    suspend fun clearAll()

    @Query("SELECT * FROM anime LIMIT 1")
    suspend fun getAnyAnime(): AnimeEntity?
}

