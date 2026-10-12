package com.example.launcher

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.example.model.GameItem

sealed interface LaunchResult {
  object NoGameSelected : LaunchResult
  data class EmulatorNotConnected(
    val title: String = "Emulator not connected",
    val message: String = "Emulator not connected. Configure the emulator integration before launching the game.",
    val detectedCores: List<String> = emptyList()
  ) : LaunchResult
  data class ReadyToLaunch(
    val emulatorPackage: String,
    val launchIntent: Intent
  ) : LaunchResult
}

data class EmulatorCoreStatus(
  val isConnected: Boolean,
  val coreName: String,
  val version: String,
  val installedPackages: List<String>,
  val notes: String
)

class EmulatorLauncher(private val context: Context) {

  // Supported PS2 backend packages on Android
  private val knownEmulatorPackages = listOf(
    "xyz.aethersx2.android",
    "xyz.nethersx2.android",
    "io.play.emulator",
    "org.pcsx2.android"
  )

  fun checkEmulatorStatus(): EmulatorCoreStatus {
    val pm = context.packageManager
    val detected = mutableListOf<String>()

    for (pkg in knownEmulatorPackages) {
      try {
        pm.getPackageInfo(pkg, 0)
        detected.add(pkg)
      } catch (_: PackageManager.NameNotFoundException) {
        // Not installed
      }
    }

    return if (detected.isNotEmpty()) {
      val primary = detected.first()
      EmulatorCoreStatus(
        isConnected = true,
        coreName = when {
          primary.contains("nether") -> "NetherSX2 Core IPC"
          primary.contains("aether") -> "AetherSX2 Core IPC"
          primary.contains("play") -> "Play! Emulator Core"
          else -> "PCSX2 Core Interface"
        },
        version = "v1.5-4248 (Discovered)",
        installedPackages = detected,
        notes = "Núcleo de emulação detectado no sistema Android."
      )
    } else {
      EmulatorCoreStatus(
        isConnected = false,
        coreName = "Standalone Launcher Interface",
        version = "No Core Hook Attached",
        installedPackages = emptyList(),
        notes = "Nenhum núcleo de emulação PS2 (AetherSX2/NetherSX2/Play!) detectado no dispositivo."
      )
    }
  }

  fun launchGame(game: GameItem?): LaunchResult {
    if (game == null) {
      return LaunchResult.NoGameSelected
    }

    val status = checkEmulatorStatus()
    if (!status.isConnected) {
      return LaunchResult.EmulatorNotConnected(
        title = "Emulator Not Connected",
        message = "Emulator not connected. Configure the emulator integration before launching the game.",
        detectedCores = emptyList()
      )
    }

    val targetPackage = status.installedPackages.firstOrNull() ?: return LaunchResult.EmulatorNotConnected()

    // Construct valid launch intent for the emulator
    val intent = Intent(Intent.ACTION_VIEW).apply {
      setPackage(targetPackage)
      if (game.filePathUri != null) {
        setDataAndType(Uri.parse(game.filePathUri), "application/octet-stream")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      }
      putExtra("boot", true)
      putExtra("source", "p3fes_launcher")
      putExtra("game_id", game.serial)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    return LaunchResult.ReadyToLaunch(
      emulatorPackage = targetPackage,
      launchIntent = intent
    )
  }
}
