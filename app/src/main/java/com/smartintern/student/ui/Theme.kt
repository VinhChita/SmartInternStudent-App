package com.smartintern.student.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Blue = Color(0xFF1565C0)
val Bg = Color(0xFFF4F6FB)
val Ink = Color(0xFF1E293B)
val Muted = Color(0xFF64748B)
val Line = Color(0xFFE2E8F0)
val Green = Color(0xFF2E7D32)
val Teal = Color(0xFF00897B)
val Orange = Color(0xFFE65100)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(primary = Blue, background = Bg, surface = Color.White, onSurface = Ink), content = content)
}
