package com.example.ui.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.ChannelRepository
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.Channel
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.player.DanarHlsPlayer
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusLive
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
  repository: ChannelRepository,
  currentLanguage: AppLanguage,
  onLanguageChange: (AppLanguage) -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  val channels by repository.channels.collectAsState()
  val isLoading by repository.isLoading.collectAsState()
  val errorMessage by repository.errorMessage.collectAsState()
  val favorites by repository.favorites.collectAsState()

  var selectedChannel by remember { mutableStateOf<Channel?>(null) }
  var isPlayerFullscreen by remember { mutableStateOf(false) }
  var searchQuery by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf<String?>(null) }
  var showAdminDashboard by remember { mutableStateOf(false) }
  var showAboutDialog by remember { mutableStateOf(false) }
  var showLanguageMenu by remember { mutableStateOf(false) }

  // Extract unique categories
  val categories = remember(channels) {
    channels.map { it.category }.filter { it.isNotBlank() }.distinct().sorted()
  }

  // Filtered channels
  val filteredChannels = remember(channels, searchQuery, selectedCategory, favorites) {
    channels.filter { ch ->
      val matchesCategory = when (selectedCategory) {
        null -> true
        "__fav__" -> favorites.contains(ch.id)
        else -> ch.category.equals(selectedCategory, ignoreCase = true)
      }
      val matchesSearch = if (searchQuery.isBlank()) true else {
        ch.name.contains(searchQuery, ignoreCase = true) ||
          ch.category.contains(searchQuery, ignoreCase = true)
      }
      matchesCategory && matchesSearch
    }
  }

  fun playAdjacentChannel(offset: Int) {
    val current = selectedChannel ?: return
    val currentIndex = filteredChannels.indexOfFirst { it.id == current.id }
    if (currentIndex >= 0 && filteredChannels.isNotEmpty()) {
      val nextIndex = (currentIndex + offset + filteredChannels.size) % filteredChannels.size
      selectedChannel = filteredChannels[nextIndex]
    }
  }

  if (showAdminDashboard) {
    AdminDashboardScreen(
      repository = repository,
      language = currentLanguage,
      channels = channels,
      onBack = { showAdminDashboard = false },
      onPlayChannel = { ch ->
        showAdminDashboard = false
        selectedChannel = ch
      }
    )
    return
  }

  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
  ) {
    val screenWidth = maxWidth
    val isWideScreen = screenWidth >= 700.dp

    Column(modifier = Modifier.fillMaxSize()) {
      // Header & Navigation Bar
      TopTvBar(
        currentLanguage = currentLanguage,
        onLanguageMenuToggle = { showLanguageMenu = !showLanguageMenu },
        onRefresh = { scope.launch { repository.fetchChannels(forceRefresh = true) } },
        onOpenAdmin = { showAdminDashboard = true },
        onOpenAbout = { showAboutDialog = true },
        onLogout = onLogout,
        isLoading = isLoading
      )

      // Active Video Player (if a channel is selected and not in dedicated fullscreen overlay)
      if (selectedChannel != null && !isPlayerFullscreen) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(if (isWideScreen) 340.dp else 240.dp)
        ) {
          DanarHlsPlayer(
            channel = selectedChannel!!,
            language = currentLanguage,
            isFullscreen = false,
            onToggleFullscreen = { isPlayerFullscreen = true },
            onNextChannel = { playAdjacentChannel(1) },
            onPreviousChannel = { playAdjacentChannel(-1) },
            onToggleFavorite = { repository.toggleFavorite(selectedChannel!!.id) },
            onClosePlayer = { selectedChannel = null }
          )
        }
      }

      // Search & Categories Row
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(DarkSurface)
          .padding(horizontal = 16.dp, vertical = 10.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Search Input
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(Strings.searchPlaceholder(currentLanguage), fontSize = 13.sp) },
            leadingIcon = {
              Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { searchQuery = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
              }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = GoldAccent,
              unfocusedBorderColor = DarkBorder,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White,
              focusedPlaceholderColor = Color.Gray,
              unfocusedPlaceholderColor = Color.Gray
            ),
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("channel_search_bar")
          )

          Spacer(modifier = Modifier.width(12.dp))

          // Channel Count Indicator
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = DarkSurfaceVariant,
            border = BorderStroke(1.dp, DarkBorder)
          ) {
            Text(
              text = "${filteredChannels.size} / ${channels.size}",
              color = GoldPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category Filter Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 4.dp)
        ) {
          // "All" chip
          item {
            TvCategoryChip(
              text = Strings.allChannels(currentLanguage),
              isSelected = selectedCategory == null,
              onClick = { selectedCategory = null },
              tag = "cat_all"
            )
          }

          // "Favorites" chip
          item {
            TvCategoryChip(
              text = "${Strings.favorites(currentLanguage)} (${favorites.size})",
              isSelected = selectedCategory == "__fav__",
              icon = Icons.Default.Favorite,
              onClick = { selectedCategory = "__fav__" },
              tag = "cat_fav"
            )
          }

          // Other categories
          items(categories) { cat ->
            TvCategoryChip(
              text = cat,
              isSelected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              tag = "cat_$cat"
            )
          }
        }
      }

      // Content Body: Loading, Error, or Channel Cards
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
      ) {
        if (isLoading && channels.isEmpty()) {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = Strings.loadingChannels(currentLanguage),
              color = Color.White,
              fontSize = 15.sp,
              fontWeight = FontWeight.Medium
            )
          }
        } else if (channels.isEmpty() && errorMessage != null) {
          Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(Icons.Default.Tv, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(54.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = Strings.errorLoading(currentLanguage),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = errorMessage ?: "", color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = { scope.launch { repository.fetchChannels(forceRefresh = true) } },
              colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
            ) {
              Text(Strings.retry(currentLanguage), fontWeight = FontWeight.Bold)
            }
          }
        } else if (filteredChannels.isEmpty()) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
              text = Strings.noChannelsFound(currentLanguage),
              color = Color.LightGray,
              fontSize = 15.sp
            )
          }
        } else {
          // Responsive Grid of Channels
          val columns = when {
            screenWidth >= 1000.dp -> 4
            screenWidth >= 650.dp -> 3
            else -> 2
          }

          LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
          ) {
            items(filteredChannels, key = { it.id }) { ch ->
              ChannelCard(
                channel = ch,
                isSelected = selectedChannel?.id == ch.id,
                language = currentLanguage,
                onClick = { selectedChannel = ch },
                onToggleFavorite = { repository.toggleFavorite(ch.id) }
              )
            }
          }
        }
      }
    }

    // Fullscreen Player Overlay
    if (selectedChannel != null && isPlayerFullscreen) {
      Box(modifier = Modifier.fillMaxSize()) {
        DanarHlsPlayer(
          channel = selectedChannel!!,
          language = currentLanguage,
          isFullscreen = true,
          onToggleFullscreen = { isPlayerFullscreen = false },
          onNextChannel = { playAdjacentChannel(1) },
          onPreviousChannel = { playAdjacentChannel(-1) },
          onToggleFavorite = { repository.toggleFavorite(selectedChannel!!.id) },
          onClosePlayer = {
            isPlayerFullscreen = false
            selectedChannel = null
          }
        )
      }
    }

    // Language Dropdown
    DropdownMenu(
      expanded = showLanguageMenu,
      onDismissRequest = { showLanguageMenu = false },
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = 54.dp, end = 16.dp)
        .background(DarkSurface)
    ) {
      AppLanguage.entries.forEach { lang ->
        DropdownMenuItem(
          text = {
            Text(
              text = "${lang.nativeName} (${lang.displayName})",
              color = if (lang == currentLanguage) GoldAccent else Color.White,
              fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal
            )
          },
          onClick = {
            onLanguageChange(lang)
            showLanguageMenu = false
          }
        )
      }
    }

    // About / Settings Dialog
    if (showAboutDialog) {
      AlertDialog(
        onDismissRequest = { showAboutDialog = false },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(16.dp),
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
              painter = painterResource(id = R.drawable.ic_danar_logo),
              contentDescription = null,
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(Strings.aboutDanarTv(currentLanguage), color = GoldAccent, fontWeight = FontWeight.Bold)
          }
        },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
              text = Strings.slogan(currentLanguage),
              color = Color.White,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = Strings.developerInfo(currentLanguage),
              color = GoldPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Text(
              text = Strings.targetDevices(currentLanguage),
              color = Color.LightGray,
              fontSize = 12.sp
            )
            Text(
              text = "${Strings.tvRemoteMode(currentLanguage)}: ${Strings.tvRemoteHint(currentLanguage)}",
              color = Color.LightGray,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Version: 1.0.0 (Phone, Android TV, TV Box)",
              color = Color.Gray,
              fontSize = 11.sp
            )
          }
        },
        confirmButton = {
          Button(
            onClick = { showAboutDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
          ) {
            Text(Strings.close(currentLanguage), fontWeight = FontWeight.Bold)
          }
        }
      )
    }
  }
}

