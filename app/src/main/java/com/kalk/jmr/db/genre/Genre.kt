package com.kalk.jmr.db.genre

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "genres",
        indices = [
                Index(value = ["id"], unique = true),
                (Index(value = ["genre"], unique = true))
        ]
)
data class Genre(
        @PrimaryKey
        val id: Int,
        val genre:String
)