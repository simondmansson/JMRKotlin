package com.kalk.jmr

import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.userActivity.UserActivity
import com.kalk.jmr.db.userActivity.UserActivityDao
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserActivityDaoTests {
    private lateinit var db: AppDatabase
    private lateinit var dao: UserActivityDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = db.userActivityDao()
    }

    @Test
    fun adds_an_activity_to_the_db() {
        val activity = UserActivity(1, "Running")
        dao.addActivity(activity)
        val queryResult = dao.byId(activity.id)
        assertEquals(queryResult.id, activity.id)
        assertEquals(queryResult.type, activity.type)
    }
}