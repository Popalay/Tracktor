package com.popalay.tracktor.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    primaryKeys = ["id", "categoryId"],
    indices = [Index("categoryId"), Index("id")],
    foreignKeys = [
        ForeignKey(
            entity = Tracker::class,
            parentColumns = ["id"],
            childColumns = ["id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TrackerCategoryCrossRef(
    val id: String,
    val categoryId: String
)