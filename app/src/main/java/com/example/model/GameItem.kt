package com.example.model

data class GameItem(
  val id: String,
  val title: String,
  val subtitle: String = "PlayStation 2",
  val serial: String = "SLUS-21621",
  val fileName: String,
  val filePathUri: String? = null,
  val format: String = "ISO",
  val fileSizeBytes: Long = 4_350_000_000L,
  val isMainP3FES: Boolean = false,
  val coverDrawableRes: Int? = null,
  val coverUri: String? = null,
  val dateAdded: Long = System.currentTimeMillis()
) {
  val formattedSize: String
    get() {
      val gb = fileSizeBytes.toDouble() / (1024 * 1024 * 1024)
      return if (gb >= 1.0) {
        String.format("%.2f GB", gb)
      } else {
        val mb = fileSizeBytes.toDouble() / (1024 * 1024)
        String.format("%.1f MB", mb)
      }
    }
}
