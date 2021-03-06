package com.example.favorite.data

import androidx.lifecycle.LiveData
import com.example.core.db.dao.entities.FilmEntity

interface FavoriteRepository {

    fun getSavedFilm(): LiveData<List<FilmEntity>>

    suspend fun deleteFilm(filmEntity: FilmEntity)

    suspend fun upsertFilm(filmEntity: FilmEntity)
}