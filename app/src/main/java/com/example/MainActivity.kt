package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.P3Background
import com.example.ui.components.P3CutShape
import com.example.ui.components.P3Dialog
import com.example.ui.screens.ControlsSettingsScreen
import com.example.ui.screens.EmulatorStatusScreen
import com.example.ui.screens.GameLibraryScreen
import com.example.ui.screens.GeneralSettingsScreen
import com.example.ui.screens.GraphicsSettingsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.P3DeepNavy
import com.example.ui.theme.P3ElectricCyan
import com.example.ui.theme.P3IceBlue
import com.example.ui.theme.Persona3FESTheme
import com.example.ui.theme.P3SilverGrey
import com.example.ui.theme.P3SurfaceNavy
import com.example.ui.theme.P3TartarusDark
import com.example.ui.theme.P3VoidBlack
import com.example.viewmodel.LauncherScreen
import com.example.viewmodel.LauncherViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: LauncherViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      Persona3FESTheme {
        val currentScreen by viewModel.currentScreen.collectAsState()
        val settings by viewModel.settings.collectAsState()
        val games by viewModel.games.collectAsState()
        val selectedGame by viewModel.selectedGame.collectAsState()
        val coreStatus by viewModel.coreStatus.collectAsState()
        val dialogState by viewModel.dialogState.collectAsState()

        // Handle Android hardware/gesture back press
        BackHandler(enabled = currentScreen != LauncherScreen.Home) {
          viewModel.navigateTo(LauncherScreen.Home)
        }

        P3Background {
          Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
              if (currentScreen != LauncherScreen.EmulatorStatus) {
                P3BottomNavigationBar(
                  currentScreen = currentScreen,
                  onSelectScreen = { viewModel.navigateTo(it) }
                )
              }
            },
            modifier = Modifier
              .fillMaxSize()
              .statusBarsPadding()
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            ) {
              AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                  (slideInHorizontally { width -> width / 4 } + fadeIn())
                    .togetherWith(slideOutHorizontally { width -> -width / 4 } + fadeOut())
                },
                label = "screen_transition"
              ) { screen ->
                when (screen) {
                  is LauncherScreen.Home -> {
                    HomeScreen(
                      selectedGame = selectedGame,
                      settings = settings,
                      isEmulatorConnected = coreStatus.isConnected,
                      onStartGame = {
                        viewModel.startGame { intent ->
                          try {
                            startActivity(intent)
                          } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, "Erro ao iniciar: ${e.message}", Toast.LENGTH_LONG).show()
                          }
                        }
                      },
                      onOpenLibrary = { viewModel.navigateTo(LauncherScreen.Library) },
                      onOpenSettings = { viewModel.navigateTo(LauncherScreen.Settings) },
                      onOpenPerformanceProfiles = { viewModel.navigateTo(LauncherScreen.Settings) }
                    )
                  }

                  is LauncherScreen.Library -> {
                    GameLibraryScreen(
                      games = games,
                      selectedGame = selectedGame,
                      onImportUri = { viewModel.importGame(it) },
                      onSelectGame = { viewModel.selectGame(it) },
                      onSetMainPersona3 = { viewModel.setAsMainPersona3(it) },
                      onRemoveGame = { viewModel.removeGame(it) },
                      onRestoreDefault = { viewModel.restoreDefaultP3FES() }
                    )
                  }

                  is LauncherScreen.Graphics -> {
                    GraphicsSettingsScreen(
                      settings = settings,
                      onUpdateSettings = { viewModel.updateSettings(it) }
                    )
                  }

                  is LauncherScreen.Controls -> {
                    ControlsSettingsScreen(
                      settings = settings,
                      onUpdateSettings = { viewModel.updateSettings(it) }
                    )
                  }

                  is LauncherScreen.Settings -> {
                    GeneralSettingsScreen(
                      settings = settings,
                      coreStatus = coreStatus,
                      onUpdateSettings = { viewModel.updateSettings(it) },
                      onResetPreferences = { viewModel.resetSettings() },
                      onCheckEmulatorConnection = { viewModel.checkEmulatorConnection() },
                      onNavigateToEmulatorStatus = { viewModel.navigateTo(LauncherScreen.EmulatorStatus) }
                    )
                  }

                  is LauncherScreen.EmulatorStatus -> {
                    EmulatorStatusScreen(
                      coreStatus = coreStatus,
                      onCheckConnection = { viewModel.checkEmulatorConnection() },
                      onBack = { viewModel.navigateTo(LauncherScreen.Settings) }
                    )
                  }
                }
              }
            }
          }

          // Global Persona 3 FES Styled Alert Dialog
          P3Dialog(
            show = dialogState.isVisible,
            onDismiss = { viewModel.dismissDialog() },
            title = dialogState.title,
            message = dialogState.message,
            confirmButtonText = dialogState.confirmButtonText,
            onConfirm = { viewModel.dismissDialog() },
            isWarning = dialogState.isWarning
          )
        }
      }
    }
  }
}

data class NavItem(
  val label: String,
  val icon: ImageVector,
  val screen: LauncherScreen
)

@Composable
fun P3BottomNavigationBar(
  currentScreen: LauncherScreen,
  onSelectScreen: (LauncherScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavItem("HOME", Icons.Default.Home, LauncherScreen.Home),
    NavItem("LIBRARY", Icons.Default.Folder, LauncherScreen.Library),
    NavItem("GRAPHICS", Icons.Default.Tune, LauncherScreen.Graphics),
    NavItem("CONTROLS", Icons.Default.Gamepad, LauncherScreen.Controls),
    NavItem("SETTINGS", Icons.Default.Settings, LauncherScreen.Settings)
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .windowInsetsPadding(WindowInsets.navigationBars)
      .background(P3VoidBlack.copy(alpha = 0.95f))
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          listOf(P3ElectricCyan.copy(alpha = 0.5f), Color.Transparent)
        ),
        shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
      )
      .padding(vertical = 6.dp, horizontal = 4.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceAround,
      verticalAlignment = Alignment.CenterVertically
    ) {
      items.forEach { item ->
        val isSelected = currentScreen == item.screen
        val activeColor = P3ElectricCyan
        val inactiveColor = P3SilverGrey

        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(6.dp))
            .clickable { onSelectScreen(item.screen) }
            .padding(vertical = 6.dp)
            .testTag("nav_${item.label.lowercase()}")
        ) {
          // Pill indicator for active item
          Box(
            modifier = Modifier
              .size(width = 36.dp, height = 24.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) P3SurfaceNavy else Color.Transparent),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = item.icon,
              contentDescription = item.label,
              tint = if (isSelected) activeColor else inactiveColor,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.height(2.dp))

          Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
              letterSpacing = 0.5.sp
            ),
            color = if (isSelected) activeColor else inactiveColor,
            fontSize = 9.sp
          )
        }
      }
    }
  }
}
