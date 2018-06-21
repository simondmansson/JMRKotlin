package com.kalk.jmr.db

import android.util.Log
import androidx.work.Worker
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.kalk.jmr.db.genre.Genre

class DatabaseSeedWorker: Worker() {
    private val TAG = DatabaseSeedWorker::class.java.simpleName

    override fun doWork(): WorkerResult {
        var jsonReader: JsonReader? = null

        return try {
            val inputStream = applicationContext.assets.open("spotify-genres.json")
            jsonReader = JsonReader(inputStream.reader())
            val genreList: List<String> = Gson().fromJson(jsonReader, List::class.java)
            val database = AppDatabase.getInstance(applicationContext)
            genreList.forEachIndexed { index, genre ->
                database.genreDao().addGenre(Genre(index, genre = genre))
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
