package com.example.gamenews.data.mapper

import com.example.gamenews.data.remote.api.GameRemoteEntity
import com.example.gamenews.domain.model.Game

fun GameRemoteEntity.toDomain(): Game = Game(
    id = id,
    title = name,
    description = shortDescription ?: gameplay ?: "",
    genre = genre ?: "Unknown",
    rating = rating?.mean ?: 0.0,
    imageUrl = image,
    developer = developer,
    releaseYear = year?.toInt()
)