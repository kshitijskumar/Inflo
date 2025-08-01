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
    val secondaryRed: Color = baseRed.copy(alpha = 0.1f)
)

data class AppDimens(
    val small0: Dp = 1.dp,
    val small1: Dp = 2.dp,
    val medium1: Dp = 8.dp,
    val medium2: Dp = 12.dp,
    val medium3: Dp = 16.dp,
    val medium4: Dp = 24.dp,
)