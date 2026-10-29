package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.Channel
import com.example.model.ChannelFeedResponse
import com.example.model.RawChannel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class ChannelRepository(private val context: Context) {
  private val prefs: SharedPreferences = context.getSharedPreferences("danar_tv_prefs", Context.MODE_PRIVATE)
  private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .followRedirects(true)
    .build()

  private val _channels = MutableStateFlow<List<Channel>>(emptyList())
  val channels: StateFlow<List<Channel>> = _channels.asStateFlow()

  private val _isLoading = MutableStateFlow(false)
  val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val _favorites = MutableStateFlow<Set<String>>(emptySet())
  val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

  private val _channelHealth = MutableStateFlow<Map<String, Boolean>>(emptyMap())
  val channelHealth: StateFlow<Map<String, Boolean>> = _channelHealth.asStateFlow()

  init {
    loadFavorites()
    loadCachedChannels()
  }

  private fun loadFavorites() {
    val favSet = prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
    _favorites.value = favSet
  }

  fun toggleFavorite(channelId: String) {
    val current = _favorites.value.toMutableSet()
    if (current.contains(channelId)) {
      current.remove(channelId)
    } else {
      current.add(channelId)
    }
    _favorites.value = current
    prefs.edit().putStringSet("favorite_ids", current).apply()

    // Update in-memory channel list
    _channels.value = _channels.value.map { ch ->
      if (ch.id == channelId) ch.copy(isFavorite = current.contains(channelId)) else ch
    }
  }

  private fun loadCachedChannels() {
    val cachedJson = prefs.getString("cached_channels_json", null)
    if (!cachedJson.isNullOrBlank()) {
      try {
        val adapter = moshi.adapter(ChannelFeedResponse::class.java)
        val feed = adapter.fromJson(cachedJson)
        if (feed?.channels != null) {
          applyChannels(feed.channels)
        }
      } catch (e: Exception) {
        // Fallback to built-in if cache corrupt
      }
    }
  }

  suspend fun fetchChannels(forceRefresh: Boolean = false): Result<List<Channel>> = withContext(Dispatchers.IO) {
    _isLoading.value = true
    _errorMessage.value = null

    try {
      val url = "https://raw.githubusercontent.com/hchxdjdhdbxu-stack/SHVAN-HAGI/refs/heads/main/channels.json"
      val request = Request.Builder()
        .url(url)
        .header("User-Agent", "DANAR-TV-Android/1.0")
        .build()

      val response = okHttpClient.newCall(request).execute()
      if (!response.isSuccessful) {
        throw RuntimeException("HTTP Error: ${response.code}")
      }

      val body = response.body?.string() ?: throw RuntimeException("Empty response")
      val adapter = moshi.adapter(ChannelFeedResponse::class.java)
      val feed = adapter.fromJson(body) ?: throw RuntimeException("Could not parse channel feed")

      val rawList = feed.channels ?: emptyList()
      // Cache raw json
      prefs.edit().putString("cached_channels_json", body).apply()

      val parsed = applyChannels(rawList)
      _isLoading.value = false
      Result.success(parsed)
    } catch (e: Exception) {
      _isLoading.value = false
      _errorMessage.value = e.message ?: "Network error"
      // If we have cached channels or fallback, keep them
      if (_channels.value.isEmpty()) {
        val fallback = getBuiltinChannels()
        applyChannels(fallback)
      }
      Result.failure(e)
    }
  }

  private fun applyChannels(rawList: List<RawChannel>): List<Channel> {
    val favSet = _favorites.value
    val customChannels = getCustomAdminChannels()

    val parsed = rawList.mapNotNull { raw ->
      val ch = Channel.fromRaw(raw, isFav = false)
      if (ch.streamUrl.isNotBlank()) {
        ch.copy(isFavorite = favSet.contains(ch.id))
      } else null
    }

    // Combine custom channels with parsed channels
    val combined = (customChannels + parsed).distinctBy { it.streamUrl }
    _channels.value = combined
    return combined
  }

  // Admin Custom Channels Management
  fun getCustomAdminChannels(): List<Channel> {
    val json = prefs.getString("custom_admin_channels", null) ?: return emptyList()
    return try {
      val type = Types.newParameterizedType(List::class.java, RawChannel::class.java)
      val adapter = moshi.adapter<List<RawChannel>>(type)
      val list = adapter.fromJson(json) ?: emptyList()
      list.map { Channel.fromRaw(it).copy(isCustom = true) }
    } catch (e: Exception) {
      emptyList()
    }
  }

  fun saveCustomChannel(channel: Channel) {
    val current = getCustomAdminChannels().toMutableList()
    val index = current.indexOfFirst { it.id == channel.id }
    val raw = RawChannel(
      title = channel.name,
      name = channel.name,
      streamUrl = channel.streamUrl,
      url = channel.streamUrl,
      logo = channel.logo,
      category = channel.category,
      referer = channel.referer,
      userAgent = channel.userAgent,
      drmKey = channel.drmKey
    )

    val rawList = current.map {
      RawChannel(
        title = it.name,
        name = it.name,
        streamUrl = it.streamUrl,
        url = it.streamUrl,
        logo = it.logo,
        category = it.category,
        referer = it.referer,
        userAgent = it.userAgent,
        drmKey = it.drmKey
      )
    }.toMutableList()

    if (index >= 0) {
      rawList[index] = raw
    } else {
      rawList.add(0, raw)
    }

    val type = Types.newParameterizedType(List::class.java, RawChannel::class.java)
    val adapter = moshi.adapter<List<RawChannel>>(type)
    prefs.edit().putString("custom_admin_channels", adapter.toJson(rawList)).apply()

    // Refresh memory
    loadCachedChannels()
  }

  fun deleteCustomChannel(channelId: String) {
    val current = getCustomAdminChannels().filter { it.id != channelId }
    val rawList = current.map {
      RawChannel(
        title = it.name,
        name = it.name,
        streamUrl = it.streamUrl,
        url = it.streamUrl,
        logo = it.logo,
        category = it.category,
        referer = it.referer,
        userAgent = it.userAgent,
        drmKey = it.drmKey
      )
    }
    val type = Types.newParameterizedType(List::class.java, RawChannel::class.java)
    val adapter = moshi.adapter<List<RawChannel>>(type)
    prefs.edit().putString("custom_admin_channels", adapter.toJson(rawList)).apply()

    _channels.value = _channels.value.filter { it.id != channelId }
  }

  fun resetToDefault() {
    prefs.edit().remove("custom_admin_channels").apply()
    loadCachedChannels()
  }

  suspend fun checkStreamHealth(channelId: String, streamUrl: String): Boolean = withContext(Dispatchers.IO) {
    try {
      val request = Request.Builder()
        .url(streamUrl)
        .header("User-Agent", "DANAR-TV/1.0")
        .head()
        .build()

      val response = okHttpClient.newCall(request).execute()
      val isSuccess = response.isSuccessful || response.code == 405 // Some servers disallow HEAD but allow GET
      val updated = _channelHealth.value.toMutableMap()
      updated[channelId] = isSuccess
      _channelHealth.value = updated
      isSuccess
    } catch (e: Exception) {
      val updated = _channelHealth.value.toMutableMap()
      updated[channelId] = false
      _channelHealth.value = updated
      false
    }
  }

  private fun getBuiltinChannels(): List<RawChannel> {
    return listOf(
      RawChannel(
        name = "Quran",
        streamUrl = "http://m.live.net.sa:1935/live/quran/quran.m3u8",
        category = "KURD TV",
        logo = "https://www.appcreator24.com/srv/imgs/cards/o3653569_17853798.png?v=6"
      ),
      RawChannel(
        name = "KURDISTAN TV",
        streamUrl = "https://5a3ed7a72ed4b.streamlock.net/live/SMIL:myStream.smil/playlist.m3u8",
        category = "KURD TV",
        logo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_xlOCtDTfHYWSfEF1SI0ZPIfICZc3qvm1AfNx4T7bdA&s=10"
      ),
      RawChannel(
        name = "RUDAW",
        streamUrl = "http://hlspackager.akamaized.net/live/DB/RUDAW/HLS/RUDAW.m3u8",
        category = "KURD TV",
        logo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQafseokktOfnO_SgzFbwz2IsY2P_ujnKv--cKSOVLT_Q"
      ),
      RawChannel(
        name = "K24",
        streamUrl = "http://hlspackager.akamaized.net/live/DB/K24/HLS/K24.m3u8",
        category = "KURD TV",
        logo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_xlOCtDTfHYWSfEF1SI0ZPIfICZc3qvm1AfNx4T7bdA&s=10"
      ),
      RawChannel(
        name = "CHANNEL 8",
        streamUrl = "https://live.channel8.com/Channel8-Kurdish/index.fmp4.m3u8",
        category = "KURD TV",
        logo = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ_xlOCtDTfHYWSfEF1SI0ZPIfICZc3qvm1AfNx4T7bdA&s=10"
      )
    )
  }
}
