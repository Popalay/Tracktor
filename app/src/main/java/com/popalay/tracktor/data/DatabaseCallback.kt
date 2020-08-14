package com.popalay.tracktor.data

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.popalay.tracktor.model.ProgressDirection
import com.popalay.tracktor.model.TrackableUnit
import com.popalay.tracktor.model.Tracker
import com.popalay.tracktor.model.ValueRecord
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.get
import java.time.LocalDateTime
import java.util.concurrent.Executors

class DatabaseCallback : RoomDatabase.Callback(), KoinComponent {
    override fun onCreate(db: SupportSQLiteDatabase) {
        Executors.newSingleThreadScheduledExecutor().execute {
            runBlocking {
                get<TrackerDao>().insert(
                    Tracker(
                        "id",
                        "Number of cool app installs",
                        TrackableUnit.Quantity,
                        ProgressDirection.ASCENDING,
                        LocalDateTime.now()
                    )
                )
                get<RecordDao>().insert(ValueRecord("id", "id", 42.0, "", LocalDateTime.now()))
            }
        }
    }
}