package com.kalk.jmr

/**
 * Created by kalk on 3/12/18.
 */

import android.app.IntentService
import android.content.Intent
import android.util.Log

import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

import androidx.localbroadcastmanager.content.LocalBroadcastManager

import android.R.attr.name
import com.google.android.gms.location.DetectedActivity.*
import com.kalk.jmr.enums.ActivityBroadcast.*
import com.kalk.jmr.enums.ValidActivity
import com.kalk.jmr.enums.ValidActivity.*

/**
 * IntentService for handling incoming intents that are generated as a result of requesting
 * activity updates using
 * [com.google.android.gms.location.ActivityRecognitionClient.requestActivityUpdates].
 */
class ActivityRecognitionService : IntentService(TAG) {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onHandleIntent(intent: Intent?) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            broadcastActivity(result.mostProbableActivity)
        }
    }

    private fun broadcastActivity(activity: DetectedActivity) {
        Log.d(TAG, "Broadcasting " + activity.toString())
        val intent = Intent(DETECTED_ACTIVITY_BROADCAST.name)
        intent.putExtra(DETECTED_ACTIVITY.name, getValidActivity(activity.type))
        intent.putExtra(ACTIVITY_CONFIDENCE.name, activity.confidence)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun getValidActivity(type: Int): String {
        when (type) {
            DetectedActivity.IN_VEHICLE -> return ValidActivity.IN_VEHICLE.name
            DetectedActivity.ON_BICYCLE -> return ValidActivity.ON_BICYCLE.name
            DetectedActivity.ON_FOOT -> return ValidActivity.ON_FOOT.name
            DetectedActivity.RUNNING -> return ValidActivity.RUNNING.name
            DetectedActivity.STILL -> return ValidActivity.STILL.name
            DetectedActivity.WALKING -> return ValidActivity.WALKING.name
            else -> return ValidActivity.UNKNOWN.name
        }
    }

    companion object {
        private val TAG = ActivityRecognitionService::class.java.simpleName
    }
}
