package com.fitchle.android.awakewear.presentation

import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
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
import com.fitchle.android.awakewear.R
import com.fitchle.android.awakewear.presentation.services.SleepActivityListenerService
import com.fitchle.android.awakewear.presentation.states.AwakeState
import com.fitchle.android.awakewear.presentation.theme.AwakeWearTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp()
        }

        val sharedPref = getSharedPreferences("awake_pref", Context.MODE_PRIVATE) ?: return
        val isAwakeEnabled = sharedPref.getBoolean("awake_enabled", false)
        AwakeState.setEnabledd(isAwakeEnabled)

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

    override fun onDestroy() {
        super.onDestroy()


        val sharedPref = getSharedPreferences("awake_pref", Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putBoolean("awake_enabled", AwakeState.enabled)
            apply()
        }
    }
}



@Composable
fun WearApp() {
    val state = AwakeState.awakeStateFlow.collectAsState();
    val text = if (state.value == true) "Stop" else "Start"

    AwakeWearTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            RowPaddedButton(text, onClick = {
                AwakeState.setEnabledd(!AwakeState.enabled);

            })
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