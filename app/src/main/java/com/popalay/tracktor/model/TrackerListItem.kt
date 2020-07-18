package com.popalay.tracktor.model

data class TrackerListItem(
    val data: TrackerWithRecords,
    val animate: Boolean = true
) : ListItem

fun TrackerWithRecords.toListItem() = TrackerListItem(this)