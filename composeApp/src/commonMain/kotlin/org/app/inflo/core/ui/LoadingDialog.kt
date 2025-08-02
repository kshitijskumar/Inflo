package org.app.inflo.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.app.inflo.core.theme.AppTheme

@Composable
fun LoadingDialog(
    loadingText: String = "Loading..."
) {
    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(AppTheme.dimens.medium4)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppTheme.dimens.medium3),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(color = AppTheme.color.baseRed)
                Spacer(modifier = Modifier.width(AppTheme.dimens.medium4))
                Text(
                    text = loadingText,
                    fontSize = 14.sp,
                    color = AppTheme.color.black60
                )
            }
        }
    }
}
