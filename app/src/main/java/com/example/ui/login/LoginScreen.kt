package com.example.ui.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.StatusLive

@Composable
fun LoginScreen(
  currentLanguage: AppLanguage,
  onLanguageChange: (AppLanguage) -> Unit,
  onLoginSuccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  var enteredCode by remember { mutableStateOf("") }
  var isError by remember { mutableStateOf(false) }
  var showLanguageMenu by remember { mutableStateOf(false) }

  fun verifyCode() {
    if (enteredCode.trim() == "0909") {
      isError = false
      onLoginSuccess()
    } else {
      isError = true
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkBackground)
      .padding(16.dp),
    contentAlignment = Alignment.Center
  ) {
    // Language Switcher at Top End
    Box(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = 16.dp, end = 16.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = DarkSurfaceVariant,
        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
        modifier = Modifier
          .clickable { showLanguageMenu = true }
          .testTag("login_language_selector")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Translate, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = currentLanguage.nativeName,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      DropdownMenu(
        expanded = showLanguageMenu,
        onDismissRequest = { showLanguageMenu = false },
        modifier = Modifier.background(DarkSurface)
      ) {
        AppLanguage.entries.forEach { lang ->
          DropdownMenuItem(
            text = {
              Text(
                text = "${lang.nativeName} (${lang.displayName})",
                color = if (lang == currentLanguage) GoldAccent else Color.White
              )
            },
            onClick = {
              onLanguageChange(lang)
              showLanguageMenu = false
            }
          )
        }
      }
    }

    // Login Card
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = DarkSurface),
      border = BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.7f)),
      modifier = Modifier
        .width(440.dp)
        .padding(16.dp)
        .testTag("login_card")
    ) {
      Column(
        modifier = Modifier.padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Icon / Logo
        Box(
          modifier = Modifier
            .size(76.dp)
            .background(
              Brush.radialGradient(listOf(GoldAccent.copy(alpha = 0.25f), Color.Transparent)),
              CircleShape
            )
            .border(2.dp, GoldPrimary, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Image(
            painter = painterResource(id = R.drawable.ic_danar_logo),
            contentDescription = "DANAR TV",
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = Strings.appName(currentLanguage),
          color = GoldAccent,
          fontSize = 26.sp,
          fontWeight = FontWeight.ExtraBold,
          letterSpacing = 1.sp
        )

        Text(
          text = Strings.slogan(currentLanguage),
          color = Color.LightGray,
          fontSize = 13.sp,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
        )

        // PIN / Code Input Field
        OutlinedTextField(
          value = enteredCode,
          onValueChange = {
            if (it.length <= 6) {
              enteredCode = it
              isError = false
            }
          },
          label = { Text(Strings.accessCodeTitle(currentLanguage)) },
          placeholder = { Text("0909") },
          singleLine = true,
          leadingIcon = {
            Icon(Icons.Default.Lock, contentDescription = null, tint = GoldPrimary)
          },
          visualTransformation = PasswordVisualTransformation(),
          keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
          ),
          keyboardActions = KeyboardActions(onDone = { verifyCode() }),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GoldAccent,
            unfocusedBorderColor = DarkBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = GoldAccent,
            unfocusedLabelColor = Color.Gray
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("access_code_input")
        )

        // Error message
        AnimatedVisibility(visible = isError) {
          Text(
            text = Strings.invalidCodeError(currentLanguage),
            color = StatusLive,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 8.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // On-screen keypad for TV Box remote ease
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          val keypadRows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("C", "0", "OK")
          )

          keypadRows.forEach { row ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              row.forEach { key ->
                TvKeypadButton(
                  text = key,
                  modifier = Modifier.weight(1f),
                  onClick = {
                    when (key) {
                      "C" -> {
                        enteredCode = ""
                        isError = false
                      }
                      "OK" -> verifyCode()
                      else -> {
                        if (enteredCode.length < 4) {
                          enteredCode += key
                          isError = false
                          if (enteredCode.length == 4) {
                            verifyCode()
                          }
                        }
                      }
                    }
                  }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sign In Action Button
        Button(
          onClick = { verifyCode() },
          colors = ButtonDefaults.buttonColors(
            containerColor = GoldPrimary,
            contentColor = Color.Black
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("login_submit_button")
        ) {
          Text(
            text = Strings.loginButton(currentLanguage),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Disclaimer explaining shared code vs administrator privileges
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF191D24),
          border = BorderStroke(1.dp, Color(0xFF2C3440)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.Top
          ) {
            Icon(
              Icons.Default.Info,
              contentDescription = null,
              tint = GoldPrimary,
              modifier = Modifier
                .size(16.dp)
                .padding(top = 2.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = Strings.sharedCodeDisclaimer(currentLanguage),
              color = Color.LightGray,
              fontSize = 11.sp,
              lineHeight = 15.sp
            )
          }
        }
      }
    }
  }
}

@Composable
fun TvKeypadButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isFocused by remember { mutableStateOf(false) }

  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isFocused) GoldPrimary else DarkSurfaceVariant,
    border = BorderStroke(1.dp, if (isFocused) GoldAccent else DarkBorder),
    modifier = modifier
      .height(42.dp)
      .focusable()
      .onFocusChanged { isFocused = it.isFocused }
      .clickable { onClick() }
      .testTag("keypad_btn_$text")
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text = text,
        color = if (isFocused) Color.Black else Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )
    }
  }
}
