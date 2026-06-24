# LyraApp - Tipografi Sistemi

> Bu dosya LyraApp isimli uygulamanın tipografi ölçeği için
> **tek doğruluk kaynağıdır** (single source of truth) ve
> doğrudan bir **Android Jetpack Compose** projesinde kullanılmak
> üzere düzenlenmiştir.

---

## 1. Temel Kurallar

### 1.1. Merkezi Yönetim
Hiçbir `@Composable` içerisinde ham `TextStyle(...)` tanımlanmaz. Tüm metin stilleri daima `MaterialTheme.typography.<slot>` üzerinden okunmak zorundadır.

### 1.2. Font Ailesi (Roboto)
Projenin ana font ailesi **Roboto** olarak belirlenmiştir. Sistem fontu ne olursa olsun, uygulama içerisinde Roboto kullanımı zorunludur. Tasarımın tutarlılığı için tüm metin bileşenleri bu font ailesini referans almalıdır.

---

## 2. Type.kt — Tipografi Tanımları

Aşağıdaki tanımlamalar Material 3 standart tipografi ölçeğini Roboto fontu ile LyraApp projesi için yapılandırır. Bu kod bloğu `app/src/main/java/com/nimetatila/lyraapp/ui/theme/Type.kt` dosyasına temel teşkil eder.

```kotlin
package com.nimetatila.lyraapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Roboto Font Tanımı
val RobotoFontFamily = FontFamily.Default 

val LyraTypography = Typography(
    // Display: Büyük hero alanları ve sayısal vurgular
    displayLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),

    // Headline: Sayfa ve bölüm başlıkları
    headlineLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),

    // Title: Uygulama çubuğu ve liste başlıkları
    titleLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),

    // Body: Metin içerikleri ve uzun okumalar
    bodyLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),

    // Label: Butonlar, küçük etiketler ve alt bilgiler
    labelLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelSmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

---

## 3. Theme.kt Entegrasyonu

Tipografi sistemi `MaterialTheme` nesnesine aşağıdaki şekilde bağlanır. Bu işlem, renk sistemi dökümanında (`00-color-system.md`) belirtilen tema yapısı ile uyumlu olmalıdır.

```kotlin
@Composable
fun LyraAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Renk şeması seçimi (00-color-system.md referans alınmıştır)
    val colorScheme = if (darkTheme) LyraDarkColors else LyraLightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = LyraTypography,
        content = content
    )
}
```

---

## 4. Kullanım Rehberi

Arayüz bileşenlerinde metin stilleri şu şekilde çağrılmalıdır:

```kotlin
// Başlık Kullanımı
Text(
    text = "Yeni Şarkılar",
    style = MaterialTheme.typography.headlineLarge
)

// Gövde Metni Kullanımı
Text(
    text = "Uygulama üzerinden binlerce şarkıya erişebilirsiniz.",
    style = MaterialTheme.typography.bodyMedium
)

// Buton Metni Kullanımı
Button(onClick = { /* ... */ }) {
    Text(
        text = "Oynat",
        style = MaterialTheme.typography.labelLarge
    )
}
```
