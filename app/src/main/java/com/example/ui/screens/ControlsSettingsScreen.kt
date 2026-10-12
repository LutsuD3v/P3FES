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
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ControlInputMode
import com.example.model.ControlsLayout
import com.example.model.LauncherSettings
import com.example.ui.components.P3Badge
import com.example.ui.components.P3CutShape
import com.example.ui.components.P3Header
import com.example.ui.components.P3Ps2ControllerPreview
import com.example.ui.theme.P3AmberAccent
import com.example.ui.theme.P3DeepNavy
import com.example.ui.theme.P3ElectricCyan
import com.example.ui.theme.P3IceBlue
import com.example.ui.theme.P3SilverGrey
import com.example.ui.theme.P3SurfaceNavy
import com.example.ui.theme.P3TartarusDark
import com.example.ui.theme.P3VoidBlack

@Composable
fun ControlsSettingsScreen(
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
      title = "CONTROLS & INPUT",
      subtitle = "Mapeamento DualShock 2 e sensibilidade"
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Interactive PS2 Controller Preview
    Text(
      text = "PRÉVIA ILUSTRATIVA DO CONTROLE",
      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
      color = P3ElectricCyan
    )
    Spacer(modifier = Modifier.height(6.dp))

    P3Ps2ControllerPreview(
      virtualOpacity = settings.buttonOpacityPercent / 100f,
      virtualSizeScale = settings.buttonSizePercent / 100f
    )

    Spacer(modifier = Modifier.height(20.dp))

    // 1. MODO DE ENTRADA
    SectionContainer(title = "MÉTODO DE ENTRADA", subtitle = "Dispositivo primário para comando no jogo") {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ControlInputMode.entries.forEach { mode ->
          val isSelected = settings.inputMode == mode
          SelectableCardItem(
            title = mode.label,
            badge = if (mode == ControlInputMode.VIRTUAL_TOUCH) "PADRÃO" else null,
            description = mode.description,
            isSelected = isSelected,
            onClick = { onUpdateSettings(settings.copy(inputMode = mode)) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 2. LAYOUT DOS BOTÕES VIRTUAIS
    SectionContainer(title = "LAYOUT DOS BOTÕES NA TELA", subtitle = "Disposição geométrica dos analógicos e comandos") {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ControlsLayout.entries.forEach { layout ->
          val isSelected = settings.controlsLayout == layout
          SelectableCardItem(
            title = layout.label,
            badge = null,
            description = layout.description,
            isSelected = isSelected,
            onClick = { onUpdateSettings(settings.copy(controlsLayout = layout)) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 3. VISIBILIDADE E DIMENSÕES
    SectionContainer(title = "CUSTOMIZAÇÃO VISUAL DOS BOTÕES", subtitle = "Transparência e escala para telas de toque") {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(P3CutShape)
          .background(P3TartarusDark)
          .border(1.dp, P3ElectricCyan.copy(alpha = 0.5f), P3CutShape)
          .padding(14.dp)
      ) {
        // Toggle on-screen controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Exibir Controles na Tela",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue
            )
            Text(
              text = "Oculta a sobreposição tátil (ideal para gamepads físicos)",
              style = MaterialTheme.typography.bodySmall,
              color = P3SilverGrey
            )
          }
          Switch(
            checked = settings.showVirtualButtons,
            onCheckedChange = { onUpdateSettings(settings.copy(showVirtualButtons = it)) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = P3ElectricCyan,
              checkedTrackColor = P3DeepNavy,
              uncheckedThumbColor = P3SilverGrey,
              uncheckedTrackColor = P3TartarusDark
            )
          )
        }

        if (settings.showVirtualButtons) {
          Spacer(modifier = Modifier.height(14.dp))

          // Button Size Slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Tamanho dos botões:",
              style = MaterialTheme.typography.bodyMedium,
              color = P3SilverGrey
            )
            Text(
              text = "${settings.buttonSizePercent}%",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = P3ElectricCyan
            )
          }
          Slider(
            value = settings.buttonSizePercent.toFloat(),
            onValueChange = { onUpdateSettings(settings.copy(buttonSizePercent = it.toInt())) },
            valueRange = 50f..150f,
            steps = 9,
            colors = SliderDefaults.colors(
              thumbColor = P3ElectricCyan,
              activeTrackColor = P3ElectricCyan,
              inactiveTrackColor = P3DeepNavy
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Button Opacity Slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Opacidade dos botões:",
              style = MaterialTheme.typography.bodyMedium,
              color = P3SilverGrey
            )
            Text(
              text = "${settings.buttonOpacityPercent}%",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = P3ElectricCyan
            )
          }
          Slider(
            value = settings.buttonOpacityPercent.toFloat(),
            onValueChange = { onUpdateSettings(settings.copy(buttonOpacityPercent = it.toInt())) },
            valueRange = 20f..100f,
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

    Spacer(modifier = Modifier.height(18.dp))

    // 4. FEEDBACK HÁPTICO & SENSIBILIDADE
    SectionContainer(title = "HÁPTICOS E SENSIBILIDADE", subtitle = "Vibração de motor e calibração analógica") {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(P3CutShape)
          .background(P3TartarusDark)
          .border(1.dp, P3ElectricCyan.copy(alpha = 0.5f), P3CutShape)
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Vibração Háptica (Rumble)",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue
            )
            Text(
              text = "Emula os motores de vibração DualShock 2 em colisões e magias",
              style = MaterialTheme.typography.bodySmall,
              color = P3SilverGrey
            )
          }
          Switch(
            checked = settings.vibrationEnabled,
            onCheckedChange = { onUpdateSettings(settings.copy(vibrationEnabled = it)) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = P3ElectricCyan,
              checkedTrackColor = P3DeepNavy,
              uncheckedThumbColor = P3SilverGrey,
              uncheckedTrackColor = P3TartarusDark
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Sensibilidade dos Analógicos:",
            style = MaterialTheme.typography.bodyMedium,
            color = P3SilverGrey
          )
          Text(
            text = "${String.format("%.1f", settings.stickSensitivity)}x",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = P3ElectricCyan
          )
        }
        Slider(
          value = settings.stickSensitivity,
          onValueChange = { onUpdateSettings(settings.copy(stickSensitivity = it)) },
          valueRange = 0.5f..2.0f,
          steps = 5,
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
