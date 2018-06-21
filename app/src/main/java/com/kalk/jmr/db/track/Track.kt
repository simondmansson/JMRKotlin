package com.kalk.jmr.db.track
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

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