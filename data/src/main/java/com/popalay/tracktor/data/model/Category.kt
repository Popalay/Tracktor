package com.popalay.tracktor.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class Category(
    @PrimaryKey val categoryId: String,
    val name: String
) {
    companion object {
        val All = Category("", "All")

        fun defaultList() = listOf(
            Category("0", "Education"),
            Category("1", "Entertainment"),
            Category("2", "Other"),
            Category("3", "Sport"),
            Category("4", "Well-being")
        )
    }
}