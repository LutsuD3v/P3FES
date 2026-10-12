package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GameRepository
import com.example.data.SettingsRepository
import com.example.launcher.EmulatorLauncher
import com.example.launcher.LaunchResult
import com.example.model.PerformanceProfile
import com.example.model.ResolutionOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var context: Context
  private lateinit var settingsRepo: SettingsRepository
  private lateinit var gameRepo: GameRepository
  private lateinit var launcher: EmulatorLauncher

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext()
    settingsRepo = SettingsRepository(context)
    gameRepo = GameRepository(context)
    launcher = EmulatorLauncher(context)
  }

  @Test
  fun testAppNameResource() {
    val appName = context.getString(R.string.app_name)
    assertEquals("Persona 3 FES", appName)
  }

  @Test
  fun testSettingsPersistence() {
    val initial = settingsRepo.settings.value
    assertEquals(PerformanceProfile.BALANCED, initial.performanceProfile)

    settingsRepo.updatePerformanceProfile(PerformanceProfile.HIGH_PERFORMANCE)
    assertEquals(PerformanceProfile.HIGH_PERFORMANCE, settingsRepo.settings.value.performanceProfile)

    settingsRepo.updateSettings(
      settingsRepo.settings.value.copy(resolution = ResolutionOption.NATIVE_3X)
    )
    assertEquals(ResolutionOption.NATIVE_3X, settingsRepo.settings.value.resolution)
  }

  @Test
  fun testDefaultGameLibrary() {
    val games = gameRepo.games.value
    assertTrue("A biblioteca padrão deve conter jogos", games.isNotEmpty())
    val p3fes = games.firstOrNull { it.isMainP3FES }
    assertNotNull("Deve conter Persona 3 FES como jogo principal", p3fes)
    assertEquals("Persona 3 FES", p3fes?.title)
  }

  @Test
  fun testEmulatorLauncherDisconnectedGracefulHandling() {
    val status = launcher.checkEmulatorStatus()
    // In Robolectric environment with no real external PS2 package installed
    assertFalse(status.isConnected)

    val game = gameRepo.selectedGame.value
    val result = launcher.launchGame(game)
    assertTrue(
      "Deve retornar EmulatorNotConnected quando não houver núcleo instalado",
      result is LaunchResult.EmulatorNotConnected
    )
    if (result is LaunchResult.EmulatorNotConnected) {
      assertEquals(
        "Emulator not connected. Configure the emulator integration before launching the game.",
        result.message
      )
    }
  }
}
