package com.an.trailers.data.remote.api


import com.an.trailers.data.local.entity.MovieEntity
import com.an.trailers.data.remote.model.CreditResponse
import com.an.trailers.data.remote.model.MovieApiResponse
import com.an.trailers.data.remote.model.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {

    @GET("movie/{type}?language=en-US&region=US")
    suspend fun fetchMoviesByType(
        @Path("type") type: String,
        @Query("page") page: Long
    ): MovieApiResponse?


    @GET("/3/movie/{movieId}")
    suspend fun fetchMovieDetail(@Path("movieId") movieId: String): MovieEntity


    @GET("/3/movie/{movieId}/videos")
    suspend fun fetchMovieVideo(@Path("movieId") movieId: String): VideoResponse

    @GET("/3/movie/{movieId}/credits")
    suspend fun fetchCastDetail(@Path("movieId") movieId: String): CreditResponse


    @GET("/3/movie/{movieId}/similar")
    suspend fun fetchSimilarMovie(
        @Path("movieId") movieId: String,
        @Query("page") page: Long
    ): MovieApiResponse


    @GET("/3/search/movie")
    suspend fun searchMoviesByQuery(
        @Query("query") query: String,
        @Query("page") page: String
    ): MovieApiResponse?
}
