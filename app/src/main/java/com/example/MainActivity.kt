package com.example

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.WindowInsets as ComposeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()

        setContent {
            MyApplicationTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

                BackHandler(enabled = currentScreen != AppScreen.HOME) {
                    currentScreen = AppScreen.HOME
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = ComposeWindowInsets.safeDrawing
                ) { innerPadding ->
                    AnimatedContent(
                        targetState = currentScreen,
                        label = "screen_transition",
                        modifier = Modifier.padding(innerPadding)
                    ) { screen ->
                        when (screen) {
                            AppScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onNavigate = { next -> currentScreen = next }
                            )
                            AppScreen.MATH_LAB -> MathLabScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.HOME }
                            )
                            AppScreen.MAGIC_MIRROR -> MagicMirrorScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.HOME }
                            )
                            AppScreen.OBLONG -> OblongScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.HOME }
                            )
                            AppScreen.WONDER_BLOCKS -> WonderBlocksScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.HOME }
                            )
                            AppScreen.ALPHABLOCKS -> AlphablocksScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.HOME }
                            )
                            AppScreen.LEADERBOARD -> LeaderboardScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.HOME }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemBars()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())
    }
}
