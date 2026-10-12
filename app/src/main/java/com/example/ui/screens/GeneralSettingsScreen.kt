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
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.launcher.EmulatorCoreStatus
import com.example.model.AppLanguage
import com.example.model.LauncherSettings
import com.example.model.LauncherTheme
import com.example.model.PerformanceProfile
import com.example.ui.components.P3Badge
import com.example.ui.components.P3CutShape
import com.example.ui.components.P3Header
import com.example.ui.theme.P3AmberAccent
import com.example.ui.theme.P3DarkHourGreen
import com.example.ui.theme.P3DeepNavy
import com.example.ui.theme.P3ElectricCyan
import com.example.ui.theme.P3IceBlue
import com.example.ui.theme.P3SeesYellow
import com.example.ui.theme.P3ShadowRed
import com.example.ui.theme.P3SilverGrey
import com.example.ui.theme.P3SurfaceNavy
import com.example.ui.theme.P3TartarusDark
import com.example.ui.theme.P3VoidBlack

@Composable
fun GeneralSettingsScreen(
  settings: LauncherSettings,
  coreStatus: EmulatorCoreStatus,
  onUpdateSettings: (LauncherSettings) -> Unit,
  onResetPreferences: () -> Unit,
  onCheckEmulatorConnection: () -> Unit,
  onNavigateToEmulatorStatus: () -> Unit,
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
      title = "GENERAL SETTINGS",
      subtitle = "Preferências globais, perfis e status do emulador"
    )

    Spacer(modifier = Modifier.height(16.dp))

    // EMULATOR STATUS CARD
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(P3CutShape)
        .background(P3TartarusDark)
        .border(
          1.5.dp,
          if (coreStatus.isConnected) P3DarkHourGreen else P3ShadowRed,
          P3CutShape
        )
        .clickable { onNavigateToEmulatorStatus() }
        .padding(16.dp)
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
                .background(if (coreStatus.isConnected) P3DarkHourGreen else P3ShadowRed)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "EMULATOR STATUS",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue
            )
          }

          P3Badge(
            text = if (coreStatus.isConnected) "CONECTADO" else "NÃO CONECTADO",
            color = if (coreStatus.isConnected) P3DarkHourGreen else P3ShadowRed,
            textColor = P3VoidBlack
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Núcleo: ${coreStatus.coreName}",
          style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White
        )
        Text(
          text = "Versão: ${coreStatus.version}",
          style = MaterialTheme.typography.bodySmall,
          color = P3SilverGrey
        )
        Text(
          text = coreStatus.notes,
          style = MaterialTheme.typography.bodySmall,
          color = P3ElectricCyan,
          modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
          onClick = onCheckEmulatorConnection,
          colors = ButtonDefaults.buttonColors(
            containerColor = P3SurfaceNavy,
            contentColor = P3ElectricCyan
          ),
          shape = P3CutShape,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = P3ElectricCyan,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "VERIFICAR CONEXÃO DE INTEGRAÇÃO",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // PERFIL DE DESEMPENHO
    SectionContainer(
      title = "PERFIL DE DESEMPENHO",
      subtitle = "Definição de perfil para uso de energia e frequências"
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PerformanceProfile.entries.forEach { profile ->
          val isSelected = settings.performanceProfile == profile
          SelectableCardItem(
            title = profile.label,
            badge = profile.subtitle,
            description = profile.description,
            isSelected = isSelected,
            onClick = { onUpdateSettings(settings.copy(performanceProfile = profile)) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // IDIOMA DO APLICATIVO
    SectionContainer(title = "IDIOMA DO LAUNCHER", subtitle = "Localização da interface e menus") {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        AppLanguage.entries.forEach { lang ->
          val isSelected = settings.language == lang
          SelectableCardItem(
            title = lang.label,
            badge = lang.code,
            description = "Definir idioma para textos e guias do launcher",
            isSelected = isSelected,
            onClick = { onUpdateSettings(settings.copy(language = lang)) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // TEMA VISUAL
    SectionContainer(title = "TEMA VISUAL", subtitle = "Esquema de cores e atmosfera da Dark Hour") {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LauncherTheme.entries.forEach { theme ->
          val isSelected = settings.theme == theme
          SelectableCardItem(
            title = theme.label,
            badge = null,
            description = theme.description,
            isSelected = isSelected,
            onClick = { onUpdateSettings(settings.copy(theme = theme)) }
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // COMPORTAMENTO
    SectionContainer(title = "COMPORTAMENTO DE INICIALIZAÇÃO", subtitle = "Rotinas do ciclo de vida do launcher") {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(P3CutShape)
          .background(P3TartarusDark)
          .border(1.dp, P3ElectricCyan.copy(alpha = 0.5f), P3CutShape)
          .padding(14.dp)
      ) {
        // Launch last selected game
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Inicializar no Último Jogo",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue
            )
            Text(
              text = "Carrega imediatamente o jogo selecionado ao abrir",
              style = MaterialTheme.typography.bodySmall,
              color = P3SilverGrey
            )
          }
          Switch(
            checked = settings.launchLastSelectedGame,
            onCheckedChange = { onUpdateSettings(settings.copy(launchLastSelectedGame = it)) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = P3ElectricCyan,
              checkedTrackColor = P3DeepNavy,
              uncheckedThumbColor = P3SilverGrey,
              uncheckedTrackColor = P3TartarusDark
            )
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Confirm before exit
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Confirmação Antes de Sair",
              style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue
            )
            Text(
              text = "Evita fechamento acidental durante a sessão de jogo",
              style = MaterialTheme.typography.bodySmall,
              color = P3SilverGrey
            )
          }
          Switch(
            checked = settings.confirmBeforeExit,
            onCheckedChange = { onUpdateSettings(settings.copy(confirmBeforeExit = it)) },
            colors = SwitchDefaults.colors(
              checkedThumbColor = P3ElectricCyan,
              checkedTrackColor = P3DeepNavy,
              uncheckedThumbColor = P3SilverGrey,
              uncheckedTrackColor = P3TartarusDark
            )
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // RESET PREFERENCES
    Button(
      onClick = onResetPreferences,
      colors = ButtonDefaults.buttonColors(
        containerColor = P3ShadowRed.copy(alpha = 0.2f),
        contentColor = P3ShadowRed
      ),
      shape = P3CutShape,
      modifier = Modifier
        .fillMaxWidth()
        .border(1.dp, P3ShadowRed.copy(alpha = 0.6f), P3CutShape)
    ) {
      Icon(
        imageVector = Icons.Default.CleaningServices,
        contentDescription = null,
        tint = P3ShadowRed,
        modifier = Modifier.size(18.dp)
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "LIMPAR PREFERÊNCIAS DA INTERFACE",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // APP INFO
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(P3DeepNavy)
        .padding(14.dp)
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "PERSONA 3 FES — ANDROID LAUNCHER",
          style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          ),
          color = P3ElectricCyan
        )
        Text(
          text = "Versão 1.0.0 (Build Release)",
          style = MaterialTheme.typography.bodySmall,
          color = P3SilverGrey
        )
        Text(
          text = "PlayStation 2 Dedicated Front-End • S.E.E.S. Special Tactical Unit",
          style = MaterialTheme.typography.labelSmall,
          color = P3SilverGrey.copy(alpha = 0.7f),
          modifier = Modifier.padding(top = 4.dp)
        )
      }
    }
  }
}
