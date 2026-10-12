package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppLanguage
import com.example.model.AspectRatioOption
import com.example.model.ControlInputMode
import com.example.model.ControlsLayout
import com.example.model.FrameRateOption
import com.example.model.LauncherSettings
import com.example.model.LauncherTheme
import com.example.model.PerformanceProfile
import com.example.model.PostProcessOption
import com.example.model.RendererOption
import com.example.model.ResolutionOption
import com.example.model.TextureFilterOption
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("p3fes_launcher_settings", Context.MODE_PRIVATE)

  private val _settings = MutableStateFlow(loadSettings())
  val settings: StateFlow<LauncherSettings> = _settings.asStateFlow()

  private fun loadSettings(): LauncherSettings {
    val resName = prefs.getString("resolution", ResolutionOption.NATIVE_2X.name)
    val resolution = runCatching { ResolutionOption.valueOf(resName ?: "") }.getOrDefault(ResolutionOption.NATIVE_2X)

    val rendName = prefs.getString("renderer", RendererOption.VULKAN.name)
    val renderer = runCatching { RendererOption.valueOf(rendName ?: "") }.getOrDefault(RendererOption.VULKAN)

    val fpsName = prefs.getString("framerate", FrameRateOption.AUTO.name)
    val frameRate = runCatching { FrameRateOption.valueOf(fpsName ?: "") }.getOrDefault(FrameRateOption.AUTO)

    val arName = prefs.getString("aspect_ratio", AspectRatioOption.ORIGINAL_4_3.name)
    val aspectRatio = runCatching { AspectRatioOption.valueOf(arName ?: "") }.getOrDefault(AspectRatioOption.ORIGINAL_4_3)

    val tfName = prefs.getString("texture_filter", TextureFilterOption.BILINEAR.name)
    val textureFiltering = runCatching { TextureFilterOption.valueOf(tfName ?: "") }.getOrDefault(TextureFilterOption.BILINEAR)

    val ppName = prefs.getString("post_process", PostProcessOption.DARK_HOUR_BLOOM.name)
    val postProcessing = runCatching { PostProcessOption.valueOf(ppName ?: "") }.getOrDefault(PostProcessOption.DARK_HOUR_BLOOM)

    val profName = prefs.getString("perf_profile", PerformanceProfile.BALANCED.name)
    val performanceProfile = runCatching { PerformanceProfile.valueOf(profName ?: "") }.getOrDefault(PerformanceProfile.BALANCED)

    val layoutName = prefs.getString("ctrl_layout", ControlsLayout.STANDARD_PS2.name)
    val controlsLayout = runCatching { ControlsLayout.valueOf(layoutName ?: "") }.getOrDefault(ControlsLayout.STANDARD_PS2)

    val inputModeName = prefs.getString("ctrl_input_mode", ControlInputMode.VIRTUAL_TOUCH.name)
    val inputMode = runCatching { ControlInputMode.valueOf(inputModeName ?: "") }.getOrDefault(ControlInputMode.VIRTUAL_TOUCH)

    val langName = prefs.getString("language", AppLanguage.PORTUGUESE.name)
    val language = runCatching { AppLanguage.valueOf(langName ?: "") }.getOrDefault(AppLanguage.PORTUGUESE)

    val themeName = prefs.getString("theme", LauncherTheme.DARK_HOUR.name)
    val theme = runCatching { LauncherTheme.valueOf(themeName ?: "") }.getOrDefault(LauncherTheme.DARK_HOUR)

    return LauncherSettings(
      resolution = resolution,
      customScale = prefs.getFloat("custom_scale", 2.5f),
      renderer = renderer,
      frameRate = frameRate,
      aspectRatio = aspectRatio,
      textureFiltering = textureFiltering,
      postProcessing = postProcessing,
      performanceProfile = performanceProfile,
      controlsLayout = controlsLayout,
      showVirtualButtons = prefs.getBoolean("show_virtual_buttons", true),
      buttonSizePercent = prefs.getInt("button_size", 100),
      buttonOpacityPercent = prefs.getInt("button_opacity", 85),
      inputMode = inputMode,
      vibrationEnabled = prefs.getBoolean("vibration_enabled", true),
      stickSensitivity = prefs.getFloat("stick_sensitivity", 1.0f),
      language = language,
      theme = theme,
      launchLastSelectedGame = prefs.getBoolean("launch_last_game", true),
      confirmBeforeExit = prefs.getBoolean("confirm_exit", true)
    )
  }

  fun updateSettings(newSettings: LauncherSettings) {
    _settings.value = newSettings
    prefs.edit()
      .putString("resolution", newSettings.resolution.name)
      .putFloat("custom_scale", newSettings.customScale)
      .putString("renderer", newSettings.renderer.name)
      .putString("framerate", newSettings.frameRate.name)
      .putString("aspect_ratio", newSettings.aspectRatio.name)
      .putString("texture_filter", newSettings.textureFiltering.name)
      .putString("post_process", newSettings.postProcessing.name)
      .putString("perf_profile", newSettings.performanceProfile.name)
      .putString("ctrl_layout", newSettings.controlsLayout.name)
      .putBoolean("show_virtual_buttons", newSettings.showVirtualButtons)
      .putInt("button_size", newSettings.buttonSizePercent)
      .putInt("button_opacity", newSettings.buttonOpacityPercent)
      .putString("ctrl_input_mode", newSettings.inputMode.name)
      .putBoolean("vibration_enabled", newSettings.vibrationEnabled)
      .putFloat("stick_sensitivity", newSettings.stickSensitivity)
      .putString("language", newSettings.language.name)
      .putString("theme", newSettings.theme.name)
      .putBoolean("launch_last_game", newSettings.launchLastSelectedGame)
      .putBoolean("confirm_exit", newSettings.confirmBeforeExit)
      .apply()
  }

  fun updatePerformanceProfile(profile: PerformanceProfile) {
    val updated = _settings.value.copy(performanceProfile = profile)
    updateSettings(updated)
  }

  fun resetToDefaults() {
    prefs.edit().clear().apply()
    _settings.value = LauncherSettings()
  }
}
