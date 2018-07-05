package com.kalk.jmr.db.location
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface UserLocationDao {

    @Query("Select * from locations where id = :id")
    fun byId(id: String): UserLocation

    @Query("Select * from locations where longitude between :longFrom and  :longTo and latitude between :latFrom and :latTo")
    fun inRangeOfCoordinates(longFrom: Double, latFrom:Double, longTo: Double, latTo:Double): UserLocation

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addLocation(location: UserLocation)
}