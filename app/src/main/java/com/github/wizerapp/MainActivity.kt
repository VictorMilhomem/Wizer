package com.github.wizerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.wizerapp.screens.MainScreen
import com.github.wizerapp.ui.theme.WizerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Extract deep link data, if available (e.g., "wizerapp://quiz?id=<quizId>")
        val quizId = intent?.data?.getQueryParameter("id") ?: ""

        setContent {
            WizerAppTheme {
                MainScreen(initialQuizId = quizId)
            }
        }
    }
}
