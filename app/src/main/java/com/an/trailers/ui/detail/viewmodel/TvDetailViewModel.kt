package com.an.trailers.ui.detail.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.trailers.data.local.dao.TvDao
import com.an.trailers.data.local.entity.TvEntity
import com.an.trailers.data.remote.api.TvApiService
import com.an.trailers.data.repository.TvRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

import javax.inject.Inject

class TvDetailViewModel @Inject constructor(
    tvDao: TvDao,
    tvApiService: TvApiService
) : ViewModel() {

    private val tvRepository: TvRepository = TvRepository(tvDao, tvApiService)
    private val tvDetailsLiveData = MutableLiveData<TvEntity>()

    fun fetchMovieDetail(tvEntity: TvEntity) {
        tvRepository.fetchTvDetails(tvEntity.id)
            .onEach { resource -> if (resource.isLoaded) tvDetailsLiveData.postValue(resource.data) }
            .launchIn(viewModelScope)
    }

    fun getTvDetailsLiveData() = tvDetailsLiveData
}
