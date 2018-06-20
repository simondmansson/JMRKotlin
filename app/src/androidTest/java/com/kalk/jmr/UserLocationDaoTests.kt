package com.kalk.jmr

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.location.Coordinates
import com.kalk.jmr.db.location.UserLocation
import com.kalk.jmr.db.location.UserLocationDao
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserLocationDaoTests {
    private lateinit var db: AppDatabase
    private lateinit var dao: UserLocationDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = db.locationDao()
    }

    @Test
    fun location_is_added_to_database() {
        val location = UserLocation(1, Coordinates(1.0, 2.0))
        dao.addLocation(location)
        val queryResult = dao.byId(1)
        assertEquals(queryResult.id, location.id)
        assertEquals(0, queryResult.coordinates.longitude.compareTo(location.coordinates.longitude))
        assertEquals(0, queryResult.coordinates.latitude.compareTo(location.coordinates.latitude))
    }

    @Test
    fun will_not_add_location_with_same_coordinates_twice_to_db() {
        val loc1 = UserLocation(1, Coordinates(1.0, 2.0))
        val loc2 = UserLocation(2, Coordinates(1.0, 2.0))
        dao.addLocation(loc1)
        dao.addLocation(loc2)
        val queryResult = dao.byId(1)
        assertEquals(queryResult.id, loc1.id)
        assertEquals(0, queryResult.coordinates.longitude.compareTo(loc1.coordinates.longitude))
        assertEquals(0, queryResult.coordinates.latitude.compareTo(loc1.coordinates.latitude))
        val queryResult2 = dao.byId(2)
        assertEquals(queryResult2, null)
    }

}