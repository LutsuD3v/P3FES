package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.GameItem
import com.example.model.LauncherSettings
import com.example.ui.components.P3AngledBadgeShape
import com.example.ui.components.P3AngledCard
import com.example.ui.components.P3Badge
import com.example.ui.components.P3Button
import com.example.ui.components.P3CutShape
import com.example.ui.theme.P3AmberAccent
import com.example.ui.theme.P3DarkHourGreen
import com.example.ui.theme.P3DeepNavy
import com.example.ui.theme.P3ElectricCyan
import com.example.ui.theme.P3IceBlue
import com.example.ui.theme.P3NeonBlue
import com.example.ui.theme.P3SeesYellow
import com.example.ui.theme.P3ShadowRed
import com.example.ui.theme.P3SilverGrey
import com.example.ui.theme.P3SurfaceHighlight
import com.example.ui.theme.P3SurfaceNavy
import com.example.ui.theme.P3TartarusDark
import com.example.ui.theme.P3VoidBlack

@Composable
fun HomeScreen(
  selectedGame: GameItem?,
  settings: LauncherSettings,
  isEmulatorConnected: Boolean,
  onStartGame: () -> Unit,
  onOpenLibrary: () -> Unit,
  onOpenSettings: () -> Unit,
  onOpenPerformanceProfiles: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scrollState = rememberScrollState()

  // Status computation
  val statusText: String
  val statusColor: Color
  when {
    !isEmulatorConnected -> {
      statusText = "Emulador não conectado"
      statusColor = P3ShadowRed
    }
    selectedGame == null -> {
      statusText = "Não configurado"
      statusColor = P3AmberAccent
    }
    else -> {
      statusText = "Pronto"
      statusColor = P3DarkHourGreen
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 20.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Bar / Title Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .width(4.dp)
              .height(28.dp)
              .background(P3ElectricCyan)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "PERSONA 3 FES",
            style = MaterialTheme.typography.displayMedium.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 2.sp
            ),
            color = P3IceBlue
          )
        }
        Text(
          text = "ANDROID EDITION",
          style = MaterialTheme.typography.labelMedium.copy(
            letterSpacing = 3.sp,
            fontWeight = FontWeight.Bold
          ),
          color = P3ElectricCyan,
          modifier = Modifier.padding(start = 12.dp)
        )
      }

      // Discrete performance indicator
      Box(
        modifier = Modifier
          .clip(P3AngledBadgeShape)
          .background(P3SurfaceNavy)
          .border(1.dp, P3ElectricCyan.copy(alpha = 0.5f), P3AngledBadgeShape)
          .clickable { onOpenPerformanceProfiles() }
          .padding(horizontal = 10.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            tint = Color(settings.performanceProfile.badgeColorHex),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = settings.performanceProfile.label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = P3IceBlue
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Main Cover Art Section
    Box(
      modifier = Modifier
        .widthIn(max = 380.dp)
        .fillMaxWidth()
        .aspectRatio(0.92f)
        .clip(P3CutShape)
        .background(P3TartarusDark)
        .border(2.dp, P3ElectricCyan, P3CutShape)
        .shadow(16.dp, P3CutShape)
    ) {
      if (selectedGame?.coverDrawableRes != null) {
        Image(
          painter = painterResource(id = selectedGame.coverDrawableRes),
          contentDescription = selectedGame.title,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      } else if (selectedGame?.coverUri != null) {
        AsyncImage(
          model = selectedGame.coverUri,
          contentDescription = selectedGame.title,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
      } else {
        // Stylized Dark Hour placeholder
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.radialGradient(
                listOf(P3DeepNavy, P3VoidBlack)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
          ) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(36.dp))
                .border(2.dp, P3ElectricCyan, RoundedCornerShape(36.dp)),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "P3",
                style = MaterialTheme.typography.titleLarge.copy(
                  fontWeight = FontWeight.Black,
                  letterSpacing = 2.sp
                ),
                color = P3ElectricCyan
              )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = selectedGame?.title ?: "Nenhum jogo selecionado",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue,
              textAlign = TextAlign.Center
            )
            Text(
              text = "PlayStation 2 Disc Image",
              style = MaterialTheme.typography.bodySmall,
              color = P3SilverGrey
            )
          }
        }
      }

      // Bottom Gradient Overlay on Cover with Game Info
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .background(
            Brush.verticalGradient(
              listOf(Color.Transparent, P3VoidBlack.copy(alpha = 0.95f))
            )
          )
          .padding(horizontal = 16.dp, vertical = 14.dp)
      ) {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            P3Badge(
              text = "PLAYSTATION 2",
              color = P3ElectricCyan,
              textColor = P3VoidBlack
            )

            // Status Badge
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(statusColor.copy(alpha = 0.2f))
                .border(1.dp, statusColor, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = statusText.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = statusColor
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = selectedGame?.title ?: "Persona 3 FES",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              letterSpacing = 0.5.sp
            ),
            color = Color.White
          )
          Text(
            text = "${selectedGame?.serial ?: "SLUS-21621"} • ${selectedGame?.format ?: "ISO"}",
            style = MaterialTheme.typography.bodySmall,
            color = P3SilverGrey
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Main Actions
    Column(
      modifier = Modifier
        .widthIn(max = 380.dp)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // START GAME Button
      P3Button(
        text = "START GAME",
        onClick = onStartGame,
        modifier = Modifier.fillMaxWidth(),
        isPrimary = true,
        testTag = "start_game_button",
        icon = {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = P3VoidBlack,
            modifier = Modifier.size(22.dp)
          )
        }
      )

      // SECONDARY Buttons Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        P3Button(
          text = "GAME LIBRARY",
          onClick = onOpenLibrary,
          modifier = Modifier.weight(1f),
          isPrimary = false,
          testTag = "library_button",
          icon = {
            Icon(
              imageVector = Icons.Default.FolderOpen,
              contentDescription = null,
              tint = P3ElectricCyan,
              modifier = Modifier.size(16.dp)
            )
          }
        )

        P3Button(
          text = "SETTINGS",
          onClick = onOpenSettings,
          modifier = Modifier.weight(1f),
          isPrimary = false,
          testTag = "settings_button",
          icon = {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = null,
              tint = P3ElectricCyan,
              modifier = Modifier.size(16.dp)
            )
          }
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Quick Stats Card (Console Spec Preview)
    P3AngledCard(
      modifier = Modifier
        .widthIn(max = 380.dp)
        .fillMaxWidth()
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "S.E.E.S. EMULATION PROFILE",
            style = MaterialTheme.typography.labelMedium.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            ),
            color = P3ElectricCyan
          )
          Text(
            text = settings.renderer.label,
            style = MaterialTheme.typography.labelSmall,
            color = P3SilverGrey
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "RESOLUÇÃO",
              style = MaterialTheme.typography.labelSmall,
              color = P3SilverGrey
            )
            Text(
              text = settings.resolution.label,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue
            )
          }

          Column {
            Text(
              text = "FRAME RATE",
              style = MaterialTheme.typography.labelSmall,
              color = P3SilverGrey
            )
            Text(
              text = settings.frameRate.label,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue
            )
          }

          Column {
            Text(
              text = "ASPECT RATIO",
              style = MaterialTheme.typography.labelSmall,
              color = P3SilverGrey
            )
            Text(
              text = settings.aspectRatio.label,
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
              color = P3IceBlue
            )
          }
        }
      }
    }
  }
}
