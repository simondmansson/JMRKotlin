package com.kalk.jmr.db.userActivity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "userActivities",
        indices = [
            Index(value = ["id", "type"], unique = true),
            Index(value = ["id"], unique = true),
            Index(value = ["type"], unique = true)
        ]
)
data class UserActivity(
        @PrimaryKey
        val id:Int,
        val type:String)