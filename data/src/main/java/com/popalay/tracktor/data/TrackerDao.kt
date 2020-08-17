package com.popalay.tracktor.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.popalay.tracktor.data.model.Tracker
import com.popalay.tracktor.data.model.TrackerWithRecords
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackerDao {
    @Transaction
    @Query("SELECT * FROM tracker ORDER by date DESC")
    fun getAllTrackersWithRecords(): Flow<List<TrackerWithRecords>>

    @Transaction
    @Query("SELECT * FROM tracker WHERE id=:id")
    fun getTrackerWithRecordsById(id: String): Flow<TrackerWithRecords>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(value: Tracker)

    @Delete
    suspend fun delete(value: Tracker)
}