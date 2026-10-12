package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DiscFull
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.GameItem
import com.example.ui.components.P3AngledBadgeShape
import com.example.ui.components.P3Badge
import com.example.ui.components.P3Button
import com.example.ui.components.P3CutShape
import com.example.ui.components.P3Header
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
fun GameLibraryScreen(
  games: List<GameItem>,
  selectedGame: GameItem?,
  onImportUri: (Uri) -> Unit,
  onSelectGame: (GameItem) -> Unit,
  onSetMainPersona3: (String) -> Unit,
  onRemoveGame: (String) -> Unit,
  onRestoreDefault: () -> Unit,
  modifier: Modifier = Modifier
) {
  // Document picker for PS2 disc images (.iso, .bin, .chd, .cso, etc.)
  val documentPicker = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.OpenDocument()
  ) { uri: Uri? ->
    if (uri != null) {
      onImportUri(uri)
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 20.dp, vertical = 16.dp)
  ) {
    // Header
    P3Header(
      title = "GAME LIBRARY",
      subtitle = "Biblioteca local de imagens de disco PS2"
    )

    Spacer(modifier = Modifier.height(14.dp))

    // Import Button & Technical Note
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      P3Button(
        text = "IMPORT GAME",
        onClick = {
          documentPicker.launch(arrayOf("*/*"))
        },
        modifier = Modifier.weight(1f),
        isPrimary = true,
        testTag = "import_game_button",
        icon = {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = P3VoidBlack,
            modifier = Modifier.size(18.dp)
          )
        }
      )

      Spacer(modifier = Modifier.width(10.dp))

      Box(
        modifier = Modifier
          .clip(P3CutShape)
          .background(P3SurfaceNavy)
          .border(1.dp, P3ElectricCyan.copy(alpha = 0.5f), P3CutShape)
          .clickable { onRestoreDefault() }
          .padding(horizontal = 14.dp, vertical = 14.dp)
      ) {
        Text(
          text = "RESET P3 FES",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = P3ElectricCyan
        )
      }
    }

    // Disclaimer card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(P3TartarusDark)
        .border(1.dp, P3ElectricCyan.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
        .padding(10.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = P3ElectricCyan,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "A importação registra a ISO no Launcher. A execução depende do núcleo de emulação conectado.",
          style = MaterialTheme.typography.bodySmall,
          color = P3SilverGrey
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Empty state or list
    if (games.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.DiscFull,
            contentDescription = null,
            tint = P3ElectricCyan.copy(alpha = 0.4f),
            modifier = Modifier.size(64.dp)
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "Nenhum jogo na biblioteca",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = P3IceBlue
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Toque em IMPORT GAME para selecionar uma imagem .iso, .chd ou .bin no armazenamento.",
            style = MaterialTheme.typography.bodyMedium,
            color = P3SilverGrey,
            textAlign = TextAlign.Center
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 20.dp)
      ) {
        items(games, key = { it.id }) { game ->
          GameListItem(
            game = game,
            isSelected = selectedGame?.id == game.id,
            onSelect = { onSelectGame(game) },
            onSetMainP3 = { onSetMainPersona3(game.id) },
            onRemove = { onRemoveGame(game.id) }
          )
        }
      }
    }
  }
}

@Composable
fun GameListItem(
  game: GameItem,
  isSelected: Boolean,
  onSelect: () -> Unit,
  onSetMainP3: () -> Unit,
  onRemove: () -> Unit
) {
  val borderColor = if (isSelected) P3ElectricCyan else P3DeepNavy
  val bgColor = if (isSelected) P3SurfaceNavy.copy(alpha = 0.95f) else P3TartarusDark.copy(alpha = 0.85f)

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(P3CutShape)
      .background(bgColor)
      .border(if (isSelected) 2.dp else 1.dp, borderColor, P3CutShape)
      .clickable { onSelect() }
      .padding(12.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Cover thumbnail
      Box(
        modifier = Modifier
          .size(width = 64.dp, height = 80.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(P3DeepNavy)
          .border(1.dp, P3ElectricCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
      ) {
        if (game.coverDrawableRes != null) {
          Image(
            painter = painterResource(id = game.coverDrawableRes),
            contentDescription = game.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else if (game.coverUri != null) {
          AsyncImage(
            model = game.coverUri,
            contentDescription = game.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else {
          Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.DiscFull,
              contentDescription = null,
              tint = P3ElectricCyan,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      // Game Details
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          if (game.isMainP3FES) {
            P3Badge(
              text = "P3 FES MAIN",
              color = P3SeesYellow,
              textColor = P3VoidBlack
            )
            Spacer(modifier = Modifier.width(6.dp))
          }
          P3Badge(
            text = game.format,
            color = P3SurfaceNavy,
            textColor = P3ElectricCyan
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = game.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = Color.White,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = game.fileName,
          style = MaterialTheme.typography.bodySmall,
          color = P3SilverGrey,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = "${game.formattedSize} • ${if (game.filePathUri != null) "Armazenamento local" else "Perfil padrão"}",
          style = MaterialTheme.typography.labelSmall,
          color = P3ElectricCyan
        )
      }

      // Actions
      Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(
          onClick = onSetMainP3,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = if (game.isMainP3FES) Icons.Filled.Star else Icons.Outlined.StarOutline,
            contentDescription = "Definir como Persona 3 FES principal",
            tint = if (game.isMainP3FES) P3SeesYellow else P3SilverGrey,
            modifier = Modifier.size(20.dp)
          )
        }

        IconButton(
          onClick = onRemove,
          modifier = Modifier.size(36.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remover jogo",
            tint = P3ShadowRed.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
