package com.kalk.jmr.db.location

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class UserLocation(
        @PrimaryKey
        val id: Int,
        @Embedded val coordinates: Coordinates)