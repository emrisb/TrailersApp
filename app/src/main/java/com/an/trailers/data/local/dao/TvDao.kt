package com.an.trailers.data.local.dao

import androidx.room.*
import com.an.trailers.data.local.entity.TvEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TvDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvList(tvEntities: List<TvEntity>): LongArray

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTv(tvEntity: TvEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTv(tvEntity: TvEntity): Int

    @Query("SELECT * FROM `TvEntity` where id = :id")
    suspend fun getTvById(id: Long?): TvEntity?

    @Query("SELECT * FROM `TvEntity` where id = :id")
    fun getTvDetailById(id: Long?): Flow<TvEntity>

    @Query("SELECT * FROM `TvEntity` where page = :page")
    suspend fun getTvsByPage(page: Long): List<TvEntity>?
}
