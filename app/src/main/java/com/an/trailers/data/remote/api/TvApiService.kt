package com.an.trailers.data.remote.api


import com.an.trailers.data.local.entity.TvEntity
import com.an.trailers.data.remote.model.CreditResponse
import com.an.trailers.data.remote.model.TvApiResponse
import com.an.trailers.data.remote.model.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TvApiService {

    @GET("tv/{type}?language=en-US&region=US")
    suspend fun fetchTvListByType(
        @Path("type") type: String,
        @Query("page") page: Long
    ): TvApiResponse?


    @GET("/3/tv/{tvId}")
    suspend fun fetchTvDetail(@Path("tvId") tvId: String): TvEntity


    @GET("/3/tv/{tvId}/videos")
    suspend fun fetchTvVideo(@Path("tvId") tvId: String): VideoResponse

    @GET("/3/tv/{tvId}/credits")
    suspend fun fetchCastDetail(@Path("tvId") tvId: String): CreditResponse


    @GET("/3/tv/{tvId}/similar")
    suspend fun fetchSimilarTvList(
        @Path("tvId") tvId: String,
        @Query("page") page: Long
    ): TvApiResponse


    @GET("/3/search/tv")
    suspend fun searchTvsByQuery(
        @Query("query") query: String,
        @Query("page") page: String
    ): TvApiResponse?
}
