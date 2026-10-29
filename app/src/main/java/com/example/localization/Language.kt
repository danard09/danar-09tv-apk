package com.example.localization

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String, val isRtl: Boolean) {
  KURDISH_SORANI("ckb", "Kurdish (Sorani)", "کوردیی سۆرانی", true),
  KURDISH_BADINI("badini", "Kurdish (Badini)", "کوردیی بادینی", true),
  ARABIC("ar", "Arabic", "العربية", true),
  ENGLISH("en", "English", "English", false)
}

object Strings {
  fun appName(lang: AppLanguage) = "DANAR TV"
  
  fun slogan(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "پەخشی ڕاستەوخۆی کەناڵەکان بە کوالێتی بەرز"
    AppLanguage.KURDISH_BADINI -> "پەخشی ئێکسەر یا کەناڵان ب کوالێتیا بلند"
    AppLanguage.ARABIC -> "البث المباشر للقنوات التلفزيونية بجودة عالية"
    AppLanguage.ENGLISH -> "Premium Live TV Streaming"
  }

  // Login
  fun accessCodeTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "کۆدی چوونەژوورەوە"
    AppLanguage.KURDISH_BADINI -> "کۆدێ دەربازبوونێ"
    AppLanguage.ARABIC -> "رمز الدخول"
    AppLanguage.ENGLISH -> "Access Code"
  }

  fun accessCodePrompt(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "تکایە کۆدی ٠٩٠٩ بنووسە بۆ چوونەژوورەوە"
    AppLanguage.KURDISH_BADINI -> "هیڤیدارین کۆدێ ٠٩٠٩ بنڤیسە بۆ دەربازبوونێ"
    AppLanguage.ARABIC -> "يرجى إدخال الرمز 0909 للدخول"
    AppLanguage.ENGLISH -> "Please enter access code 0909 to continue"
  }

  fun loginButton(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "چوونەژوورەوە"
    AppLanguage.KURDISH_BADINI -> "دەربازبوون"
    AppLanguage.ARABIC -> "تسجيل الدخول"
    AppLanguage.ENGLISH -> "Sign In"
  }

  fun clearButton(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "سڕینەوە"
    AppLanguage.KURDISH_BADINI -> "ژێبرن"
    AppLanguage.ARABIC -> "مسح"
    AppLanguage.ENGLISH -> "Clear"
  }

  fun invalidCodeError(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "کۆدەکە هەڵەیە! تکایە کۆدی دروست ٠٩٠٩ بنووسە."
    AppLanguage.KURDISH_BADINI -> "کۆد خەلەتە! هیڤیدارین کۆدێ دروست ٠٩٠٩ بنڤیسە."
    AppLanguage.ARABIC -> "الرمز غير صحيح! يرجى كتابة الرمز الصحيح 0909."
    AppLanguage.ENGLISH -> "Invalid code! Please enter the correct code 0909."
  }

  fun sharedCodeDisclaimer(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "تێبینیی گرنگ: ئەم کۆدە هاوبەشە (٠٩٠٩) بۆ تاقیکردنەوە و بینینی خێرایە، وەک دەسەڵاتی بەڕێوەبەر (Admin) هەژمار ناکرێت."
    AppLanguage.KURDISH_BADINI -> "تێبینییا گرنگ: ئەڤ کۆدێ هەڤپشک (٠٩٠٩) بۆ تەماشاکرنا لەزە و ناهێتە هژمارتن وەک دەستپێگەهشتنا ئەدمینی (Admin)."
    AppLanguage.ARABIC -> "ملاحظة أمنية: هذا الرمز المشترك (0909) للمعاينة السريعة فقط ولا يمنح أي صلاحيات إدارية (Admin)."
    AppLanguage.ENGLISH -> "Security Notice: The shared access code (0909) is for viewer preview and does not grant administrator privileges."
  }

  // Navigation & Tabs
  fun allChannels(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "هەموو کەناڵەکان"
    AppLanguage.KURDISH_BADINI -> "هەمی کەناڵ"
    AppLanguage.ARABIC -> "جميع القنوات"
    AppLanguage.ENGLISH -> "All Channels"
  }

  fun favorites(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "دڵخوازەکان"
    AppLanguage.KURDISH_BADINI -> "کەناڵێن حەزژێکری"
    AppLanguage.ARABIC -> "المفضلة"
    AppLanguage.ENGLISH -> "Favorites"
  }

  fun categories(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "هاوپۆلەکان"
    AppLanguage.KURDISH_BADINI -> "پۆلین"
    AppLanguage.ARABIC -> "الفئات"
    AppLanguage.ENGLISH -> "Categories"
  }

  fun searchPlaceholder(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "گەڕان بەدوای کەناڵدا..."
    AppLanguage.KURDISH_BADINI -> "لێگەڕیان ل کەناڵان..."
    AppLanguage.ARABIC -> "البحث عن قناة..."
    AppLanguage.ENGLISH -> "Search channels..."
  }

  fun refresh(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "نوێکردنەوە"
    AppLanguage.KURDISH_BADINI -> "نویکرن"
    AppLanguage.ARABIC -> "تحديث"
    AppLanguage.ENGLISH -> "Refresh"
  }

  fun loadingChannels(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "کەناڵەکان باردەکرێن..."
    AppLanguage.KURDISH_BADINI -> "کەناڵ دهێنە بارکرن..."
    AppLanguage.ARABIC -> "جاري تحميل القنوات..."
    AppLanguage.ENGLISH -> "Loading channels..."
  }

  fun errorLoading(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "نەتوانرا لیستی کەناڵەکان داببەزێنرێت"
    AppLanguage.KURDISH_BADINI -> "لیستێ کەناڵان نەهاتە بارکرن"
    AppLanguage.ARABIC -> "تعذر تحميل قائمة القنوات"
    AppLanguage.ENGLISH -> "Failed to load channel list"
  }

  fun retry(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "دووبارە هەوڵبدەرەوە"
    AppLanguage.KURDISH_BADINI -> "جارەکادی بپەژڕینە"
    AppLanguage.ARABIC -> "إعادة المحاولة"
    AppLanguage.ENGLISH -> "Try Again"
  }

  fun noChannelsFound(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "هیچ کەناڵێک نەدۆزرایەوە"
    AppLanguage.KURDISH_BADINI -> "چ کەناڵ نەهاتنە دیتن"
    AppLanguage.ARABIC -> "لم يتم العثور على أي قناة"
    AppLanguage.ENGLISH -> "No channels found"
  }

  // Player
  fun liveBadge(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "پەخشی ڕاستەوخۆ"
    AppLanguage.KURDISH_BADINI -> "پەخشی ئێکسەر"
    AppLanguage.ARABIC -> "مباشر"
    AppLanguage.ENGLISH -> "LIVE"
  }

  fun streamOffline(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "پەخشەکە لەکارکەوتووە یان بەردەست نییە"
    AppLanguage.KURDISH_BADINI -> "پەخش هاتیە ڕاگرتن یان بەردەست نینە"
    AppLanguage.ARABIC -> "البث غير متوفر حالياً أو متوقف"
    AppLanguage.ENGLISH -> "Stream is offline or unavailable"
  }

  fun streamOfflineHint(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "تکایە هەوڵی نوێکردنەوە بدە یان کەناڵێکی تر هەڵبژێرە"
    AppLanguage.KURDISH_BADINI -> "هیڤیدارین جارەکادی بپەژڕینە یان کەناڵەکێ دی هەلبژێرە"
    AppLanguage.ARABIC -> "يرجى إعادة المحاولة أو الانتقال إلى قناة أخرى"
    AppLanguage.ENGLISH -> "Please reload or select another channel"
  }

  fun reloadStream(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "نوێکردنەوەی پەخش"
    AppLanguage.KURDISH_BADINI -> "نویکرنا پەخشی"
    AppLanguage.ARABIC -> "إعادة تشغيل البث"
    AppLanguage.ENGLISH -> "Reload Stream"
  }

  fun previousChannel(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "کەناڵی پێشوو"
    AppLanguage.KURDISH_BADINI -> "کەناڵێ بەرێ"
    AppLanguage.ARABIC -> "القناة السابقة"
    AppLanguage.ENGLISH -> "Previous Channel"
  }

  fun nextChannel(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "کەناڵی داهاتوو"
    AppLanguage.KURDISH_BADINI -> "کەناڵێ دی"
    AppLanguage.ARABIC -> "القناة التالية"
    AppLanguage.ENGLISH -> "Next Channel"
  }

  fun aspectRatio(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "ڕێژەی ڕوونما"
    AppLanguage.KURDISH_BADINI -> "قەبارێ شاشێ"
    AppLanguage.ARABIC -> "أبعاد الشاشة"
    AppLanguage.ENGLISH -> "Aspect Ratio"
  }

  fun fullscreen(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "تەواوی شاشە"
    AppLanguage.KURDISH_BADINI -> "تەمامیا شاشێ"
    AppLanguage.ARABIC -> "ملء الشاشة"
    AppLanguage.ENGLISH -> "Fullscreen"
  }

  fun close(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "داخستن"
    AppLanguage.KURDISH_BADINI -> "گرتن"
    AppLanguage.ARABIC -> "إغلاق"
    AppLanguage.ENGLISH -> "Close"
  }

  fun addToFav(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "زیادکردن بۆ دڵخوازەکان"
    AppLanguage.KURDISH_BADINI -> "زێدەکرن بۆ حەزژێکرییان"
    AppLanguage.ARABIC -> "إضافة إلى المفضلة"
    AppLanguage.ENGLISH -> "Add to Favorites"
  }

  fun removeFromFav(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "لابردن لە دڵخوازەکان"
    AppLanguage.KURDISH_BADINI -> "لابردن ژ حەزژێکرییان"
    AppLanguage.ARABIC -> "إزالة من المفضلة"
    AppLanguage.ENGLISH -> "Remove from Favorites"
  }

  // Admin Dashboard
  fun adminDashboard(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "بەڕێوەبەرایەتیی کەناڵەکان (Admin)"
    AppLanguage.KURDISH_BADINI -> "ڕێڤەبەریا کەناڵان (Admin)"
    AppLanguage.ARABIC -> "لوحة تحكم المشرف (Admin)"
    AppLanguage.ENGLISH -> "Admin Dashboard"
  }

  fun adminNotice(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "بەشی پارێزراوی بەڕێوەبەر: لە رێگەی یاساکانی پاراستنی فایەربەیسەوە کۆنترۆڵ دەکرێت. بەکارهێنەرانی ئاسایی ناتوانن کەناڵەکان دەستکاری بکەن."
    AppLanguage.KURDISH_BADINI -> "پشکێ پاراستی یێ ئەدمینی: ب رێکا یاسایێن پاراستنا فایەربەیسێ دهێتە کۆنترۆلکرن."
    AppLanguage.ARABIC -> "منطقة الإدارة الآمنة: محمية عبر قواعد أمان Firebase Firestore لمنع التلاعب غير المصرح به."
    AppLanguage.ENGLISH -> "Protected Admin Area: Secured by Firebase Authentication and server-side Firestore Security Rules."
  }

  fun addChannel(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "زیادکردنی کەناڵی نوێ"
    AppLanguage.KURDISH_BADINI -> "زێدەکرنا کەناڵێ نوو"
    AppLanguage.ARABIC -> "إضافة قناة جديدة"
    AppLanguage.ENGLISH -> "Add New Channel"
  }

  fun editChannel(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "دەستکاریکردنی کەناڵ"
    AppLanguage.KURDISH_BADINI -> "دەستکاریکرنا کەناڵی"
    AppLanguage.ARABIC -> "تعديل القناة"
    AppLanguage.ENGLISH -> "Edit Channel"
  }

  fun deleteChannel(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "سڕینەوەی کەناڵ"
    AppLanguage.KURDISH_BADINI -> "ژێبرنا کەناڵی"
    AppLanguage.ARABIC -> "حذف القناة"
    AppLanguage.ENGLISH -> "Delete Channel"
  }

  fun channelName(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "ناوی کەناڵ"
    AppLanguage.KURDISH_BADINI -> "ناڤێ کەناڵی"
    AppLanguage.ARABIC -> "اسم القناة"
    AppLanguage.ENGLISH -> "Channel Name"
  }

  fun streamUrl(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "بەستەری پەخش (Stream URL / m3u8)"
    AppLanguage.KURDISH_BADINI -> "لینکێ پەخشی (Stream URL / m3u8)"
    AppLanguage.ARABIC -> "رابط البث (Stream URL / m3u8)"
    AppLanguage.ENGLISH -> "Stream URL (.m3u8 / http)"
  }

  fun logoUrl(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "بەستەری لۆگۆ (Logo URL)"
    AppLanguage.KURDISH_BADINI -> "لینکێ لۆگۆیێ"
    AppLanguage.ARABIC -> "رابط الشعار (Logo URL)"
    AppLanguage.ENGLISH -> "Logo URL"
  }

  fun categoryLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "هاوپۆل (Category)"
    AppLanguage.KURDISH_BADINI -> "پۆل (Category)"
    AppLanguage.ARABIC -> "الفئة (Category)"
    AppLanguage.ENGLISH -> "Category"
  }

  fun save(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "پاشەکەوتکردن"
    AppLanguage.KURDISH_BADINI -> "پاراستن"
    AppLanguage.ARABIC -> "حفظ"
    AppLanguage.ENGLISH -> "Save"
  }

  fun cancel(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "پاشگەزبوونەوە"
    AppLanguage.KURDISH_BADINI -> "پاشگەزبوون"
    AppLanguage.ARABIC -> "إلغاء"
    AppLanguage.ENGLISH -> "Cancel"
  }

  fun resetDefaults(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "گەڕاندنەوە بۆ کەناڵە سەرەکییەکان"
    AppLanguage.KURDISH_BADINI -> "زڤڕاندن بۆ کەناڵێن سەرەکی"
    AppLanguage.ARABIC -> "استعادة القنوات الأصلية"
    AppLanguage.ENGLISH -> "Restore Original Channels"
  }

  // Settings & About
  fun settings(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "ڕێکخستنەکان"
    AppLanguage.KURDISH_BADINI -> "ڕێکخستن"
    AppLanguage.ARABIC -> "الإعدادات"
    AppLanguage.ENGLISH -> "Settings"
  }

  fun languageSetting(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "زمانی بەرنامە"
    AppLanguage.KURDISH_BADINI -> "زمانێ بەرنامەی"
    AppLanguage.ARABIC -> "لغة التطبيق"
    AppLanguage.ENGLISH -> "App Language"
  }

  fun tvRemoteMode(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "شێوازی کۆنترۆڵی تەلەڤزیۆن (TV Remote)"
    AppLanguage.KURDISH_BADINI -> "شێوازێ کۆنترۆلا تلەفزیۆنێ (TV Remote)"
    AppLanguage.ARABIC -> "وضع جهاز التحكم (ريموت TV)"
    AppLanguage.ENGLISH -> "TV Remote Navigation"
  }

  fun tvRemoteHint(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "پشتگیری تەواوی فلیکان و کۆنترۆڵی Android TV Box"
    AppLanguage.KURDISH_BADINI -> "پشتەڤانیا تەمام یا کۆنترۆلا Android TV Box"
    AppLanguage.ARABIC -> "دعم كامل لأجهزة Android TV Box وأزرار الاتجاهات"
    AppLanguage.ENGLISH -> "Full D-pad & Remote focus support for TV Box"
  }

  fun aboutDanarTv(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "دەربارەی DANAR TV"
    AppLanguage.KURDISH_BADINI -> "دەربارەی DANAR TV"
    AppLanguage.ARABIC -> "حول DANAR TV"
    AppLanguage.ENGLISH -> "About DANAR TV"
  }

  fun developerInfo(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "پەرەپێدەر: danar"
    AppLanguage.KURDISH_BADINI -> "گەشەپێدەر: danar"
    AppLanguage.ARABIC -> "المطور: danar"
    AppLanguage.ENGLISH -> "Developer: danar"
  }

  fun targetDevices(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "ئامێرەکان: مۆبایلی ئەندرۆید، Android TV، و Android TV Box"
    AppLanguage.KURDISH_BADINI -> "ئامیر: مۆبایلا ئەندرۆید، Android TV، و Android TV Box"
    AppLanguage.ARABIC -> "الأجهزة: هواتف أندرويد، شاشات Android TV، وأجهزة TV Box"
    AppLanguage.ENGLISH -> "Target: Android Phones, Android TV, and Android TV Box"
  }

  fun logout(lang: AppLanguage) = when (lang) {
    AppLanguage.KURDISH_SORANI -> "دەرچوون"
    AppLanguage.KURDISH_BADINI -> "دەڕکەفتن"
    AppLanguage.ARABIC -> "تسجيل الخروج"
    AppLanguage.ENGLISH -> "Sign Out"
  }
}
