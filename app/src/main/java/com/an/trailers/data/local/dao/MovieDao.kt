package com.an.trailers.data.local.dao

import androidx.room.*
import com.an.trailers.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMovie(movie: MovieEntity): Int

    @Query("SELECT * FROM `MovieEntity` where id = :id")
    suspend fun getMovieById(id: Long?): MovieEntity?

    @Query("SELECT * FROM `MovieEntity` where id = :id")
    fun getMovieDetailById(id: Long?): Flow<MovieEntity>

    @Query("SELECT * FROM `MovieEntity` where page = :page")
    suspend fun getMoviesByPage(page: Long): List<MovieEntity>?
}