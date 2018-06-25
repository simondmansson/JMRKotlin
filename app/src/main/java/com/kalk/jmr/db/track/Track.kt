package com.kalk.jmr.db.track
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tracks",
        indices = [
                Index(value = ["uri"], unique = true)
        ]
)
data class Track(
        @PrimaryKey
        val uri:String,
        val name:String
)