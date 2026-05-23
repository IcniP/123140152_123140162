package com.example.gamenews.data.repository

import com.example.gamenews.data.remote.api.GameBrainService
import com.example.gamenews.data.mapper.toDomain
import com.example.gamenews.domain.model.Game
import com.example.gamenews.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GameRepositoryImpl(
    private val apiService: GameBrainService
) : GameRepository {

    private val cachedGames = mutableListOf<Game>()

    override fun getLatestGames(): Flow<List<Game>> = flow {
        val result = apiService.searchGames(
            query = "*",
            releaseDate = "last_month",
            sortBy = "release_date"
        )
        result.onSuccess { response ->
            val domainList = response.results.map { it.toDomain() }
            cachedGames.clear()
            cachedGames.addAll(domainList)
            emit(domainList)
        }.onFailure {
            emit(emptyList())
        }
    }

    override fun searchGames(query: String, genre: String?): Flow<List<Game>> = flow {
        val searchQuery = query.ifBlank { "*" }
        val result = apiService.searchGames(
            query = searchQuery,
            sortBy = "release_date"
            // hapus genre dari sini
        )
        result.onSuccess { response ->
            var list = response.results.map { it.toDomain() }
            // Filter genre di client-side
            if (!genre.isNullOrBlank()) {
                list = list.filter { it.genre.equals(genre, ignoreCase = true) }
            }
            cachedGames.clear()
            cachedGames.addAll(list)
            emit(list)
        }.onFailure {
            emit(emptyList())
        }
    }

    override fun getGameById(id: Long): Flow<Game?> = flow {
        val result = apiService.getGameDetails(id)
        result.onSuccess { entity ->
            // LOG SEMENTARA — hapus setelah tahu field yang benar
            println("=== RAW GAME DETAIL ===")
            println("shortDescription: ${entity.shortDescription}")
            println("description: ${entity.description}")
            println("about: ${entity.about}")
            println("gameplay: ${entity.gameplay}")
            println("=======================")
            emit(entity.toDomain())
        }.onFailure {
            emit(cachedGames.find { it.id == id.toInt() })
        }
    }
}