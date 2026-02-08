package com.example.aniverse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.aniverse.data.local.dao.AnimeDao
import com.example.aniverse.data.local.dao.RemoteKeysDao
import com.example.aniverse.data.local.entity.AnimeEntity
import com.example.aniverse.data.local.entity.RemoteKeys

/**
 * Main Room database for the Aniverse app.
 *
 * This acts as the single source of truth for anime data and
 * pagination keys used by the Paging 3 RemoteMediator.
 */
@Database(
    entities = [
        AnimeEntity::class,
        RemoteKeys::class,
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun animeDao(): AnimeDao

    abstract fun remoteKeysDao(): RemoteKeysDao
}

