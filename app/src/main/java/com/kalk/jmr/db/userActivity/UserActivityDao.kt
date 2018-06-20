package com.kalk.jmr.db.userActivity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserActivityDao {

    @Query("Select * from userActivities where id = :id")
    fun byId(id: Int): UserActivity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addActivity(activity: UserActivity)
}