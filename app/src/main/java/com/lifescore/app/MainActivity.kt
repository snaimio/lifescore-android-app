package com.lifescore.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.lifescore.app.core.designsystem.AppThemeMode
import com.lifescore.app.core.designsystem.LifeScoreTheme
import com.lifescore.app.core.designsystem.rememberThemeManager
import com.lifescore.app.presentation.navigation.LifeScoreNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as LifeScoreApp

        setContent {
            val themeManager = rememberThemeManager()
            val themeMode by themeManager.themeMode.collectAsState()
            val isSystemDark = isSystemInDarkTheme()

            val isDarkTheme = when (themeMode) {
                AppThemeMode.LIGHT -> false // ✅ Default Light Mode
                AppThemeMode.DARK -> true   // ✅ AMOLED True Black
                AppThemeMode.SYSTEM -> isSystemDark
            }

            LifeScoreTheme(darkTheme = isDarkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    LifeScoreNavGraph(
                        navController = navController,
                        app = app
                    )
                }
            }
        }
    }
}
