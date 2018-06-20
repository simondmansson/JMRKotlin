package com.kalk.jmr.db.track

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tracks",
        indices = [
                Index(value = ["id"], unique = true),
                Index(value = ["uri"], unique = true)
        ]
)
data class Track(
        @PrimaryKey
        val id: Int,
        val uri:String,
        val title:String
)