package com.fitchle.android.awakewear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun AwakeWearTheme(
        content: @Composable () -> Unit
) {
    MaterialTheme(
            content = content
    )
}