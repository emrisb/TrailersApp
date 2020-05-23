package com.an.trailers.ui.detail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.trailers.data.local.dao.MovieDao
import com.an.trailers.data.local.entity.MovieEntity
import com.an.trailers.data.remote.api.MovieApiService
import com.an.trailers.data.repository.MovieRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(
    movieDao: MovieDao,
    movieApiService: MovieApiService
) : ViewModel() {

    private val movieRepository: MovieRepository = MovieRepository(movieDao, movieApiService)
    private val movieDetailsLiveData = MutableLiveData<MovieEntity>()

    fun fetchMovieDetail(movieEntity: MovieEntity) {
        movieRepository.fetchMovieDetails(movieEntity.id)
            .onEach { resource -> if (resource.isLoaded) movieDetailsLiveData.postValue(resource.data) }
            .launchIn(viewModelScope)
    }

    fun getMovieDetailsLiveData() = movieDetailsLiveData
}
