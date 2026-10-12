package com.example.model

enum class ResolutionOption(val label: String, val scale: String, val description: String) {
  NATIVE_1X("Native / 1x", "480p", "Resolução nativa original do PS2 (640x448)"),
  NATIVE_2X("2x Native", "720p HD", "Equilíbrio ideal para telas HD de smartphones"),
  NATIVE_3X("3x Native", "1080p FHD", "Visual extremamente nítido em telas Full HD"),
  NATIVE_4X("4x Native", "1440p QHD", "Máxima fidelidade para chips gráficos de ponta"),
  CUSTOM("Custom", "Personalizado", "Multiplicador de resolução livre definido pelo usuário")
}

enum class RendererOption(val label: String, val apiName: String, val isAvailableOnDevice: Boolean, val note: String) {
  DEFAULT("Renderer Padrão", "Auto Core", true, "Gerenciado dinamicamente pelo núcleo do emulador"),
  VULKAN("Vulkan", "VK 1.3", true, "Menor sobrecarga de CPU e alto desempenho moderno"),
  OPENGL("OpenGL ES", "GLES 3.2", true, "Compatibilidade tradicional ampla com shaders legados")
}

enum class FrameRateOption(val label: String, val targetFps: String) {
  AUTO("Auto / Default", "60 / 59.94 Hz"),
  FPS_30("30 FPS", "Economia e estabilidade"),
  FPS_60("60 FPS", "Fluidez máxima de emulação")
}

enum class AspectRatioOption(val label: String, val description: String) {
  ORIGINAL_4_3("Original 4:3", "Proporção clássica de TV de tubo sem distorções"),
  STRETCH_SCREEN("Stretch to Screen", "Estica para preencher a tela inteira do celular"),
  FIT_SCREEN("Fit Screen (Letterbox)", "Ajusta à tela preservando proporção com barras laterais")
}

enum class TextureFilterOption(val label: String, val description: String) {
  OFF("Off (Pixel Art)", "Sem filtragem, fidelidade crua de textura"),
  BILINEAR("Bilinear", "Suavização padrão de bordas de texturas PS2"),
  TRILINEAR("Trilinear", "Filtragem mipmap avançada para superfícies distantes")
}

enum class PostProcessOption(val label: String, val description: String) {
  NONE("Nenhum", "Renderização direta sem shaders secundários"),
  FXAA("Anti-Aliasing (FXAA)", "Suavização rápida de serrilhados poligonais"),
  CRT_SCANLINES("CRT Scanlines", "Simulação nostálgica de linhas de varredura"),
  DARK_HOUR_BLOOM("P3 Dark Hour Bloom", "Brilho atmosférico em fontes de luz e névoa azul")
}

enum class PerformanceProfile(val label: String, val subtitle: String, val description: String, val badgeColorHex: Long) {
  POWER_SAVING(
    "Power Saving",
    "Economia de Energia",
    "Perfil destinado a reduzir o consumo de energia e preservar bateria em sessões longas.",
    0xFF00E676
  ),
  BALANCED(
    "Balanced",
    "Equilibrado",
    "Perfil equilibrado para uso cotidiano, combinando boa taxa de quadros e estabilidade térmica.",
    0xFF00E5FF
  ),
  HIGH_PERFORMANCE(
    "High Performance",
    "Alto Desempenho",
    "Perfil destinado a priorizar taxa de quadros máxima e resolução aprimorada para dispositivos potentes.",
    0xFFFFD600
  )
}

enum class ControlsLayout(val label: String, val description: String) {
  STANDARD_PS2("DualShock 2 Padrão", "Disposição autêntica de controle clássico de PS2"),
  ERGONOMIC("Ergonômico Mobile", "Botoeira inclinada para pegada confortável com dois polegares"),
  COMPACT("Compacto", "Área reduzida para máxima visibilidade do campo de jogo")
}

enum class ControlInputMode(val label: String, val description: String) {
  VIRTUAL_TOUCH("Controles Virtuais na Tela", "Sobreposição tátil na tela de toque"),
  PHYSICAL_GAMEPAD("Controle Físico (Bluetooth / USB)", "Controle externo DualShock, DualSense, Xbox ou telescopic"),
  HYBRID("Modo Híbrido", "Controles táteis discretos com suporte a gamepad simultâneo")
}

enum class AppLanguage(val label: String, val code: String) {
  PORTUGUESE("Português (Brasil)", "pt-BR"),
  ENGLISH("English (US)", "en-US"),
  JAPANESE("日本語 (Japanese)", "ja-JP")
}

enum class LauncherTheme(val label: String, val description: String) {
  DARK_HOUR("Dark Hour Cyan", "Azul profundo com neon ciano e brilho elétrico"),
  TARTARUS_INDIGO("Tartarus Midnight", "Preto estelar com azul marinho e detalhes dourados S.E.E.S."),
  VELVET_BLUE("Velvet Room Indigo", "Índigo suntuoso e detalhes em prata evoker")
}

data class LauncherSettings(
  // Graphics
  val resolution: ResolutionOption = ResolutionOption.NATIVE_2X,
  val customScale: Float = 2.5f,
  val renderer: RendererOption = RendererOption.VULKAN,
  val frameRate: FrameRateOption = FrameRateOption.AUTO,
  val aspectRatio: AspectRatioOption = AspectRatioOption.ORIGINAL_4_3,
  val textureFiltering: TextureFilterOption = TextureFilterOption.BILINEAR,
  val postProcessing: PostProcessOption = PostProcessOption.DARK_HOUR_BLOOM,

  // Performance Profile
  val performanceProfile: PerformanceProfile = PerformanceProfile.BALANCED,

  // Controls
  val controlsLayout: ControlsLayout = ControlsLayout.STANDARD_PS2,
  val showVirtualButtons: Boolean = true,
  val buttonSizePercent: Int = 100, // 50..150
  val buttonOpacityPercent: Int = 85, // 20..100
  val inputMode: ControlInputMode = ControlInputMode.VIRTUAL_TOUCH,
  val vibrationEnabled: Boolean = true,
  val stickSensitivity: Float = 1.0f, // 0.5f..2.0f

  // General
  val language: AppLanguage = AppLanguage.PORTUGUESE,
  val theme: LauncherTheme = LauncherTheme.DARK_HOUR,
  val launchLastSelectedGame: Boolean = true,
  val confirmBeforeExit: Boolean = true
)
