package com.kalk.jmr.db.genre

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "genres")
data class Genre(
        @PrimaryKey
        val id: Int,
        val genre:String
)
