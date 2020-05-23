package com.an.trailers.ui.search.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.an.trailers.data.Resource
import com.an.trailers.data.local.dao.TvDao
import com.an.trailers.data.local.entity.TvEntity
import com.an.trailers.data.remote.api.TvApiService
import com.an.trailers.data.repository.TvRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

import javax.inject.Inject

class TvSearchViewModel @Inject constructor(
    tvDao: TvDao,
    tvApiService: TvApiService) : ViewModel() {

    private val tvRepository: TvRepository = TvRepository(tvDao, tvApiService)
    private val tvsLiveData = MutableLiveData<Resource<List<TvEntity>>>()

    fun searchTv(text: String) {
        tvRepository.searchTvs(1, text)
            .onEach { resource -> tvsLiveData.postValue(resource) }
            .launchIn(viewModelScope)
    }

    fun getTvListLiveData() = tvsLiveData
}
