package com.lifescore.app.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object LifeScoreTypography {
    fun getTypography(): Typography {
        return Typography(
            displayLarge = TextStyle(
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.25).sp,
                lineHeight = 54.sp
            ),
            displayMedium = TextStyle(
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 44.sp
            ),
            displaySmall = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 36.sp
            ),
            headlineLarge = TextStyle(
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 32.sp
            ),
            headlineMedium = TextStyle(
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 28.sp
            ),
            headlineSmall = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.sp,
                lineHeight = 24.sp
            ),
            titleLarge = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.sp,
                lineHeight = 24.sp
            ),
            titleMedium = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.1.sp,
                lineHeight = 20.sp
            ),
            titleSmall = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.1.sp,
                lineHeight = 18.sp
            ),
            bodyLarge = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.2.sp,
                lineHeight = 20.sp
            ),
            bodyMedium = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.2.sp,
                lineHeight = 18.sp
            ),
            bodySmall = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.3.sp,
                lineHeight = 15.sp
            ),
            labelLarge = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.1.sp,
                lineHeight = 18.sp
            ),
            labelMedium = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp,
                lineHeight = 15.sp
            ),
            labelSmall = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.4.sp,
                lineHeight = 14.sp
            )
        )
    }
}
