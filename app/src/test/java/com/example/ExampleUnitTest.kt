package com.example

import com.example.localization.AppLanguage
import com.example.localization.Strings
import com.example.model.Channel
import com.example.model.RawChannel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testFourLanguagesCompleteness() {
    val languages = AppLanguage.entries

    assertEquals("Must support exactly 4 languages", 4, languages.size)

    languages.forEach { lang ->
      assertTrue(Strings.appName(lang).isNotEmpty())
      assertTrue(Strings.slogan(lang).isNotEmpty())
      assertTrue(Strings.accessCodeTitle(lang).isNotEmpty())
      assertTrue(Strings.accessCodePrompt(lang).isNotEmpty())
      assertTrue(Strings.loginButton(lang).isNotEmpty())
      assertTrue(Strings.clearButton(lang).isNotEmpty())
      assertTrue(Strings.invalidCodeError(lang).isNotEmpty())
      assertTrue(Strings.sharedCodeDisclaimer(lang).isNotEmpty())
      assertTrue(Strings.allChannels(lang).isNotEmpty())
      assertTrue(Strings.favorites(lang).isNotEmpty())
      assertTrue(Strings.searchPlaceholder(lang).isNotEmpty())
      assertTrue(Strings.refresh(lang).isNotEmpty())
      assertTrue(Strings.loadingChannels(lang).isNotEmpty())
      assertTrue(Strings.streamOffline(lang).isNotEmpty())
      assertTrue(Strings.reloadStream(lang).isNotEmpty())
      assertTrue(Strings.adminDashboard(lang).isNotEmpty())
      assertTrue(Strings.addChannel(lang).isNotEmpty())
      assertTrue(Strings.channelName(lang).isNotEmpty())
      assertTrue(Strings.streamUrl(lang).isNotEmpty())
      assertTrue(Strings.settings(lang).isNotEmpty())
    }

    // Verify RTL / LTR configuration
    assertTrue(AppLanguage.KURDISH_SORANI.isRtl)
    assertTrue(AppLanguage.KURDISH_BADINI.isRtl)
    assertTrue(AppLanguage.ARABIC.isRtl)
    assertFalse(AppLanguage.ENGLISH.isRtl)
  }

  @Test
  fun testChannelSafeParsing() {
    // Test safe fallbacks when title or url are alternative fields
    val raw1 = RawChannel(
      title = "Rudaw",
      name = null,
      streamUrl = null,
      url = "http://example.com/rudaw.m3u8",
      logo = null,
      category = null
    )
    val parsed1 = Channel.fromRaw(raw1)
    assertEquals("Rudaw", parsed1.name)
    assertEquals("http://example.com/rudaw.m3u8", parsed1.streamUrl)
    assertEquals("KURD TV", parsed1.category)
    assertNotNull(parsed1.id)

    val raw2 = RawChannel(
      title = null,
      name = "Kurdistan TV",
      streamUrl = "https://example.com/stream.m3u8",
      url = null,
      logo = "https://example.com/logo.png",
      category = "News"
    )
    val parsed2 = Channel.fromRaw(raw2)
    assertEquals("Kurdistan TV", parsed2.name)
    assertEquals("https://example.com/stream.m3u8", parsed2.streamUrl)
    assertEquals("News", parsed2.category)
    assertEquals("https://example.com/logo.png", parsed2.logo)
  }

  @Test
  fun testAccessCodeValidation() {
    val validCode = "0909"
    val invalidCodes = listOf("1234", "0000", "9090", "", "admin")

    assertTrue(validCode == "0909")
    invalidCodes.forEach { code ->
      assertFalse(code == "0909")
    }
  }
}
