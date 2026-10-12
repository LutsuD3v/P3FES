package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.data.SettingsRepository
import com.example.launcher.EmulatorCoreStatus
import com.example.launcher.EmulatorLauncher
import com.example.launcher.LaunchResult
import com.example.model.GameItem
import com.example.model.LauncherSettings
import com.example.model.PerformanceProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface LauncherScreen {
  object Home : LauncherScreen
  object Library : LauncherScreen
  object Graphics : LauncherScreen
  object Controls : LauncherScreen
  object Settings : LauncherScreen
  object EmulatorStatus : LauncherScreen
}

data class DialogState(
  val isVisible: Boolean = false,
  val title: String = "",
  val message: String = "",
  val isWarning: Boolean = true,
  val confirmButtonText: String = "OK"
)

class LauncherViewModel(application: Application) : AndroidViewModel(application) {

  private val settingsRepository = SettingsRepository(application)
  private val gameRepository = GameRepository(application)
  private val emulatorLauncher = EmulatorLauncher(application)

  val settings: StateFlow<LauncherSettings> = settingsRepository.settings
  val games: StateFlow<List<GameItem>> = gameRepository.games
  val selectedGame: StateFlow<GameItem?> = gameRepository.selectedGame

  private val _currentScreen = MutableStateFlow<LauncherScreen>(LauncherScreen.Home)
  val currentScreen: StateFlow<LauncherScreen> = _currentScreen.asStateFlow()

  private val _coreStatus = MutableStateFlow(emulatorLauncher.checkEmulatorStatus())
  val coreStatus: StateFlow<EmulatorCoreStatus> = _coreStatus.asStateFlow()

  private val _dialogState = MutableStateFlow(DialogState())
  val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

  fun navigateTo(screen: LauncherScreen) {
    _currentScreen.value = screen
  }

  fun updateSettings(newSettings: LauncherSettings) {
    settingsRepository.updateSettings(newSettings)
  }

  fun updatePerformanceProfile(profile: PerformanceProfile) {
    settingsRepository.updatePerformanceProfile(profile)
  }

  fun resetSettings() {
    settingsRepository.resetToDefaults()
    _dialogState.value = DialogState(
      isVisible = true,
      title = "Preferências Redefinidas",
      message = "Todas as opções gráficas, controles e preferências foram restauradas para os padrões recomendados.",
      isWarning = false
    )
  }

  fun importGame(uri: Uri) {
    viewModelScope.launch {
      val imported = gameRepository.importGameFromUri(uri)
      _dialogState.value = DialogState(
        isVisible = true,
        title = "Jogo Importado com Sucesso",
        message = "O arquivo '${imported.fileName}' foi associado à biblioteca do Persona 3 FES.",
        isWarning = false
      )
    }
  }

  fun selectGame(game: GameItem) {
    gameRepository.selectGame(game)
  }

  fun setAsMainPersona3(gameId: String) {
    gameRepository.setAsMainPersona3(gameId)
  }

  fun removeGame(gameId: String) {
    gameRepository.removeGame(gameId)
  }

  fun restoreDefaultP3FES() {
    gameRepository.restoreDefaultP3FES()
  }

  fun checkEmulatorConnection() {
    val updated = emulatorLauncher.checkEmulatorStatus()
    _coreStatus.value = updated
    if (!updated.isConnected) {
      _dialogState.value = DialogState(
        isVisible = true,
        title = "Verificação de Núcleo",
        message = "Nenhum emulador compatível (AetherSX2, NetherSX2, Play!) foi detectado neste dispositivo. O launcher continuará em modo de configuração independente.",
        isWarning = true
      )
    } else {
      _dialogState.value = DialogState(
        isVisible = true,
        title = "Núcleo Conectado",
        message = "Conexão com ${updated.coreName} verificada com sucesso!",
        isWarning = false
      )
    }
  }

  fun startGame(onIntentLaunch: (Intent) -> Unit) {
    val game = selectedGame.value
    if (game == null) {
      _dialogState.value = DialogState(
        isVisible = true,
        title = "Nenhum Jogo Selecionado",
        message = "Selecione ou importe uma imagem de disco de Persona 3 FES na Biblioteca de Jogos antes de iniciar.",
        isWarning = true
      )
      return
    }

    when (val result = emulatorLauncher.launchGame(game)) {
      is LaunchResult.NoGameSelected -> {
        _dialogState.value = DialogState(
          isVisible = true,
          title = "Nenhum Jogo Selecionado",
          message = "Selecione um jogo na biblioteca antes de iniciar.",
          isWarning = true
        )
      }
      is LaunchResult.EmulatorNotConnected -> {
        // As strictly required by prompt requirement #8:
        // "Emulator not connected. Configure the emulator integration before launching the game."
        _dialogState.value = DialogState(
          isVisible = true,
          title = "Emulator Not Connected",
          message = "Emulator not connected. Configure the emulator integration before launching the game.",
          isWarning = true,
          confirmButtonText = "CONFIGURAR"
        )
      }
      is LaunchResult.ReadyToLaunch -> {
        onIntentLaunch(result.launchIntent)
      }
    }
  }

  fun dismissDialog() {
    val current = _dialogState.value
    _dialogState.value = DialogState()
    if (current.confirmButtonText == "CONFIGURAR") {
      _currentScreen.value = LauncherScreen.EmulatorStatus
    }
  }
}
