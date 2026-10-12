package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.OpenableColumns
import com.example.R
import com.example.model.GameItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class GameRepository(private val context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("p3fes_game_library", Context.MODE_PRIVATE)

  private val defaultP3FES = GameItem(
    id = "p3fes_primary_core",
    title = "Persona 3 FES",
    subtitle = "PlayStation 2 [SLUS-21621]",
    serial = "SLUS-21621",
    fileName = "Persona 3 FES (USA).iso",
    filePathUri = null,
    format = "ISO",
    fileSizeBytes = 4_467_982_336L,
    isMainP3FES = true,
    coverDrawableRes = R.drawable.img_p3fes_cover,
    coverUri = null
  )

  private val _games = MutableStateFlow<List<GameItem>>(emptyList())
  val games: StateFlow<List<GameItem>> = _games.asStateFlow()

  private val _selectedGame = MutableStateFlow<GameItem?>(null)
  val selectedGame: StateFlow<GameItem?> = _selectedGame.asStateFlow()

  init {
    loadGames()
  }

  private fun loadGames() {
    val jsonString = prefs.getString("saved_games_json", null)
    val loadedList = mutableListOf<GameItem>()

    if (jsonString.isNullOrEmpty()) {
      // First launch default includes the primary Persona 3 FES profile
      loadedList.add(defaultP3FES)
      saveGamesInternal(loadedList)
    } else {
      runCatching {
        val jsonArray = JSONArray(jsonString)
        for (i in 0 until jsonArray.length()) {
          val obj = jsonArray.getJSONObject(i)
          val id = obj.optString("id", UUID.randomUUID().toString())
          val title = obj.optString("title", "Unknown Title")
          val subtitle = obj.optString("subtitle", "PlayStation 2")
          val serial = obj.optString("serial", "SLUS-XXXXX")
          val fileName = obj.optString("fileName", "disc.iso")
          val filePathUri = if (obj.has("filePathUri") && !obj.isNull("filePathUri")) obj.optString("filePathUri") else null
          val format = obj.optString("format", "ISO")
          val fileSizeBytes = obj.optLong("fileSizeBytes", 4_000_000_000L)
          val isMain = obj.optBoolean("isMainP3FES", false)
          val coverDrawableRes = if (isMain) R.drawable.img_p3fes_cover else null
          val coverUri = if (obj.has("coverUri") && !obj.isNull("coverUri")) obj.optString("coverUri") else null
          val dateAdded = obj.optLong("dateAdded", System.currentTimeMillis())

          loadedList.add(
            GameItem(
              id = id,
              title = title,
              subtitle = subtitle,
              serial = serial,
              fileName = fileName,
              filePathUri = filePathUri,
              format = format,
              fileSizeBytes = fileSizeBytes,
              isMainP3FES = isMain,
              coverDrawableRes = coverDrawableRes,
              coverUri = coverUri,
              dateAdded = dateAdded
            )
          )
        }
      }.onFailure {
        loadedList.clear()
        loadedList.add(defaultP3FES)
      }
    }

    _games.value = loadedList

    val savedSelectedId = prefs.getString("selected_game_id", null)
    val active = loadedList.firstOrNull { it.id == savedSelectedId }
      ?: loadedList.firstOrNull { it.isMainP3FES }
      ?: loadedList.firstOrNull()

    _selectedGame.value = active
  }

  fun importGameFromUri(uri: Uri): GameItem {
    var displayName = "Imported Disc.iso"
    var size = 0L

    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
      val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
      val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
      if (cursor.moveToFirst()) {
        if (nameIndex != -1) {
          displayName = cursor.getString(nameIndex) ?: displayName
        }
        if (sizeIndex != -1) {
          size = cursor.getLong(sizeIndex)
        }
      }
    }

    val extension = displayName.substringAfterLast('.', "ISO").uppercase()
    val baseTitle = displayName.substringBeforeLast('.')
      .replace('_', ' ')
      .replace('-', ' ')
      .trim()

    val isPersona = displayName.contains("persona", ignoreCase = true) ||
      displayName.contains("p3", ignoreCase = true)

    val newGame = GameItem(
      id = UUID.randomUUID().toString(),
      title = if (baseTitle.isNotEmpty()) baseTitle else "PS2 Disc Image",
      subtitle = if (isPersona) "Persona 3 FES — PlayStation 2" else "PlayStation 2 Disc Image",
      serial = if (isPersona) "SLUS-21621" else "PS2-IMPORT",
      fileName = displayName,
      filePathUri = uri.toString(),
      format = extension,
      fileSizeBytes = if (size > 0) size else 4_100_000_000L,
      isMainP3FES = isPersona,
      coverDrawableRes = if (isPersona) R.drawable.img_p3fes_cover else null,
      coverUri = null
    )

    val currentList = _games.value.toMutableList()
    // If it's a Persona 3 FES import and we still only had the placeholder default, we can replace or append
    currentList.add(0, newGame)
    _games.value = currentList
    saveGamesInternal(currentList)
    selectGame(newGame)
    return newGame
  }

  fun selectGame(game: GameItem) {
    _selectedGame.value = game
    prefs.edit().putString("selected_game_id", game.id).apply()
  }

  fun setAsMainPersona3(gameId: String) {
    val currentList = _games.value.map {
      if (it.id == gameId) it.copy(isMainP3FES = true)
      else it.copy(isMainP3FES = false)
    }
    _games.value = currentList
    saveGamesInternal(currentList)
    val updatedGame = currentList.firstOrNull { it.id == gameId }
    if (updatedGame != null) {
      selectGame(updatedGame)
    }
  }

  fun removeGame(gameId: String) {
    val currentList = _games.value.filterNot { it.id == gameId }
    _games.value = currentList
    saveGamesInternal(currentList)
    if (_selectedGame.value?.id == gameId) {
      val next = currentList.firstOrNull { it.isMainP3FES } ?: currentList.firstOrNull()
      _selectedGame.value = next
      prefs.edit().putString("selected_game_id", next?.id).apply()
    }
  }

  fun restoreDefaultP3FES() {
    val currentList = _games.value.toMutableList()
    if (currentList.none { it.id == defaultP3FES.id }) {
      currentList.add(defaultP3FES)
      _games.value = currentList
      saveGamesInternal(currentList)
      selectGame(defaultP3FES)
    }
  }

  private fun saveGamesInternal(list: List<GameItem>) {
    val jsonArray = JSONArray()
    for (item in list) {
      val obj = JSONObject().apply {
        put("id", item.id)
        put("title", item.title)
        put("subtitle", item.subtitle)
        put("serial", item.serial)
        put("fileName", item.fileName)
        put("filePathUri", item.filePathUri)
        put("format", item.format)
        put("fileSizeBytes", item.fileSizeBytes)
        put("isMainP3FES", item.isMainP3FES)
        put("coverUri", item.coverUri)
        put("dateAdded", item.dateAdded)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString("saved_games_json", jsonArray.toString()).apply()
  }
}
