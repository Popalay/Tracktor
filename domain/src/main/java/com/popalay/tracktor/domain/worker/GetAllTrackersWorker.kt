package com.popalay.tracktor.domain.worker

import com.popalay.tracktor.data.TrackingRepository
import com.popalay.tracktor.data.model.TrackerWithRecords
import com.squareup.workflow.Worker
import kotlinx.coroutines.flow.Flow

class GetAllTrackersWorker(
    private val trackingRepository: TrackingRepository
) : Worker<List<TrackerWithRecords>> {
    override fun run(): Flow<List<TrackerWithRecords>> = trackingRepository.getAllTrackersWithRecords()
}