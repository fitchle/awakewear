package com.fitchle.android.awakewear.presentation.services

import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.UserActivityInfo
import androidx.health.services.client.data.UserActivityState


class SleepActivityListenerService: PassiveListenerService() {
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onUserActivityInfoReceived(info: UserActivityInfo) {
        val userActivityState: UserActivityState = info.userActivityState
        if (userActivityState == UserActivityState.USER_ACTIVITY_PASSIVE || userActivityState == UserActivityState.USER_ACTIVITY_UNKNOWN) {
            val sharedPref = getSharedPreferences("awake_pref", Context.MODE_PRIVATE) ?: return
            val isAwakeEnabled = sharedPref.getBoolean("awake_enabled", false)
            //if (!isAwakeEnabled) return

            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        }
    }
}