@Composable
fun TopTvBar(
  currentLanguage: AppLanguage,
  onLanguageMenuToggle: () -> Unit,
  onRefresh: () -> Unit,
  onOpenAdmin: () -> Unit,
  onOpenAbout: () -> Unit,
  onLogout: () -> Unit,
  isLoading: Boolean
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(DarkSurfaceElevated)
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // Brand Logo & Title
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.testTag("brand_header")
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_danar_logo),
        contentDescription = "DANAR TV",
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = Strings.appName(currentLanguage),
          color = GoldAccent,
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          letterSpacing = 0.5.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(StatusLive, CircleShape)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = Strings.liveBadge(currentLanguage),
            color = Color.LightGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Top action buttons
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      // Refresh
      IconButton(
        onClick = onRefresh,
        modifier = Modifier.testTag("main_refresh_button")
      ) {
        if (isLoading) {
          CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.size(18.dp))
        } else {
          Icon(Icons.Default.Refresh, contentDescription = Strings.refresh(currentLanguage), tint = GoldAccent)
        }
      }

      // Language Switcher
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = DarkSurfaceVariant,
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
        modifier = Modifier
          .clickable { onLanguageMenuToggle() }
          .testTag("main_lang_button")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Translate, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = currentLanguage.nativeName,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Admin Dashboard
      IconButton(
        onClick = onOpenAdmin,
        modifier = Modifier.testTag("main_admin_button")
      ) {
        Icon(Icons.Default.AdminPanelSettings, contentDescription = Strings.adminDashboard(currentLanguage), tint = GoldAccent)
      }

      // About
      IconButton(
        onClick = onOpenAbout,
        modifier = Modifier.testTag("main_about_button")
      ) {
        Icon(Icons.Default.Info, contentDescription = Strings.aboutDanarTv(currentLanguage), tint = Color.LightGray)
      }

      // Logout
      IconButton(
        onClick = onLogout,
        modifier = Modifier.testTag("main_logout_button")
      ) {
        Icon(Icons.Default.ExitToApp, contentDescription = Strings.logout(currentLanguage), tint = Color.LightGray)
      }
    }
  }
}

