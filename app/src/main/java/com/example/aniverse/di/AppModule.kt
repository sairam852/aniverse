package com.example.aniverse.di

import com.example.aniverse.data.repository.AnimeRepositoryImpl
import com.example.aniverse.domain.repository.AnimeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAnimeRepository(
        animeRepositoryImpl: AnimeRepositoryImpl
    ): AnimeRepository {
        return animeRepositoryImpl
    }
}

