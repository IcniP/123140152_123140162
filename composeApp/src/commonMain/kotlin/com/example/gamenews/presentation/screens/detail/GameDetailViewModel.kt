package com.example.gamenews.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.AIRepository
import com.example.gamenews.domain.repository.GameRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameDetailViewModel(
    private val repository: GameRepository,
    private val aiRepository: AIRepository
) : ViewModel() {

    private val _game = MutableStateFlow<Game?>(null)
    val game: StateFlow<Game?> = _game.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _aiDescription = MutableStateFlow<String?>(null)
    val aiDescription: StateFlow<String?> = _aiDescription.asStateFlow()

    private val _isGeneratingDescription = MutableStateFlow(false)
    val isGeneratingDescription: StateFlow<Boolean> = _isGeneratingDescription.asStateFlow()

    fun loadGame(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getGameById(id)
                .collect { result ->
                    _game.value = result
                    _isLoading.value = false
                    result?.let { generateAIDescription(it) }
                }
        }
    }

    private fun generateAIDescription(game: Game) {
        viewModelScope.launch {
            _isGeneratingDescription.value = true
            aiRepository.generateGameDescription(
                title = game.title,
                genre = game.genre,
                developer = game.developer,
                year = game.releaseYear
            ).onSuccess { description ->
                _aiDescription.value = description
            }.onFailure {
                _aiDescription.value = null
            }
            _isGeneratingDescription.value = false
        }
    }
}