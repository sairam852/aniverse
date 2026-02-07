package com.example.aniverse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.example.aniverse.domain.model.Anime
import com.example.aniverse.domain.usecase.GetAnimeListUseCase
import com.example.aniverse.domain.usecase.SyncAnimeUseCase
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the home screen displaying the top anime list.
 *
 * Exposes a Flow of PagingData<Anime> from the GetAnimeListUseCase,
 * and provides a refresh method for pull-to-refresh.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAnimeListUseCase: GetAnimeListUseCase,
    private val syncAnimeUseCase: SyncAnimeUseCase
) : ViewModel() {

    val animePagingFlow: Flow<PagingData<Anime>> = getAnimeListUseCase()
        .cachedIn(viewModelScope)

    /**
     * Triggers a sync to refresh the top anime list.
     * Called on pull-to-refresh.
     */
    fun refresh() {
        viewModelScope.launch {
            syncAnimeUseCase()
        }
    }
}