@Composable
fun TvCategoryChip(
  text: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  tag: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
  var isFocused by remember { mutableStateOf(false) }

  val bg = if (isSelected) GoldPrimary else if (isFocused) DarkSurfaceElevated else DarkSurfaceVariant
  val textColor = if (isSelected) Color.Black else if (isFocused) GoldAccent else Color.White
  val border = if (isFocused) BorderStroke(1.5.dp, GoldAccent) else if (isSelected) null else BorderStroke(1.dp, DarkBorder)

  Surface(
    shape = RoundedCornerShape(20.dp),
    color = bg,
    border = border,
    modifier = Modifier
      .testTag(tag)
      .focusable()
      .onFocusChanged { isFocused = it.isFocused }
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) Color.Black else GoldAccent,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
      }
      Text(
        text = text,
        color = textColor,
        fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Normal,
        fontSize = 12.sp
      )
    }
  }
}

@Composable
fun ChannelCard(
  channel: Channel,
  isSelected: Boolean,
  language: AppLanguage,
  onClick: () -> Unit,
  onToggleFavorite: () -> Unit
) {
  var isFocused by remember { mutableStateOf(false) }

  val scale by animateFloatAsState(
    targetValue = if (isFocused) 1.04f else 1.0f,
    label = "card_scale"
  )
  val borderColor by animateColorAsState(
    targetValue = when {
      isFocused -> GoldAccent
      isSelected -> GoldPrimary
      else -> DarkBorder
    },
    label = "border_color"
  )
  val containerColor by animateColorAsState(
    targetValue = when {
      isFocused -> DarkSurfaceElevated
      isSelected -> GoldContainer.copy(alpha = 0.5f)
      else -> DarkSurface
    },
    label = "container_color"
  )

  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = containerColor),
    border = BorderStroke(if (isFocused || isSelected) 2.dp else 1.dp, borderColor),
    modifier = Modifier
      .fillMaxWidth()
      .scale(scale)
      .focusable()
      .onFocusChanged { isFocused = it.isFocused }
      .clickable { onClick() }
      .testTag("channel_card_${channel.id}")
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(90.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(Color(0xFF0F1218)),
        contentAlignment = Alignment.Center
      ) {
        if (channel.logo.isNotBlank()) {
          AsyncImage(
            model = channel.logo,
            contentDescription = channel.name,
            modifier = Modifier
              .size(72.dp)
              .clip(RoundedCornerShape(8.dp)),
            contentScale = androidx.compose.ui.layout.ContentScale.Fit
          )
        } else {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Tv, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(36.dp))
            Text(
              text = channel.name.take(3).uppercase(),
              color = GoldAccent,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Live badge on top-left of thumbnail
        Row(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(6.dp)
            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(5.dp)
              .background(StatusLive, CircleShape)
          )
          Spacer(modifier = Modifier.width(3.dp))
          Text(
            text = "LIVE",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Favorite star on top-right of thumbnail
        IconButton(
          onClick = onToggleFavorite,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .size(30.dp)
            .padding(4.dp)
        ) {
          Icon(
            imageVector = if (channel.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Favorite",
            tint = if (channel.isFavorite) GoldAccent else Color.LightGray.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Channel Name
      Text(
        text = channel.name,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
      )

      // Category
      Text(
        text = channel.category,
        color = GoldPrimary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.padding(top = 2.dp)
      )
    }
  }
}
