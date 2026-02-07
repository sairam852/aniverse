package com.example.aniverse.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aniverse.domain.model.AnimeDetails
import com.example.aniverse.domain.usecase.GetAnimeDetailsUseCase
import com.example.aniverse.util.Resource
import com.example.aniverse.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * ViewModel for the anime detail screen.
 *
 * Exposes a sealed UI state (Loading, Success, Error) and loads
 * anime details for the given ID.
 */
@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getAnimeDetailsUseCase: GetAnimeDetailsUseCase
) : ViewModel() {

    private val animeId: Int = savedStateHandle.get<Int>("animeId") ?: -1

    private val _uiState = MutableStateFlow<UiState<AnimeDetails>>(UiState.Loading)
    val uiState: StateFlow<UiState<AnimeDetails>> = _uiState.asStateFlow()

    init {
        if (animeId != -1) {
            loadAnimeDetails()
        } else {
            _uiState.value = UiState.Error("Invalid anime ID")
        }
    }

    private fun loadAnimeDetails() {
        getAnimeDetailsUseCase(animeId)
            .onEach { resource ->
                _uiState.value = when (resource) {
                    is Resource.Loading -> UiState.Loading
                    is Resource.Success -> UiState.Success(resource.data)
                    is Resource.Error -> UiState.Error(
                        resource.message.ifEmpty { "Failed to load anime details" }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Retries loading anime details.
     */
    fun retry() {
        if (animeId != -1) {
            _uiState.value = UiState.Loading
            loadAnimeDetails()
        }
    }
}
