package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.components.P3Badge
import com.example.ui.components.P3CutShape
import com.example.ui.components.P3Header
import com.example.ui.theme.P3AmberAccent
import com.example.ui.theme.P3DarkHourGreen
import com.example.ui.theme.P3DeepNavy
import com.example.ui.theme.P3ElectricCyan
import com.example.ui.theme.P3IceBlue
import com.example.ui.theme.P3ShadowRed
import com.example.ui.theme.P3SilverGrey
import com.example.ui.theme.P3SurfaceNavy
import com.example.ui.theme.P3TartarusDark
import com.example.ui.theme.P3VoidBlack

@Composable
fun EmulatorStatusScreen(
  coreStatus: EmulatorCoreStatus,
  onCheckConnection: () -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Top Nav Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Voltar",
          tint = P3ElectricCyan
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      P3Header(
        title = "DIAGNÓSTICO DO EMULADOR",
        subtitle = "Integração de núcleo e compatibilidade"
      )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Core Connectivity Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(P3CutShape)
        .background(P3TartarusDark)
        .border(
          2.dp,
          if (coreStatus.isConnected) P3DarkHourGreen else P3ShadowRed,
          P3CutShape
        )
        .padding(16.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (coreStatus.isConnected) Icons.Default.CheckCircle else Icons.Default.Error,
              contentDescription = null,
              tint = if (coreStatus.isConnected) P3DarkHourGreen else P3ShadowRed,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (coreStatus.isConnected) "NÚCLEO CONECTADO" else "SEM INTEGRAÇÃO ATIVA",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = if (coreStatus.isConnected) P3DarkHourGreen else P3ShadowRed
            )
          }

          P3Badge(
            text = if (coreStatus.isConnected) "ONLINE" else "STANDALONE",
            color = if (coreStatus.isConnected) P3DarkHourGreen else P3ShadowRed,
            textColor = P3VoidBlack
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        DiagnosticField(label = "NÚCLEO MOTOR", value = coreStatus.coreName)
        DiagnosticField(label = "VERSÃO REGISTRADA", value = coreStatus.version)
        DiagnosticField(
          label = "PACOTES ENCONTRADOS",
          value = if (coreStatus.installedPackages.isNotEmpty()) {
            coreStatus.installedPackages.joinToString(", ")
          } else {
            "Nenhum pacote compatível instalado"
          }
        )
        DiagnosticField(label = "DIAGNÓSTICO", value = coreStatus.notes)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onCheckConnection,
          colors = ButtonDefaults.buttonColors(
            containerColor = P3ElectricCyan,
            contentColor = P3VoidBlack
          ),
          shape = P3CutShape,
          modifier = Modifier.fillMaxWidth()
        ) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = null,
            tint = P3VoidBlack,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "VERIFICAR PACOTES INSTALADOS",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // PS2 COMPATIBILITY DATABASE ENTRY
    SectionContainer(
      title = "BASE DE DADOS DE COMPATIBILIDADE",
      subtitle = "Relatório verificado da comunidade PS2"
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(P3CutShape)
          .background(P3SurfaceNavy)
          .border(1.dp, P3ElectricCyan.copy(alpha = 0.5f), P3CutShape)
          .padding(14.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Shin Megami Tensei: Persona 3 FES",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue
            )
            P3Badge(
              text = "PLAYABLE (PERFEITO)",
              color = P3DarkHourGreen,
              textColor = P3VoidBlack
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Seriais testados: SLUS-21621 (NTSC-U), SLES-55354 (PAL), SLPM-66690 (NTSC-J)",
            style = MaterialTheme.typography.bodySmall,
            color = P3SilverGrey
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Status: Execução completa sem bugs impeditivos. Recomenda-se renderizador Vulkan e multiplicador 2x para equilíbrio de temperatura e 60 FPS consistentes.",
            style = MaterialTheme.typography.bodySmall,
            color = P3IceBlue,
            lineHeight = 16.sp
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // TECHNICAL BRIDGE ARCHITECTURE
    SectionContainer(
      title = "ARQUITETURA DE INTEGRAÇÃO (EMULATOR LAUNCHER)",
      subtitle = "Estrutura nativa para futura ponte de emulação"
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(P3TartarusDark)
          .border(1.dp, P3ElectricCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
          .padding(14.dp)
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.DeveloperBoard,
              contentDescription = null,
              tint = P3ElectricCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "EmulatorLauncher.kt Bridge",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = P3ElectricCyan
            )
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "O aplicativo implementa a classe EmulatorLauncher desacoplada. Ao integrar uma biblioteca Libretro Core (PCSX2 Core .so) ou IPC AIDL com um emulador de sistema, as chamadas de inicialização serão despachadas diretamente sem alterar as telas.",
            style = MaterialTheme.typography.bodySmall,
            color = P3SilverGrey,
            lineHeight = 16.sp
          )
        }
      }
    }
  }
}

@Composable
private fun DiagnosticField(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = P3SilverGrey
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
      color = P3IceBlue
    )
  }
}
