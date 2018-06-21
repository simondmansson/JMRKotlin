package com.kalk.jmr

import com.kalk.jmr.db.AppDatabase
import com.kalk.jmr.db.userActivity.UserActivity
import com.kalk.jmr.db.userActivity.UserActivityDao
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

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

    @Test
    fun will_not_add_activity_if_activity_already_exists() {
        val activity = UserActivity(1, "Running")
        val activity2 = UserActivity(2, "Running")
        dao.addActivity(activity)
        dao.addActivity(activity2)
        val queryResult = dao.byId(activity.id)
        assertEquals(activity.id, queryResult.id)
        assertEquals(activity.type, queryResult.type)
        val queryResult2 = dao.byId(activity2.id)
        assertEquals(null, queryResult2)
    }
}