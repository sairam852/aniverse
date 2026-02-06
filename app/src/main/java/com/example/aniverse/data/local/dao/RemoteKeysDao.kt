package com.example.aniverse.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.aniverse.data.local.entity.RemoteKeys

/**
 * DAO for accessing RemoteKeys used by Paging 3's RemoteMediator.
 */
@Dao
interface RemoteKeysDao {

    @Query("SELECT * FROM remote_keys WHERE anime_id = :animeId LIMIT 1")
    suspend fun remoteKeysAnimeId(animeId: Int): RemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKeys>)

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()
}

