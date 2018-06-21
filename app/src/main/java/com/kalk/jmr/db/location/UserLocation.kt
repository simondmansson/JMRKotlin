package com.kalk.jmr.db.location
import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

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