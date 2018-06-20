package com.kalk.jmr.db.genre

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity (tableName = "genres",
        indices = [
                Index(value = ["id"], unique = true),
                Index(value = ["genre"], unique = true)
        ]
)
data class Genre(
        @PrimaryKey
        val id: Int,
        val genre:String
)