package com.fitchle.android.awakewear.presentation.services

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.UserActivityInfo
import androidx.health.services.client.data.UserActivityState
import com.fitchle.android.awakewear.presentation.states.AlarmState


class SleepActivityListenerService: PassiveListenerService() {


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onUserActivityInfoReceived(info: UserActivityInfo) {
        val vm = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val userActivityState: UserActivityState = info.userActivityState

        if (userActivityState == UserActivityState.USER_ACTIVITY_ASLEEP) {
            val sharedPref = getSharedPreferences("awake_pref", Context.MODE_PRIVATE) ?: return
            val isAwakeEnabled = sharedPref.getBoolean("awake_enabled", false)
            if (!isAwakeEnabled) return
            AlarmState.setPlayingg(true)
            AlarmState.playSound(applicationContext)

            val vibrationEffect =  VibrationEffect.createWaveform(longArrayOf(0, 500, 500), intArrayOf(0, 255, 0), 0)
            vm.vibrate(CombinedVibration.createParallel(vibrationEffect));
        } else {
            AlarmState.setPlayingg(false)
            AlarmState.stopSound()
            vm.cancel()
        }
    }
}