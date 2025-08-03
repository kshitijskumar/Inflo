package org.app.inflo.core.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppTheme {

    val color = AppColor()
    val dimens = AppDimens()

}

data class AppColor(
    val baseRed: Color = Color(0xFFFF5758),
    val white: Color = Color(0xFFFFFFFF),
    val secondaryRed: Color = baseRed.copy(alpha = 0.1f),
    val black: Color = Color(0xFF000000),
    val black40: Color = Color(0xFF000000).copy(alpha = 0.4f),
    val black60: Color = Color(0xFF000000).copy(alpha = 0.6f),
    val background: Color = Color(0xFFFAFAFF),
    val basePurple: Color = Color(0xFF6750A4)
)

data class AppDimens(
    val small0: Dp = 1.dp,
    val small1: Dp = 2.dp,
    val small2: Dp = 4.dp,
    val medium1: Dp = 8.dp,
    val medium2: Dp = 12.dp,
    val medium3: Dp = 16.dp,
    val medium4: Dp = 24.dp,
    val medium5: Dp = 32.dp,
    val large1: Dp = 48.dp,
    val large2: Dp = 64.dp,
)