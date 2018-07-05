package com.kalk.jmr.db

import android.util.Log
import androidx.work.Worker
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.kalk.jmr.db.genre.Genre
import com.kalk.jmr.db.userActivity.UserActivity

class DatabaseSeedWorker: Worker() {
    private val TAG = DatabaseSeedWorker::class.java.simpleName

    override fun doWork(): WorkerResult {
        var jsonReader: JsonReader? = null

        return try {
            val genreInputStream = applicationContext.assets.open("spotify-genres.json")
            jsonReader = JsonReader(genreInputStream.reader())
            val genreList: List<String> = Gson().fromJson(jsonReader, List::class.java)
            val database = AppDatabase.getInstance(applicationContext)
            val genreDao =  database.genreDao()
            genreList.forEachIndexed { index, genre ->
                genreDao.addGenre(Genre(index, genre = genre))
            }

            val actvitesInputStream = applicationContext.assets.open("activites.json")
            jsonReader = JsonReader(actvitesInputStream.reader())
            val userActivivites:Array<UserActivity> = Gson().fromJson(jsonReader, Array<UserActivity>::class.java)
            val asList = userActivivites.toList()
            val activityDao = database.userActivityDao()
            asList.forEach {
                activityDao.addActivity(it)
            }
            WorkerResult.SUCCESS
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            WorkerResult.FAILURE
        } finally {
            jsonReader?.close()
        }
    }
}
