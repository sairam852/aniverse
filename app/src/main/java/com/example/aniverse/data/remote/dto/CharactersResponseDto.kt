package com.example.aniverse.data.remote.dto

import com.squareup.moshi.Json

/**
 * DTO for `/v4/anime/{id}/characters` response.
 */
data class CharactersResponseDto(
    val data: List<CharacterDataDto>
)

data class CharacterDataDto(
    val character: CharacterDto,
    val role: String?,
    val voice_actors: List<VoiceActorDto>?
)

data class CharacterDto(
    @Json(name = "mal_id")
    val malId: Int,
    val url: String?,
    val images: CharacterImagesDto?,
    val name: String?
)

data class CharacterImagesDto(
    val jpg: ImageFormatDto?,
    val webp: ImageFormatDto?
)

data class VoiceActorDto(
    val person: PersonDto?,
    val language: String?
)

data class PersonDto(
    @Json(name = "mal_id")
    val malId: Int,
    val url: String?,
    val images: CharacterImagesDto?, // Reusing CharacterImagesDto for simplicity as structure is similar
    val name: String?
)
