package org.app.inflo.core.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object AppTheme {

    val color = AppColor()
    val dimens = AppDimens()

}

data class AppColor(
    val baseRed: Color = Color(0xFFFF5758)
)

data class AppDimens(
    val medium1: Dp = 8.dp,
    val medium2: Dp = 12.dp,
    val medium3: Dp = 16.dp,
)