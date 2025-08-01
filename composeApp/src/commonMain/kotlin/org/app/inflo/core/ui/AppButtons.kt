package org.app.inflo.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import org.app.inflo.core.theme.AppTheme

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.color.baseRed
        )
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = AppTheme.color.white
        )
    }
}

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.color.secondaryRed
        ),
        border = BorderStroke(
            width = AppTheme.dimens.small0,
            color = AppTheme.color.baseRed
        )
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = AppTheme.color.baseRed
        )
    }
}