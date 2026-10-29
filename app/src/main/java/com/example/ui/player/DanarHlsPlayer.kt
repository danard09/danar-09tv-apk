package com.example.ui.player

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.Channel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusLive
import kotlinx.coroutines.delay

class PlayerWebInterface(
  private val onPlaying: () -> Unit,
  private val onBuffering: () -> Unit,
  private val onError: (String) -> Unit
) {
  @JavascriptInterface
  fun onNativePlaying() {
    onPlaying()
  }

  @JavascriptInterface
  fun onNativeBuffering() {
    onBuffering()
  }

  @JavascriptInterface
  fun onNativeError(msg: String) {
    onError(msg)
  }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun DanarHlsPlayer(
  channel: Channel,
  language: AppLanguage,
  isFullscreen: Boolean,
  onToggleFullscreen: () -> Unit,
  onNextChannel: () -> Unit,
  onPreviousChannel: () -> Unit,
  onToggleFavorite: () -> Unit,
  onClosePlayer: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isControlsVisible by remember { mutableStateOf(true) }
  var isBuffering by remember { mutableStateOf(true) }
  var hasPlaybackError by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf("") }
  var isMuted by remember { mutableStateOf(false) }
  var aspectRatioMode by remember { mutableStateOf("contain") } // "contain", "cover", "fill"
  var webViewInstance by remember { mutableStateOf<WebView?>(null) }

  // Auto-hide controls after 5 seconds if not interacting
  LaunchedEffect(isControlsVisible) {
    if (isControlsVisible) {
      delay(5000)
      isControlsVisible = false
    }
  }

  BackHandler {
    if (isFullscreen) {
      onToggleFullscreen()
    } else {
      onClosePlayer()
    }
  }

  val htmlContent = remember(channel.streamUrl, aspectRatioMode) {
    generateHlsPlayerHtml(
      streamUrl = channel.streamUrl,
      objectFit = aspectRatioMode
    )
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .testTag("danar_hls_player_container")
      .clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
      ) {
        isControlsVisible = !isControlsVisible
      }
  ) {
    // Android WebView running HTML5 + HLS.js
    AndroidView(
      modifier = Modifier.fillMaxSize(),
      factory = { ctx ->
        WebView(ctx).apply {
          layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
          )
          setBackgroundColor(AndroidColor.BLACK)
          settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
            cacheMode = WebSettings.LOAD_DEFAULT
            allowFileAccess = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
          }
          webChromeClient = WebChromeClient()
          webViewClient = WebViewClient()
          addJavascriptInterface(
            PlayerWebInterface(
              onPlaying = {
                isBuffering = false
                hasPlaybackError = false
              },
              onBuffering = {
                isBuffering = true
              },
              onError = { err ->
                isBuffering = false
                hasPlaybackError = true
                errorMessage = err
              }
            ),
            "DanarAndroid"
          )
          loadDataWithBaseURL("https://danartv.app", htmlContent, "text/html", "UTF-8", null)
          webViewInstance = this
        }
      },
      update = { wv ->
        wv.loadDataWithBaseURL("https://danartv.app", htmlContent, "text/html", "UTF-8", null)
      }
    )

    // Buffering indicator
    if (isBuffering && !hasPlaybackError) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          CircularProgressIndicator(
            color = GoldPrimary,
            modifier = Modifier.size(52.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = Strings.loadingChannels(language),
            color = Color.White,
            fontSize = 14.sp
          )
        }
      }
    }

    // Stream Offline / Error Alert Overlay
    if (hasPlaybackError) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
      ) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = DarkSurface,
          border = androidx.compose.foundation.BorderStroke(1.5.dp, GoldPrimary),
          modifier = Modifier
            .padding(24.dp)
            .width(420.dp)
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.ErrorOutline,
              contentDescription = null,
              tint = StatusLive,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = Strings.streamOffline(language),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = Strings.streamOfflineHint(language),
              color = Color.LightGray,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Button(
                onClick = {
                  hasPlaybackError = false
                  isBuffering = true
                  webViewInstance?.loadDataWithBaseURL("https://danartv.app", htmlContent, "text/html", "UTF-8", null)
                },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                modifier = Modifier
                  .weight(1f)
                  .testTag("player_retry_button")
              ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(6.dp))
                Text(Strings.reloadStream(language), color = Color.Black, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = onNextChannel,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D3748)),
                modifier = Modifier
                  .weight(1f)
                  .testTag("player_next_button")
              ) {
                Icon(Icons.Default.SkipNext, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(Strings.nextChannel(language), color = Color.White)
              }
            }
          }
        }
      }
    }

    // Top and Bottom Controls Overlay
    AnimatedVisibility(
      visible = isControlsVisible,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.fillMaxSize()
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter)
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
              )
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onClosePlayer,
            modifier = Modifier.testTag("player_back_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = Strings.close(language),
              tint = GoldAccent
            )
          }

          if (channel.logo.isNotBlank()) {
            AsyncImage(
              model = channel.logo,
              contentDescription = channel.name,
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.DarkGray)
            )
            Spacer(modifier = Modifier.width(10.dp))
          }

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = channel.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.width(8.dp))
              // Live badge
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                  .background(StatusLive, RoundedCornerShape(4.dp))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .background(Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = Strings.liveBadge(language),
                  color = Color.White,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
            Text(
              text = channel.category,
              color = GoldPrimary,
              fontSize = 12.sp
            )
          }

          // Favorite toggle
          IconButton(
            onClick = onToggleFavorite,
            modifier = Modifier.testTag("player_fav_button")
          ) {
            Icon(
              imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = if (channel.isFavorite) Strings.removeFromFav(language) else Strings.addToFav(language),
              tint = if (channel.isFavorite) GoldAccent else Color.White
            )
          }

          // Aspect ratio toggle
          IconButton(
            onClick = {
              aspectRatioMode = when (aspectRatioMode) {
                "contain" -> "cover"
                "cover" -> "fill"
                else -> "contain"
              }
            },
            modifier = Modifier.testTag("player_aspect_ratio_button")
          ) {
            Icon(
              imageVector = Icons.Default.AspectRatio,
              contentDescription = Strings.aspectRatio(language),
              tint = Color.White
            )
          }

          // Fullscreen toggle
          IconButton(
            onClick = onToggleFullscreen,
            modifier = Modifier.testTag("player_fullscreen_button")
          ) {
            Icon(
              imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
              contentDescription = Strings.fullscreen(language),
              tint = GoldAccent
            )
          }
        }

        // Bottom Bar / Remote Control Strip
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
              )
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Previous Channel button (with TV focus support)
            TvFocusableButton(
              onClick = onPreviousChannel,
              label = Strings.previousChannel(language),
              icon = Icons.Default.SkipPrevious,
              tag = "tv_prev_channel"
            )

            // Reload button
            TvFocusableButton(
              onClick = {
                hasPlaybackError = false
                isBuffering = true
                webViewInstance?.loadDataWithBaseURL("https://danartv.app", htmlContent, "text/html", "UTF-8", null)
              },
              label = Strings.reloadStream(language),
              icon = Icons.Default.Refresh,
              tag = "tv_reload_stream"
            )

            // Next Channel button
            TvFocusableButton(
              onClick = onNextChannel,
              label = Strings.nextChannel(language),
              icon = Icons.Default.SkipNext,
              tag = "tv_next_channel"
            )
          }

          // Mute toggle
          IconButton(
            onClick = {
              isMuted = !isMuted
              webViewInstance?.evaluateJavascript("setMuted($isMuted);", null)
            },
            modifier = Modifier.testTag("player_mute_button")
          ) {
            Icon(
              imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
              contentDescription = "Mute/Unmute",
              tint = if (isMuted) StatusLive else Color.White
            )
          }
        }
      }
    }
  }

  DisposableEffect(channel.id) {
    onDispose {
      webViewInstance?.destroy()
    }
  }
}

