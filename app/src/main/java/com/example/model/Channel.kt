package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.UUID

@JsonClass(generateAdapter = true)
data class ChannelFeedResponse(
  @Json(name = "settings") val settings: FeedSettings? = null,
  @Json(name = "channels") val channels: List<RawChannel>? = null
)

@JsonClass(generateAdapter = true)
data class FeedSettings(
  @Json(name = "activation") val activation: Boolean? = true,
  @Json(name = "update") val update: Boolean? = false,
  @Json(name = "vpn") val vpn: Boolean? = false,
  @Json(name = "update_url") val updateUrl: String? = ""
)

@JsonClass(generateAdapter = true)
data class RawChannel(
  @Json(name = "title") val title: String? = null,
  @Json(name = "name") val name: String? = null,
  @Json(name = "stream_url") val streamUrl: String? = null,
  @Json(name = "url") val url: String? = null,
  @Json(name = "logo") val logo: String? = null,
  @Json(name = "category") val category: String? = null,
  @Json(name = "referer") val referer: String? = null,
  @Json(name = "user_agent") val userAgent: String? = null,
  @Json(name = "drm_key") val drmKey: String? = null
)

data class Channel(
  val id: String = UUID.randomUUID().toString(),
  val name: String,
  val streamUrl: String,
  val logo: String,
  val category: String,
  val referer: String? = null,
  val userAgent: String? = null,
  val drmKey: String? = null,
  val isFavorite: Boolean = false,
  val isCustom: Boolean = false,
  val isWorking: Boolean? = null
) {
  companion object {
    fun fromRaw(raw: RawChannel, isFav: Boolean = false): Channel {
      val resolvedName = (raw.name ?: raw.title ?: "Untitled Channel").trim()
      val resolvedUrl = (raw.streamUrl ?: raw.url ?: "").trim()
      val resolvedLogo = (raw.logo ?: "").trim()
      val resolvedCategory = (raw.category ?: "KURD TV").trim().ifEmpty { "KURD TV" }
      val cleanId = "chan_" + resolvedName.lowercase().replace(Regex("[^a-z0-9]"), "_") + "_" + resolvedUrl.hashCode()
      return Channel(
        id = cleanId,
        name = resolvedName,
        streamUrl = resolvedUrl,
        logo = resolvedLogo,
        category = resolvedCategory,
        referer = raw.referer?.ifBlank { null },
        userAgent = raw.userAgent?.ifBlank { null },
        drmKey = raw.drmKey?.ifBlank { null },
        isFavorite = isFav
      )
    }
  }
}
