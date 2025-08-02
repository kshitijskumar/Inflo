package org.app.inflo.core.ui

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.app.inflo.core.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = true,
    maxLines: Int = 1,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = RoundedCornerShape(4.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.appColors(),
) {
    // If color is not provided via the text style, use content color as a default
    val textColor = textStyle.color.takeOrElse {
        textColor(colors, enabled, isError, interactionSource).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))

    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        OutlinedTextField(
            value = value,
            modifier = modifier,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            textStyle = mergedTextStyle,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = singleLine,
            maxLines = maxLines,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            shape = shape,
            colors = colors
        )
    }
}

typealias DecorationBoxComposable = @Composable (innerTextField: @Composable () -> Unit) -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun defaultDecorationBox(
    value: String,
    visualTransformation: VisualTransformation,
    label: @Composable (() -> Unit)?,
    placeholder: @Composable (() -> Unit)?,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    isError: Boolean,
    singleLine: Boolean,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    colors: TextFieldColors
): DecorationBoxComposable {
    val decorationBox: DecorationBoxComposable = @Composable { innerTextField ->
        // places leading icon, text field with label and placeholder, trailing icon
        TextFieldDefaults.DecorationBox(
            value = value,
            visualTransformation = visualTransformation,
            innerTextField = innerTextField,
            placeholder = placeholder,
            label = label,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            enabled = enabled,
            isError = isError,
            interactionSource = interactionSource,
            colors = colors,
            contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 8.dp)
        )
    }

    return decorationBox
}

@Composable
private fun textColor(
    colors: TextFieldColors,
    enabled: Boolean,
    isError: Boolean,
    interactionSource: InteractionSource
): State<Color> {
    val focused by interactionSource.collectIsFocusedAsState()

    val targetValue = when {
        !enabled -> colors.disabledTextColor
        isError -> colors.errorTextColor
        focused -> colors.focusedTextColor
        else -> colors.unfocusedTextColor
    }
    return rememberUpdatedState(targetValue)
}

@Composable
fun OutlinedTextFieldDefaults.appColors(
    focusedTextColor: Color = AppTheme.color.black,
    unfocusedTextColor: Color = AppTheme.color.black,
    disabledTextColor: Color = AppTheme.color.black.copy(alpha = 0.5f),
    focusedContainerColor: Color = AppTheme.color.white,
    unfocusedContainerColor: Color = AppTheme.color.white,
    disabledContainerColor: Color = AppTheme.color.white,
    errorLabelColor: Color = AppTheme.color.black.copy(alpha = 0.4f),
    focusedLabelColor: Color = AppTheme.color.black.copy(alpha = 0.4f),
    disabledLabelColor: Color = AppTheme.color.black.copy(alpha = 0.4f),
    unfocusedLabelColor: Color = AppTheme.color.black.copy(alpha = 0.4f),
    unfocusedBorderColor: Color = AppTheme.color.black40,
    focusedBorderColor: Color = AppTheme.color.black,
    disabledBorderColor: Color = AppTheme.color.black40,
    errorBorderColor: Color = AppTheme.color.black,
    cursorColor: Color = AppTheme.color.baseRed,
    errorCursorColor: Color = AppTheme.color.baseRed,
    errorContainerColor: Color = Color.Transparent,
): TextFieldColors {
    return colors(
        focusedTextColor = focusedTextColor,
        unfocusedTextColor = unfocusedTextColor,
        disabledTextColor = disabledTextColor,
        focusedContainerColor = focusedContainerColor,
        unfocusedContainerColor = unfocusedContainerColor,
        disabledContainerColor = disabledContainerColor,
        errorLabelColor = errorLabelColor,
        focusedLabelColor = focusedLabelColor,
        disabledLabelColor = disabledLabelColor,
        unfocusedLabelColor = unfocusedLabelColor,
        cursorColor = cursorColor,
        errorCursorColor = errorCursorColor,
        errorContainerColor = errorContainerColor,
        focusedBorderColor = focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        disabledBorderColor = disabledBorderColor,
        errorBorderColor = errorBorderColor,
    )
}