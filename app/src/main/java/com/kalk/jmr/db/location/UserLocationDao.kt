package com.kalk.jmr.db.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserLocationDao {

    @Query("Select * from locations where id = :id")
    fun byId(id: Int): UserLocation

    @Insert
    fun addLocation(location: UserLocation)
}