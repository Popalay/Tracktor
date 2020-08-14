package com.popalay.tracktor.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey val categoryId: String,
    val name: String
) {
    companion object {
        fun defaultList() = listOf(
            Category("0", "Education"),
            Category("1", "Entertainment"),
            Category("2", "Other"),
            Category("3", "Sport"),
            Category("4", "Well-being")
        )
    }
}