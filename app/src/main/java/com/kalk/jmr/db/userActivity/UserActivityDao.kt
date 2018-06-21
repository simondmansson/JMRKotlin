package com.kalk.jmr.db.userActivity

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface UserActivityDao {

    @Query("Select * from userActivities where id = :id")
    fun byId(id: Int): UserActivity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addActivity(activity: UserActivity)
}