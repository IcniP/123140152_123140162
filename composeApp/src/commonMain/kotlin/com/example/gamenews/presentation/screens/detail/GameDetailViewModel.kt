package com.example.gamenews.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val repository: GameRepository
) : ViewModel() {

    private val _game = MutableStateFlow<Game?>(null)
    val game: StateFlow<Game?> = _game.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadGame(gameId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.getGameById(gameId)
                .catch { e ->
                    _errorMessage.value = "Gagal memuat detail game"
                    _isLoading.value = false
                }
                .collect { game ->
                    _game.value = game
                    _isLoading.value = false
                }
        }
    }
}