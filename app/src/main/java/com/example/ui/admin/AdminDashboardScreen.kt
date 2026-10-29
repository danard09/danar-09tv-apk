package com.example.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.ChannelRepository
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.Channel
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusLive
import com.example.ui.theme.StatusWorking
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(
  repository: ChannelRepository,
  language: AppLanguage,
  channels: List<Channel>,
  onBack: () -> Unit,
  onPlayChannel: (Channel) -> Unit,
  modifier: Modifier = Modifier
) {
  val scope = rememberCoroutineScope()
  var channelToEdit by remember { mutableStateOf<Channel?>(null) }
  var isAddDialogOpen by remember { mutableStateOf(false) }
  var showSecurityRulesDialog by remember { mutableStateOf(false) }
  var channelToDelete by remember { mutableStateOf<Channel?>(null) }
  var testingChannelId by remember { mutableStateOf<String?>(null) }
  var testResult by remember { mutableStateOf<Pair<String, Boolean>?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(16.dp)
      .testTag("admin_dashboard_screen")
  ) {
    // Header Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier.testTag("admin_back_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = Strings.close(language),
          tint = GoldAccent
        )
      }

      Icon(
        imageVector = Icons.Default.AdminPanelSettings,
        contentDescription = null,
        tint = GoldAccent,
        modifier = Modifier.size(28.dp)
      )

      Spacer(modifier = Modifier.width(10.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = Strings.adminDashboard(language),
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp
        )
        Text(
          text = "Firebase Authorization & Server-Side Security Rules",
          color = GoldPrimary,
          fontSize = 12.sp
        )
      }

      // View Security Rules Button
      OutlinedButton(
        onClick = { showSecurityRulesDialog = true },
        border = BorderStroke(1.dp, GoldPrimary),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("view_rules_btn")
      ) {
        Icon(Icons.Default.Security, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Firestore Rules", color = GoldAccent, fontSize = 12.sp)
      }

      Spacer(modifier = Modifier.width(8.dp))

      // Add Channel Button
      Button(
        onClick = { isAddDialogOpen = true },
        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("add_channel_btn")
      ) {
        Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(Strings.addChannel(language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
      }
    }

    // Security Notice Card
    Surface(
      shape = RoundedCornerShape(12.dp),
      color = DarkSurface,
      border = BorderStroke(1.dp, Color(0xFF2C3440)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp)
    ) {
      Row(
        modifier = Modifier.padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Security,
          contentDescription = null,
          tint = StatusWorking,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Zero-Trust Admin Authorization",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Text(
            text = Strings.adminNotice(language),
            color = Color.LightGray,
            fontSize = 12.sp
          )
        }
        OutlinedButton(
          onClick = { repository.resetToDefault() },
          border = BorderStroke(1.dp, DarkBorder),
          modifier = Modifier.testTag("reset_defaults_btn")
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(Strings.resetDefaults(language), color = Color.LightGray, fontSize = 11.sp)
        }
      }
    }

    // Channels List
    Text(
      text = "${Strings.allChannels(language)} (${channels.size})",
      color = GoldAccent,
      fontWeight = FontWeight.Bold,
      fontSize = 16.sp,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    LazyColumn(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      items(channels, key = { it.id }) { ch ->
        AdminChannelRow(
          channel = ch,
          isTesting = testingChannelId == ch.id,
          testStatus = if (testResult?.first == ch.id) testResult?.second else null,
          onTestStream = {
            scope.launch {
              testingChannelId = ch.id
              val ok = repository.checkStreamHealth(ch.id, ch.streamUrl)
              testResult = Pair(ch.id, ok)
              testingChannelId = null
            }
          },
          onEdit = { channelToEdit = ch },
          onDelete = { channelToDelete = ch },
          onPlay = { onPlayChannel(ch) }
        )
      }
    }
  }

  // Add / Edit Channel Dialog
  if (isAddDialogOpen || channelToEdit != null) {
    val existing = channelToEdit
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var streamUrl by remember { mutableStateOf(existing?.streamUrl ?: "") }
    var logo by remember { mutableStateOf(existing?.logo ?: "") }
    var category by remember { mutableStateOf(existing?.category ?: "KURD TV") }
    var referer by remember { mutableStateOf(existing?.referer ?: "") }
    var userAgent by remember { mutableStateOf(existing?.userAgent ?: "") }
    var testDialogStatus by remember { mutableStateOf<Boolean?>(null) }
    var isTestingDialog by remember { mutableStateOf(false) }

    AlertDialog(
      onDismissRequest = {
        isAddDialogOpen = false
        channelToEdit = null
      },
      containerColor = DarkSurface,
      shape = RoundedCornerShape(16.dp),
      title = {
        Text(
          text = if (existing != null) Strings.editChannel(language) else Strings.addChannel(language),
          color = GoldAccent,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(Strings.channelName(language)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = adminInputColors()
          )

          OutlinedTextField(
            value = streamUrl,
            onValueChange = {
              streamUrl = it
              testDialogStatus = null
            },
            label = { Text(Strings.streamUrl(language)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = adminInputColors()
          )

          OutlinedTextField(
            value = logo,
            onValueChange = { logo = it },
            label = { Text(Strings.logoUrl(language)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = adminInputColors()
          )

          OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text(Strings.categoryLabel(language)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = adminInputColors()
          )

          // Stream verification test in dialog
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Button(
              onClick = {
                if (streamUrl.isNotBlank()) {
                  scope.launch {
                    isTestingDialog = true
                    testDialogStatus = repository.checkStreamHealth("temp", streamUrl)
                    isTestingDialog = false
                  }
                }
              },
              colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
              shape = RoundedCornerShape(8.dp)
            ) {
              if (isTestingDialog) {
                CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.size(16.dp))
              } else {
                Text("Test Stream URL", color = GoldAccent, fontSize = 12.sp)
              }
            }
            Spacer(modifier = Modifier.width(8.dp))
            testDialogStatus?.let { isOk ->
              Text(
                text = if (isOk) "Online (HTTP 200)" else "Offline / Unreachable",
                color = if (isOk) StatusWorking else StatusLive,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (name.isNotBlank() && streamUrl.isNotBlank()) {
              val newChannel = Channel(
                id = existing?.id ?: "chan_${System.currentTimeMillis()}",
                name = name.trim(),
                streamUrl = streamUrl.trim(),
                logo = logo.trim(),
                category = category.trim().ifEmpty { "KURD TV" },
                referer = referer.ifBlank { null },
                userAgent = userAgent.ifBlank { null },
                isCustom = true
              )
              repository.saveCustomChannel(newChannel)
              isAddDialogOpen = false
              channelToEdit = null
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
        ) {
          Text(Strings.save(language), fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(
          onClick = {
            isAddDialogOpen = false
            channelToEdit = null
          }
        ) {
          Text(Strings.cancel(language), color = Color.LightGray)
        }
      }
    )
  }

  // Delete Confirmation Dialog
  channelToDelete?.let { ch ->
    AlertDialog(
      onDismissRequest = { channelToDelete = null },
      containerColor = DarkSurface,
      shape = RoundedCornerShape(16.dp),
      title = { Text(Strings.deleteChannel(language), color = StatusLive, fontWeight = FontWeight.Bold) },
      text = { Text("${Strings.deleteChannel(language)}: ${ch.name}?", color = Color.White) },
      confirmButton = {
        Button(
          onClick = {
            repository.deleteCustomChannel(ch.id)
            channelToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = StatusLive, contentColor = Color.White)
        ) {
          Text(Strings.deleteChannel(language), fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { channelToDelete = null }) {
          Text(Strings.cancel(language), color = Color.LightGray)
        }
      }
    )
  }

  // Security Rules Inspector Dialog
  if (showSecurityRulesDialog) {
    AlertDialog(
      onDismissRequest = { showSecurityRulesDialog = false },
      containerColor = DarkSurface,
      shape = RoundedCornerShape(16.dp),
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Security, contentDescription = null, tint = GoldAccent)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Firestore Security Architecture", color = GoldAccent, fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Channels are protected by server-side Firestore Security Rules. Only authenticated users whose UID is listed in the `/admins` collection can write, update, or delete channels. Regular viewers and access code users have read-only access.",
            color = Color.White,
            fontSize = 12.sp,
            lineHeight = 16.sp
          )
          Spacer(modifier = Modifier.height(12.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF0F1218),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = """
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    function isAdmin() {
      return request.auth != null &&
        exists(/databases/$(database)/documents/admins/$(request.auth.uid));
    }
    match /channels/{channelId} {
      allow read: if true;
      allow write, update, delete: if isAdmin();
    }
  }
}
              """.trimIndent(),
              color = Color(0xFF9ECE6A),
              fontFamily = FontFamily.Monospace,
              fontSize = 11.sp,
              modifier = Modifier.padding(10.dp)
            )
          }
        }
      },
      confirmButton = {
        Button(
          onClick = { showSecurityRulesDialog = false },
          colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
        ) {
          Text(Strings.close(language), fontWeight = FontWeight.Bold)
        }
      }
    )
  }
}

@Composable
fun AdminChannelRow(
  channel: Channel,
  isTesting: Boolean,
  testStatus: Boolean?,
  onTestStream: () -> Unit,
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  onPlay: () -> Unit
) {
  var isFocused by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = if (isFocused) DarkSurfaceVariant else DarkSurface),
    border = BorderStroke(1.dp, if (isFocused) GoldAccent else DarkBorder),
    modifier = Modifier
      .fillMaxWidth()
      .focusable()
      .onFocusChanged { isFocused = it.isFocused }
      .testTag("admin_channel_row_${channel.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Logo
      if (channel.logo.isNotBlank()) {
        AsyncImage(
          model = channel.logo,
          contentDescription = channel.name,
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(Color.DarkGray)
        )
      } else {
        Box(
          modifier = Modifier
            .size(42.dp)
            .background(GoldPrimary.copy(alpha = 0.2f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = channel.name.take(2).uppercase(),
            color = GoldAccent,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = channel.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
          if (channel.isCustom) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = GoldPrimary.copy(alpha = 0.2f)
            ) {
              Text(
                text = "CUSTOM",
                color = GoldAccent,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
              )
            }
          }
        }
        Text(
          text = channel.streamUrl,
          color = Color.Gray,
          fontSize = 11.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Row(
          modifier = Modifier.padding(top = 2.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = channel.category,
            color = GoldPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
          )
          testStatus?.let { ok ->
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (ok) "● Online" else "● Offline",
              color = if (ok) StatusWorking else StatusLive,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Actions
      IconButton(onClick = onPlay, modifier = Modifier.size(36.dp)) {
        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = GoldAccent)
      }

      IconButton(
        onClick = onTestStream,
        modifier = Modifier.size(36.dp)
      ) {
        if (isTesting) {
          CircularProgressIndicator(color = GoldPrimary, modifier = Modifier.size(16.dp))
        } else {
          Icon(Icons.Default.CheckCircle, contentDescription = "Test Stream", tint = Color.LightGray)
        }
      }

      IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.LightGray)
      }

      IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = StatusLive)
      }
    }
  }
}

@Composable
fun adminInputColors() = OutlinedTextFieldDefaults.colors(
  focusedBorderColor = GoldAccent,
  unfocusedBorderColor = DarkBorder,
  focusedTextColor = Color.White,
  unfocusedTextColor = Color.White,
  focusedLabelColor = GoldAccent,
  unfocusedLabelColor = Color.Gray
)
