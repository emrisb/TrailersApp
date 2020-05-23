package com.an.trailers.data.repository


import com.an.trailers.data.NetworkBoundResource
import com.an.trailers.data.Resource
import com.an.trailers.data.local.dao.TvDao
import com.an.trailers.data.local.entity.TvEntity
import com.an.trailers.data.remote.api.TvApiService
import com.an.trailers.data.remote.model.CreditResponse
import com.an.trailers.data.remote.model.TvApiResponse
import com.an.trailers.data.remote.model.VideoResponse
import com.an.trailers.utils.AppUtils
import kotlinx.coroutines.flow.*
import java.util.*

import javax.inject.Singleton

@Singleton
class TvRepository(
    private val tvDao: TvDao,
    private val tvApiService: TvApiService
) {

    fun loadTvsByType(
        page: Long,
        type: String
    ): Flow<Resource<List<TvEntity>>> {
        return object : NetworkBoundResource<List<TvEntity>, TvApiResponse>() {

            override suspend fun saveCallResult(item: TvApiResponse) {
                val tvEntities = ArrayList<TvEntity>()
                for (tvEntity in item.results) {
                    val storedEntity = tvDao.getTvById(tvEntity.id)
                    if (storedEntity == null) {
                        tvEntity.categoryTypes = listOf(type)
                    } else {
                        val categories: MutableList<String> = mutableListOf()
                        if (storedEntity.categoryTypes != null) categories.addAll(storedEntity.categoryTypes!!)
                        categories.add(type)
                        tvEntity.categoryTypes = categories
                    }

                    tvEntity.page = item.page
                    tvEntity.totalPages = item.total_pages
                    tvEntities.add(tvEntity)
                }
                tvDao.insertTvList(tvEntities)
            }

            override fun shouldFetch(): Boolean {
                return true
            }

            override fun loadFromDb(): Flow<List<TvEntity>> {
                return flow {
                    val movieEntities = tvDao.getTvsByPage(page)
                    if (movieEntities == null || movieEntities.isEmpty()) {
                        emitAll(emptyFlow<List<TvEntity>>())
                    } else emit(AppUtils.getTvsByType(type, movieEntities))
                }
            }

            override fun createCall(): Flow<Resource<TvApiResponse>> {
                return flow { emit(tvApiService.fetchTvListByType(type, page)) }
                    .map { tvApiResponse ->
                        if (tvApiResponse == null)
                            Resource.error("", TvApiResponse(1, emptyList(), 0, 1))
                        else
                            Resource.success(tvApiResponse)
                    }
            }
        }.getAsFlow()
    }


    fun fetchTvDetails(tvId: Long): Flow<Resource<TvEntity>> {
        return object : NetworkBoundResource<TvEntity, TvEntity>() {
            override suspend fun saveCallResult(item: TvEntity) {
                val tvEntity: TvEntity? = tvDao.getTvById(tvId)
                if (null == tvEntity) tvDao.insertTv(item)
                else {
                    item.page = tvEntity.page
                    item.totalPages = tvEntity.totalPages
                    item.categoryTypes = tvEntity.categoryTypes
                    tvDao.updateTv(item)
                }
            }

            override fun shouldFetch(): Boolean {
                return true
            }

            override fun loadFromDb(): Flow<TvEntity> {
                return flow<TvEntity> {
                    val tvEntity: TvEntity? = tvDao.getTvById(tvId)
                    if (null == tvEntity) emitAll(emptyFlow())
                    else emit(tvEntity)
                }
            }

            override fun createCall(): Flow<Resource<TvEntity>> {
                val id = tvId.toString()
                return combine(
                    flow { emit(tvApiService.fetchTvDetail(id)) },
                    flow { emit(tvApiService.fetchTvVideo(id)) },
                    flow { emit(tvApiService.fetchCastDetail(id)) },
                    flow { emit(tvApiService.fetchSimilarTvList(id, 1)) }
                ) { tvEntity: TvEntity,
                    videoResponse: VideoResponse?,
                    creditResponse: CreditResponse?,
                    tvApiResponse: TvApiResponse? ->

                    if (videoResponse != null) {
                        tvEntity.videos = videoResponse.results
                    }

                    if (creditResponse != null) {
                        tvEntity.crews = creditResponse.crew
                        tvEntity.casts = creditResponse.cast
                    }

                    if (tvApiResponse != null) {
                        tvEntity.similarTvEntities = tvApiResponse.results
                    }
                    Resource.success(tvEntity)
                }
            }
        }.getAsFlow()
    }


    fun searchTvs(
        page: Long,
        query: String
    ): Flow<Resource<List<TvEntity>>> {
        return object : NetworkBoundResource<List<TvEntity>, TvApiResponse>() {

            override suspend fun saveCallResult(item: TvApiResponse) {
                val tvEntities = ArrayList<TvEntity>()
                for (tvEntity in item.results) {
                    val storedEntity = tvDao.getTvById(tvEntity.id)
                    if (storedEntity == null) {
                        tvEntity.categoryTypes = listOf(query)
                    } else {
                        val categories: MutableList<String> = mutableListOf()
                        if (storedEntity.categoryTypes != null) categories.addAll(storedEntity.categoryTypes!!)
                        categories.add(query)
                        tvEntity.categoryTypes = categories
                    }

                    tvEntity.page = item.page
                    tvEntity.totalPages = item.total_pages
                    tvEntities.add(tvEntity)
                }
                tvDao.insertTvList(tvEntities)
            }

            override fun shouldFetch(): Boolean {
                return true
            }

            override fun loadFromDb(): Flow<List<TvEntity>> {
                return flow {
                    val movieEntities = tvDao.getTvsByPage(page)
                    if (movieEntities == null || movieEntities.isEmpty()) {
                        emitAll(emptyFlow<List<TvEntity>>())
                    } else emit(AppUtils.getTvsByType(query, movieEntities))
                }
            }

            override fun createCall(): Flow<Resource<TvApiResponse>> {
                return flow { emit(tvApiService.searchTvsByQuery(query, "1")) }
                    .map { tvApiResponse ->
                        if (tvApiResponse == null)
                            Resource.error("", TvApiResponse(1, emptyList(), 0, 1))
                        else
                            Resource.success(tvApiResponse)
                    }
            }
        }.getAsFlow()
    }

}
