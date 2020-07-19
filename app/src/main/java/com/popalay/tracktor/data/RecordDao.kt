package com.popalay.tracktor.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.popalay.tracktor.model.ValueRecord

@Dao
interface RecordDao {
    @Insert
    suspend fun insert(value: ValueRecord)

    @Query("SELECT * FROM ValueRecord WHERE trackerId=:trackerId")
    suspend fun getAllByTrackerId(trackerId: String): List<ValueRecord>
}