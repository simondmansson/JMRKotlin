package com.kalk.jmr.enums

import com.kalk.jmr.db.userActivity.UserActivity

/**
 * Created by kalk on 3/27/18.
 * The subset of activitys we use from googleActivity recognition.
 */

enum class ValidActivity(val id: Int, val title:String) {
    IN_VEHICLE(1, "in vehicle"),
    ON_BICYCLE(2, "on bicycle"),
    ON_FOOT(3, "on foot"),
    WALKING(4, "walking"),
    RUNNING(5, "running"),
    STILL(6, "still"),
    UNKNOWN(7, "unknown")

}

fun validAcitvityBuilder(validActivity: String): UserActivity {
    when(validActivity) {
        ValidActivity.IN_VEHICLE.name -> return UserActivity(ValidActivity.IN_VEHICLE.id, ValidActivity.IN_VEHICLE.title)
        ValidActivity.ON_BICYCLE.name -> return UserActivity(ValidActivity.ON_BICYCLE.id, ValidActivity.ON_BICYCLE.title)
        ValidActivity.ON_FOOT.name -> return UserActivity(ValidActivity.ON_FOOT.id, ValidActivity.ON_FOOT.title)
        ValidActivity.WALKING.name -> return UserActivity(ValidActivity.WALKING.id, ValidActivity.WALKING.title)
        ValidActivity.RUNNING.name-> return UserActivity(ValidActivity.RUNNING.id, ValidActivity.RUNNING.title)
        else -> return UserActivity(ValidActivity.STILL.id, ValidActivity.STILL.title)
    }
}

