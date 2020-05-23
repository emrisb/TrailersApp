package com.an.trailers.ui.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.trailers.data.Resource
import com.an.trailers.data.local.dao.MovieDao
import com.an.trailers.data.local.entity.MovieEntity
import com.an.trailers.data.remote.api.MovieApiService
import com.an.trailers.data.repository.MovieRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MovieSearchViewModel @Inject constructor(
    movieDao: MovieDao,
    movieApiService: MovieApiService) : ViewModel() {


    private val movieRepository: MovieRepository = MovieRepository(movieDao, movieApiService)
    private val moviesLiveData = MutableLiveData<Resource<List<MovieEntity>>>()

    fun searchMovie(text: String) {
        movieRepository.searchMovies(1, text)
            .onEach { resource -> moviesLiveData.postValue(resource) }
            .launchIn(viewModelScope)
    }

    fun getMoviesLiveData() = moviesLiveData
}
