package com.fitchle.android.awakewear.presentation

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.PassiveListenerConfig
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.fitchle.android.awakewear.presentation.services.SleepActivityListenerService
import com.fitchle.android.awakewear.presentation.states.AlarmState
import com.fitchle.android.awakewear.presentation.states.AwakeState
import com.fitchle.android.awakewear.presentation.theme.AwakeWearTheme

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        val sharedPref = getSharedPreferences("awake_pref", Context.MODE_PRIVATE) ?: return
        val isAwakeEnabled = sharedPref.getBoolean("awake_enabled", false)
        AwakeState.setEnabledd(isAwakeEnabled)

        if (checkSelfPermission(
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                android.Manifest.permission.SCHEDULE_EXACT_ALARM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION, android.Manifest.permission.SCHEDULE_EXACT_ALARM),
                100
            )
        }

        setContent {
            WearApp()
        }

        val healthClient = HealthServices.getClient(this /*context*/)
        val passiveMonitoringClient = healthClient.passiveMonitoringClient
        val passiveListenerConfig = PassiveListenerConfig.builder()
            .setShouldUserActivityInfoBeRequested(true)
            .build()
        passiveMonitoringClient.setPassiveListenerServiceAsync(
            SleepActivityListenerService::class.java,
            passiveListenerConfig
        )
    }

    private fun checkAndRequestActivityRecognitionPermission() {
        if (checkSelfPermission(
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                100
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()


        val sharedPref = getSharedPreferences("awake_pref", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("awake_enabled", AwakeState.enabled)
            apply()
        }
    }
}


@Composable
fun WearApp() {
    val state = AwakeState.awakeStateFlow.collectAsState();
    val text = if (state.value == true) "Stop" else "Start"

    val alarmState = AlarmState.alarmStateFlow.collectAsState();
    val alarmText = if (alarmState.value == true) "Active" else "Deactive"

    AwakeWearTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Alarm is $alarmText", fontSize = TextUnit(10f, TextUnitType.Sp))
                Box(modifier = Modifier.size(18.dp))
                RowPaddedButton(text, onClick = {
                    AwakeState.setEnabledd(!AwakeState.enabled)
                    if (!AwakeState.enabled) {
                        AlarmState.setPlayingg(false)
                        AlarmState.stopSound()
                    }
                })
            }
        }
    }
}

@Composable
fun RowPaddedButton(text: String, onClick: () -> Unit = {}) {

    Button(
        onClick = {
            onClick();
        },
        modifier = Modifier
            .wrapContentWidth()
            .defaultMinSize(100.dp, 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(99, 101, 241)),

        ) {
        Text(text = text, fontSize = TextUnit(12f, TextUnitType.Sp))
    }


}

//@Composable
//fun Greeting(greetingName: String) {
//    Text(
//            modifier = Modifier.fillMaxWidth(),
//            textAlign = TextAlign.Center,
//            color = MaterialTheme.colors.primary,
//            text = stringResource(R.string.hello_world, greetingName)
//    )
//}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}