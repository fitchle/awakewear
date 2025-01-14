package com.fitchle.android.awakewear.presentation.states

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Vibrator
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


object AlarmState {
    var playing: Boolean = false;

    var mediaPlayer: MediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
        )

        isLooping = true
    }

    private val _alarmStateFlow = MutableStateFlow<Boolean?>(null)
    val alarmStateFlow = _alarmStateFlow.asStateFlow()

    fun setPlayingg(playing: Boolean) {
        this.playing = playing;
        _alarmStateFlow.update { playing }
    }

    fun playSound(context: Context) {
        val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        mediaPlayer.setDataSource(context, notification);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    fun stopSound() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release();
        }
    }
}