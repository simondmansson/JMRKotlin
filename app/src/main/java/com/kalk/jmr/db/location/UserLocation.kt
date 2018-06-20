package com.kalk.jmr.db.location

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "locations",
        indices = [
            Index(value = ["id"], unique = true),
            Index(value = ["longitude", "latitude"], unique = true)
        ]
)
data class UserLocation(
        @PrimaryKey
        val id: Int,
        @Embedded val coordinates: Coordinates)