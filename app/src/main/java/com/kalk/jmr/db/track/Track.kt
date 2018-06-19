package com.kalk.jmr.db.track

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
        @PrimaryKey
        val id: Int,
        val uri:String,
        val title:String
)