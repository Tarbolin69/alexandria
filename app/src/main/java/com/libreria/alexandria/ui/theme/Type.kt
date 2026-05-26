package com.libreria.alexandria.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.libreria.alexandria.R

object AppFont {
    val Aporetic = FontFamily(
        Font(R.font.aporetic_sans_regular, FontWeight.Normal),
        Font(R.font.aporetic_sans_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.aporetic_sans_bold, FontWeight.Bold),
        Font(R.font.aporetic_sans_bold_italic, FontWeight.Bold, FontStyle.Italic)
    )
}

private val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = AppFont.Aporetic),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = AppFont.Aporetic),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = AppFont.Aporetic),

    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = AppFont.Aporetic),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = AppFont.Aporetic),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = AppFont.Aporetic),

    titleLarge = defaultTypography.titleLarge.copy(fontFamily = AppFont.Aporetic),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = AppFont.Aporetic),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = AppFont.Aporetic),

    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = AppFont.Aporetic),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = AppFont.Aporetic),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = AppFont.Aporetic),

    labelLarge = defaultTypography.labelLarge.copy(fontFamily = AppFont.Aporetic),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = AppFont.Aporetic),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = AppFont.Aporetic)
)