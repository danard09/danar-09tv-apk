package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.data.ChannelRepository
import com.example.localization.AppLanguage
import com.example.ui.login.LoginScreen
import com.example.ui.main.MainScreen
import com.example.ui.theme.DanarTvTheme
import com.example.ui.theme.DarkBackground

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val repository = ChannelRepository(applicationContext)
    val prefs = getSharedPreferences("danar_tv_prefs", Context.MODE_PRIVATE)

    setContent {
      var currentLanguage by remember {
        val savedLangCode = prefs.getString("selected_language", AppLanguage.KURDISH_SORANI.code)
        mutableStateOf(
          AppLanguage.entries.find { it.code == savedLangCode } ?: AppLanguage.KURDISH_SORANI
        )
      }

      var isLoggedIn by remember {
        mutableStateOf(prefs.getBoolean("is_logged_in", false))
      }

      // Initial channel fetch
      LaunchedEffect(Unit) {
        repository.fetchChannels()
      }

      fun updateLanguage(newLang: AppLanguage) {
        currentLanguage = newLang
        prefs.edit().putString("selected_language", newLang.code).apply()
      }

      fun handleLoginSuccess() {
        isLoggedIn = true
        prefs.edit().putBoolean("is_logged_in", true).apply()
      }

      fun handleLogout() {
        isLoggedIn = false
        prefs.edit().putBoolean("is_logged_in", false).apply()
      }

      // Apply RTL for Kurdish Sorani, Badini, and Arabic; LTR for English
      val layoutDirection = if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

      DanarTvTheme {
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(DarkBackground)
              .safeDrawingPadding()
          ) {
            if (!isLoggedIn) {
              LoginScreen(
                currentLanguage = currentLanguage,
                onLanguageChange = { updateLanguage(it) },
                onLoginSuccess = { handleLoginSuccess() }
              )
            } else {
              MainScreen(
                repository = repository,
                currentLanguage = currentLanguage,
                onLanguageChange = { updateLanguage(it) },
                onLogout = { handleLogout() }
              )
            }
          }
        }
      }
    }
  }
}
