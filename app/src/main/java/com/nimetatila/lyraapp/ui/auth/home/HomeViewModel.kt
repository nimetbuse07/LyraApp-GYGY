package com.nimetatila.lyraapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect: Flow<HomeEffect> = _effect.receiveAsFlow()

    init {
        loadMockContent()
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.QuickPickClicked -> {
                _uiState.update { current ->
                    val selected = current.quickPicks.find { it.id == intent.itemId }
                    current.copy(currentPlayingTrack = selected, isPlaying = true)
                }
            }
            is HomeIntent.TrackClicked -> {
                _uiState.update { current ->
                    val selected = current.recentlyPlayed.find { it.id == intent.itemId }
                        ?: current.customPlaylists.find { it.id == intent.itemId }
                    current.copy(currentPlayingTrack = selected, isPlaying = true)
                }
            }
            HomeIntent.TogglePlayPause -> _uiState.update { it.copy(isPlaying = !it.isPlaying) }
            HomeIntent.ToggleFavorite -> _uiState.update { it.copy(isFavorite = !it.isFavorite) }
            HomeIntent.ProfileClicked -> viewModelScope.launch { _effect.send(HomeEffect.NavigateToProfile) }
            HomeIntent.SeeAllRecentlyPlayedClicked -> {
                viewModelScope.launch { _effect.send(HomeEffect.ShowNotification("Çok yakında eklenecek")) }
            }
        }
    }

    private fun loadMockContent() {
        _uiState.update {
            HomeUiState(
                userName = "Nazlı Yazıcı",
                quickPicks = listOf(
                    PlayableItem("1", "Gece Sürüşü", gradientIndex = 0),
                    PlayableItem("2", "Sabah Kahvesi", gradientIndex = 1),
                    PlayableItem("3", "Neon Sokaklar", gradientIndex = 2),
                    PlayableItem("4", "Odaklan", gradientIndex = 0),
                    PlayableItem("5", "Derin Mavi", gradientIndex = 1),
                    PlayableItem("6", "Yaz Anıları", gradientIndex = 2)
                ),
                recentlyPlayed = listOf(
                    PlayableItem("3", "Neon Sokaklar", "Şehir Işıkları"),
                    PlayableItem("5", "Derin Mavi", "Okyanus"),
                    PlayableItem("7", "Yıldız Tozu", "Polaris")
                ),
                customPlaylists = listOf(
                    PlayableItem("8", "Akustik Yolculuk", "Sakin Ritmler"),
                    PlayableItem("9", "Yeraltı Beats", "Lo-Fi Evreni")
                ),
                currentPlayingTrack = PlayableItem("3", "Neon Sokaklar", "Şehir Işıkları"),
                isPlaying = false
            )
        }
    }
}