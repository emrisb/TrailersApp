package com.an.trailers.data.repository


import com.an.trailers.data.NetworkBoundResource
import com.an.trailers.data.Resource
import com.an.trailers.data.local.dao.MovieDao
import com.an.trailers.data.local.entity.MovieEntity
import com.an.trailers.data.remote.api.MovieApiService
import com.an.trailers.data.remote.model.CreditResponse
import com.an.trailers.data.remote.model.MovieApiResponse
import com.an.trailers.data.remote.model.VideoResponse
import com.an.trailers.utils.AppUtils
import kotlinx.coroutines.flow.*
import javax.inject.Singleton


@Singleton
class MovieRepository(
    private val movieDao: MovieDao,
    private val movieApiService: MovieApiService
) {

    fun loadMoviesByType(
        page: Long,
        type: String
    ): Flow<Resource<List<MovieEntity>>> {
        return object : NetworkBoundResource<List<MovieEntity>, MovieApiResponse>() {

            override suspend fun saveCallResult(item: MovieApiResponse) {
                val movieEntities = ArrayList<MovieEntity>()
                for (movieEntity in item.results) {

                    val storedEntity = movieDao.getMovieById(movieEntity.id)
                    if (storedEntity == null) {
                        movieEntity.categoryTypes = listOf(type)
                    } else {
                        val categories: MutableList<String> = mutableListOf()
                        if (storedEntity.categoryTypes != null) categories.addAll(storedEntity.categoryTypes!!)
                        categories.add(type)
                        movieEntity.categoryTypes = categories
                    }

                    movieEntity.page = item.page
                    movieEntity.totalPages = item.total_pages
                    movieEntities.add(movieEntity)
                }
                movieDao.insertMovies(movieEntities)
            }

            override fun shouldFetch(): Boolean {
                return true
            }

            override fun loadFromDb(): Flow<List<MovieEntity>> {
                return flow {
                    val movieEntities = movieDao.getMoviesByPage(page)
                    if (movieEntities == null || movieEntities.isEmpty()) {
                        emitAll(emptyFlow<List<MovieEntity>>())
                    } else emit(AppUtils.getMoviesByType(type, movieEntities))
                }
            }

            override fun createCall(): Flow<Resource<MovieApiResponse>> {
                return flow { emit(movieApiService.fetchMoviesByType(type, page)) }
                    .map { movieApiResponse ->
                        if (movieApiResponse == null)
                            Resource.error("", MovieApiResponse(page, emptyList(), 0, 1))
                        else
                            Resource.success(movieApiResponse)
                    }
            }
        }.getAsFlow()
    }


    fun fetchMovieDetails(movieId: Long): Flow<Resource<MovieEntity>> {
        return object : NetworkBoundResource<MovieEntity, MovieEntity>() {
            override suspend fun saveCallResult(item: MovieEntity) {
                val movieEntity: MovieEntity? = movieDao.getMovieById(movieId)
                if (null == movieEntity) movieDao.insertMovie(item)
                else {
                    item.page = movieEntity.page
                    item.totalPages = movieEntity.totalPages
                    item.categoryTypes = movieEntity.categoryTypes
                    movieDao.updateMovie(item)
                }
            }

            override fun shouldFetch(): Boolean {
                return true
            }

            override fun loadFromDb(): Flow<MovieEntity> {
                return flow<MovieEntity> {
                    val movieEntity: MovieEntity? = movieDao.getMovieById(movieId)
                    if (null == movieEntity) emitAll(emptyFlow())
                    else emit(movieEntity)
                }
            }

            override fun createCall(): Flow<Resource<MovieEntity>> {
                val id = movieId.toString()
                return combine(
                    flow { emit(movieApiService.fetchMovieDetail(id)) },
                    flow { emit(movieApiService.fetchMovieVideo(id)) },
                    flow { emit(movieApiService.fetchCastDetail(id)) },
                    flow { emit(movieApiService.fetchSimilarMovie(id, 1)) }
                ) { movieEntity: MovieEntity,
                    videoResponse: VideoResponse?,
                    creditResponse: CreditResponse?,
                    movieApiResponse: MovieApiResponse? ->

                    if (videoResponse != null) {
                        movieEntity.videos = videoResponse.results
                    }

                    if (creditResponse != null) {
                        movieEntity.crews = creditResponse.crew
                        movieEntity.casts = creditResponse.cast
                    }

                    if (movieApiResponse != null) {
                        movieEntity.similarMovies = movieApiResponse.results
                    }
                    Resource.success(movieEntity)
                }
            }
        }.getAsFlow()
    }


    fun searchMovies(
        page: Long,
        query: String
    ): Flow<Resource<List<MovieEntity>>> {
        return object : NetworkBoundResource<List<MovieEntity>, MovieApiResponse>() {

            override suspend fun saveCallResult(item: MovieApiResponse) {
                val movieEntities = ArrayList<MovieEntity>()
                for (movieEntity in item.results) {
                    val storedEntity = movieDao.getMovieById(movieEntity.id)
                    if (storedEntity == null) {
                        movieEntity.categoryTypes = listOf(query)
                    } else {
                        val categories: MutableList<String> = mutableListOf()
                        if (storedEntity.categoryTypes != null) categories.addAll(storedEntity.categoryTypes!!)
                        categories.add(query)
                        movieEntity.categoryTypes = categories
                    }

                    movieEntity.page = item.page
                    movieEntity.totalPages = item.total_pages
                    movieEntities.add(movieEntity)
                }
                movieDao.insertMovies(movieEntities)
            }

            override fun shouldFetch(): Boolean {
                return true
            }

            override fun loadFromDb(): Flow<List<MovieEntity>> {
                return flow {
                    val movieEntities = movieDao.getMoviesByPage(page)
                    if (movieEntities == null || movieEntities.isEmpty()) {
                        emitAll(emptyFlow<List<MovieEntity>>())
                    } else emit(AppUtils.getMoviesByType(query, movieEntities))
                }
            }

            override fun createCall(): Flow<Resource<MovieApiResponse>> {
                return flow { emit(movieApiService.searchMoviesByQuery(query, "1")) }
                    .map { movieApiResponse ->
                        if (movieApiResponse == null) Resource.error(
                            "",
                            MovieApiResponse(1, emptyList(), 0, 1)
                        )
                        else Resource.success(movieApiResponse)
                    }
            }
        }.getAsFlow()
    }

}
