package com.kalk.jmr.db.location

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserLocationDao {

    @Query("Select * from locations where id = :id")
    fun byId(id: Int): UserLocation

    @Query("Select * from locations where longitude between :longFrom and  :longTo and latitude between :latFrom and :latTo")
    fun inRangeOfCoordinates(longFrom: Double, latFrom:Double, longTo: Double, latTo:Double): UserLocation

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addLocation(location: UserLocation)
}