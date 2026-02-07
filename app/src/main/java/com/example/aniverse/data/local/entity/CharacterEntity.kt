package com.example.aniverse.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.aniverse.domain.model.Character

/**
 * Entity representing a character in an anime.
 * 
 * Linked to AnimeEntity via animeId. 
 * Allows duplicates of the same character for different anime (simpler than many-to-many).
 */
@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(
            entity = AnimeEntity::class,
            parentColumns = ["id"],
            childColumns = ["animeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["animeId"])]
)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val localId: Int = 0,
    val animeId: Int,
    val characterId: Int,
    val name: String,
    val imageUrl: String?
) {
    fun toDomain(): Character {
        return Character(
            id = characterId,
            name = name,
            imageUrl = imageUrl
        )
    }
}