@Composable
fun TvFocusableButton(
  onClick: () -> Unit,
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tag: String
) {
  var isFocused by remember { mutableStateOf(false) }

  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isFocused) GoldPrimary else Color(0xFF1E222B),
    border = if (isFocused) androidx.compose.foundation.BorderStroke(2.dp, GoldAccent) else null,
    modifier = Modifier
      .testTag(tag)
      .focusable()
      .onFocusChanged { isFocused = it.isFocused }
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isFocused) Color.Black else Color.White,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        color = if (isFocused) Color.Black else Color.White,
        fontSize = 12.sp,
        fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}

private fun generateHlsPlayerHtml(streamUrl: String, objectFit: String): String {
  return """
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
  <title>DANAR TV Player</title>
  <script src="https://cdn.jsdelivr.net/npm/hls.js@latest"></script>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    html, body {
      width: 100%;
      height: 100%;
      background-color: #000;
      overflow: hidden;
      display: flex;
      justify-content: center;
      align-items: center;
    }
    video {
      width: 100%;
      height: 100%;
      object-fit: $objectFit;
      background: #000;
    }
  </style>
</head>
<body>
  <video id="danarVideo" playsinline autoplay></video>

  <script>
    const video = document.getElementById('danarVideo');
    const streamSource = "$streamUrl";
    let hls = null;

    function reportPlaying() {
      if (window.DanarAndroid && window.DanarAndroid.onNativePlaying) {
        window.DanarAndroid.onNativePlaying();
      }
    }

    function reportBuffering() {
      if (window.DanarAndroid && window.DanarAndroid.onNativeBuffering) {
        window.DanarAndroid.onNativeBuffering();
      }
    }

    function reportError(msg) {
      if (window.DanarAndroid && window.DanarAndroid.onNativeError) {
        window.DanarAndroid.onNativeError(msg);
      }
    }

    function setMuted(muted) {
      video.muted = muted;
    }

    video.addEventListener('playing', () => reportPlaying());
    video.addEventListener('waiting', () => reportBuffering());
    video.addEventListener('error', (e) => {
      reportError("Playback error on stream");
    });

    if (Hls.isSupported()) {
      hls = new Hls({
        enableWorker: true,
        lowLatencyMode: true,
        backBufferLength: 60
      });
      hls.loadSource(streamSource);
      hls.attachMedia(video);
      hls.on(Hls.Events.MANIFEST_PARSED, function() {
        video.play().catch(e => console.log('Autoplay handled', e));
      });
      hls.on(Hls.Events.ERROR, function(event, data) {
        if (data.fatal) {
          switch(data.type) {
            case Hls.ErrorTypes.NETWORK_ERROR:
              hls.startLoad();
              break;
            case Hls.ErrorTypes.MEDIA_ERROR:
              hls.recoverMediaError();
              break;
            default:
              hls.destroy();
              reportError(data.details || "Fatal HLS Stream Error");
              break;
          }
        }
      });
    } else if (video.canPlayType('application/vnd.apple.mpegurl')) {
      video.src = streamSource;
      video.addEventListener('loadedmetadata', function() {
        video.play().catch(e => console.log('Native playback handled', e));
      });
    } else {
      video.src = streamSource;
      video.play().catch(e => reportError("Format unsupported"));
    }
  </script>
</body>
</html>
  """.trimIndent()
}
