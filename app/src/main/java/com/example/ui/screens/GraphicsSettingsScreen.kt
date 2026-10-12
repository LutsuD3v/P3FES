package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatioOption
import com.example.model.FrameRateOption
import com.example.model.LauncherSettings
import com.example.model.PostProcessOption
import com.example.model.RendererOption
import com.example.model.ResolutionOption
import com.example.model.TextureFilterOption
import com.example.ui.components.P3AngledBadgeShape
import com.example.ui.components.P3AngledCard
import com.example.ui.components.P3Badge
import com.example.ui.components.P3CutShape
import com.example.ui.components.P3Header
import com.example.ui.theme.P3AmberAccent
import com.example.ui.theme.P3DeepNavy
import com.example.ui.theme.P3ElectricCyan
import com.example.ui.theme.P3IceBlue
import com.example.ui.theme.P3SeesYellow
import com.example.ui.theme.P3SilverGrey
import com.example.ui.theme.P3SurfaceHighlight
import com.example.ui.theme.P3SurfaceNavy
import com.example.ui.theme.P3TartarusDark
import com.example.ui.theme.P3VoidBlack

@Composable
fun GraphicsSettingsScreen(
  settings: LauncherSettings,
  onUpdateSettings: (LauncherSettings) -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Header
    P3Header(
      title = "GRAPHICS SETTINGS",
      subtitle = "Resolução, renderizador e pós-processamento"
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Notice banner: Engine status indicator
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(P3CutShape)
        .background(P3TartarusDark)
        .border(1.dp, P3AmberAccent.copy(alpha = 0.6f), P3CutShape)
        .padding(12.dp)
    ) {
      Row(verticalAlignment = Alignment.Top) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = P3AmberAccent,
          modifier = Modifier
            .size(20.dp)
            .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "STATUS DE APLICAÇÃO GRÁFICA",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = P3AmberAccent
          )
          Text(
            text = "As preferências estão salvas no Launcher. Os parâmetros serão aplicados à pipeline gráfica assim que o emulador for conectado.",
            style = MaterialTheme.typography.bodySmall,
            color = P3SilverGrey,
            lineHeight = 16.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 1. RESOLUTION
    SectionContainer(title = "RESOLUÇÃO DE RENDERIZAÇÃO", subtitle = "Multiplicador de densidade de pixels do PlayStation 2") {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ResolutionOption.entries.forEach { option ->
          val isSelected = settings.resolution == option
          SelectableCardItem(
            title = option.label,
            badge = option.scale,
            description = option.description,
            isSelected = isSelected,
            onClick = { onUpdateSettings(settings.copy(resolution = option)) }
          )
        }

        if (settings.resolution == ResolutionOption.CUSTOM) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 8.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(P3SurfaceNavy)
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Escala customizada:",
                style = MaterialTheme.typography.bodyMedium,
                color = P3IceBlue
              )
              Text(
                text = "${String.format("%.1f", settings.customScale)}x",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = P3ElectricCyan
              )
            }
            Slider(
              value = settings.customScale,
              onValueChange = { onUpdateSettings(settings.copy(customScale = it)) },
              valueRange = 1.0f..5.0f,
              steps = 7,
              colors = SliderDefaults.colors(
                thumbColor = P3ElectricCyan,
                activeTrackColor = P3ElectricCyan,
                inactiveTrackColor = P3DeepNavy
              )
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 2. RENDERER / GPU
    SectionContainer(title = "RENDERER / GPU ENGINE", subtitle = "Backend de renderização da GPU do dispositivo") {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        RendererOption.entries.forEach { option ->
          val isSelected = settings.renderer == option
          SelectableCardItem(
            title = option.label,
            badge = option.apiName,
            description = "${option.note}${if (!option.isAvailableOnDevice) " (Indisponível neste dispositivo)" else ""}",
            isSelected = isSelected,
            enabled = option.isAvailableOnDevice,
            onClick = { onUpdateSettings(settings.copy(renderer = option)) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 3. FRAME RATE
    SectionContainer(title = "FRAME RATE TARGET", subtitle = "Limite da taxa de quadros por segundo") {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FrameRateOption.entries.forEach { option ->
          val isSelected = settings.frameRate == option
          Box(
            modifier = Modifier
              .weight(1f)
              .clip(P3CutShape)
              .background(if (isSelected) P3ElectricCyan else P3TartarusDark)
              .border(1.dp, if (isSelected) P3IceBlue else P3ElectricCyan.copy(alpha = 0.4f), P3CutShape)
              .clickable { onUpdateSettings(settings.copy(frameRate = option)) }
              .padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = option.label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isSelected) P3VoidBlack else P3IceBlue
              )
              Text(
                text = option.targetFps,
                style = MaterialTheme.typography.bodySmall,
                color = if (isSelected) P3VoidBlack.copy(alpha = 0.8f) else P3SilverGrey
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 4. ASPECT RATIO
    SectionContainer(title = "ASPECT RATIO (PROPORÇÃO)", subtitle = "Geometria da tela de saída") {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AspectRatioOption.entries.forEach { option ->
          val isSelected = settings.aspectRatio == option
          SelectableCardItem(
            title = option.label,
            badge = null,
            description = option.description,
            isSelected = isSelected,
            onClick = { onUpdateSettings(settings.copy(aspectRatio = option)) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 5. TEXTURE FILTERING
    SectionContainer(title = "TEXTURE FILTERING", subtitle = "Filtragem de mapeamento de texturas poligonais") {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TextureFilterOption.entries.forEach { option ->
          val isSelected = settings.textureFiltering == option
          SelectableCardItem(
            title = option.label,
            badge = null,
            description = option.description,
            isSelected = isSelected,
            onClick = { onUpdateSettings(settings.copy(textureFiltering = option)) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 6. POST-PROCESSING
    SectionContainer(title = "POST-PROCESSING SHADERS", subtitle = "Filtros de pós-processamento de imagem") {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PostProcessOption.entries.forEach { option ->
          val isSelected = settings.postProcessing == option
          SelectableCardItem(
            title = option.label,
            badge = if (option == PostProcessOption.DARK_HOUR_BLOOM) "RECOMENDADO" else null,
            description = option.description,
            isSelected = isSelected,
            onClick = { onUpdateSettings(settings.copy(postProcessing = option)) }
          )
        }
      }
    }
  }
}

@Composable
fun SectionContainer(
  title: String,
  subtitle: String,
  content: @Composable () -> Unit
) {
  Column(modifier = Modifier.fillMaxWidth()) {
    Text(
      text = title.uppercase(),
      style = MaterialTheme.typography.titleMedium.copy(
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
      ),
      color = P3ElectricCyan
    )
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodySmall,
      color = P3SilverGrey,
      modifier = Modifier.padding(bottom = 10.dp)
    )
    content()
  }
}

@Composable
fun SelectableCardItem(
  title: String,
  badge: String?,
  description: String,
  isSelected: Boolean,
  enabled: Boolean = true,
  onClick: () -> Unit
) {
  val borderColor = if (isSelected) P3ElectricCyan else P3DeepNavy
  val bgColor = when {
    !enabled -> P3VoidBlack.copy(alpha = 0.5f)
    isSelected -> P3SurfaceNavy
    else -> P3TartarusDark.copy(alpha = 0.8f)
  }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(P3CutShape)
      .background(bgColor)
      .border(if (isSelected) 2.dp else 1.dp, borderColor, P3CutShape)
      .clickable(enabled = enabled) { onClick() }
      .padding(14.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(RoundedCornerShape(5.dp))
              .background(if (isSelected) P3ElectricCyan else Color.Transparent)
              .border(1.dp, if (isSelected) P3ElectricCyan else P3SilverGrey, RoundedCornerShape(5.dp))
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = if (isSelected) P3IceBlue else if (enabled) P3SilverGrey else P3SilverGrey.copy(alpha = 0.4f)
          )
        }

        if (badge != null) {
          P3Badge(
            text = badge,
            color = if (isSelected) P3ElectricCyan else P3DeepNavy,
            textColor = if (isSelected) P3VoidBlack else P3ElectricCyan
          )
        }
      }

      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = description,
        style = MaterialTheme.typography.bodySmall,
        color = P3SilverGrey.copy(alpha = 0.8f),
        modifier = Modifier.padding(start = 20.dp)
      )
    }
  }
}
