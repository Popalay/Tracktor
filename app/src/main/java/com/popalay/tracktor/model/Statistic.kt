package com.popalay.tracktor.model

import com.popalay.tracktor.model.ProgressDirection.ASCENDING
import com.popalay.tracktor.model.ProgressDirection.DESCENDING
import java.time.LocalDateTime
import java.time.ZoneOffset

data class Statistic(
    val trackerCount: Int,
    val trackersWithProgress: Int,
    val lastUpdatedTracker: Tracker,
    val lastUpdatedTrackerDate: LocalDateTime,
    val mostProgressiveTracker: Tracker
) {
    val overallProgress: Double get() = trackersWithProgress.toDouble() / trackerCount

    companion object {
        fun generateFor(trackers: List<TrackerWithRecords>): Statistic {
            val trackerCount = trackers.size
            val trackersWithProgress = trackers.count {
                it.progress() >= 0 && it.tracker.direction == ASCENDING ||
                        it.progress() <= 0 && it.tracker.direction == DESCENDING
            }
            val lastUpdatedTracker = trackers.maxByOrNull { trackerWithRecords ->
                trackerWithRecords.records.map { it.date.toInstant(ZoneOffset.UTC).epochSecond }.maxOrNull() ?: 0
            }
            val lastUpdatedTrackerDate = lastUpdatedTracker?.let { trackerWithRecords ->
                trackerWithRecords.records.maxByOrNull { it.date.toInstant(ZoneOffset.UTC).epochSecond }?.date
            }
            val mostProgressiveTracker = trackers.maxByOrNull {
                it.progress() * if (it.tracker.direction == DESCENDING) -1 else 1
            }
            return Statistic(
                trackerCount = trackerCount,
                trackersWithProgress = trackersWithProgress,
                lastUpdatedTracker = lastUpdatedTracker!!.tracker,
                lastUpdatedTrackerDate = lastUpdatedTrackerDate ?: LocalDateTime.MIN,
                mostProgressiveTracker = mostProgressiveTracker!!.tracker
            )
        }
    }
}