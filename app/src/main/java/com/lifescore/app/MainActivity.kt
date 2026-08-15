package com.lifescore.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.lifescore.app.core.designsystem.LifeScoreTheme
import com.lifescore.app.presentation.navigation.LifeScoreNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as LifeScoreApp

        setContent {
            LifeScoreTheme {
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
