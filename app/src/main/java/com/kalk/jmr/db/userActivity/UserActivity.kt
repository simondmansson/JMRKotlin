package com.kalk.jmr.db.userActivity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userActivities")
data class UserActivity(
        @PrimaryKey
        val id:Int,
        val type:String)