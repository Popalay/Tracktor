package com.popalay.tracktor.ui.widget

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.ui.animation.animate
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.foundation.Border
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentColorAmbient
import androidx.ui.foundation.clickable
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.graphics.Shape
import androidx.ui.layout.ExperimentalLayout
import androidx.ui.layout.FlowRow
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.SizeMode
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp

@OptIn(ExperimentalLayout::class)
@Composable
fun ChipGroup(
    children: @Composable () -> Unit
) {
    FlowRow(
        mainAxisSize = SizeMode.Expand,
        mainAxisSpacing = 16.dp,
        crossAxisSpacing = 8.dp,
        mainAxisAlignment = MainAxisAlignment.Center
    ) {
        children()
    }
}

@Composable
fun Chip(
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    shape: Shape = CircleShape,
    activeColor: Color = MaterialTheme.colors.secondary,
    inactiveColor: Color = EmphasisAmbient.current.disabled.applyEmphasis(activeColor),
    contentColor: Color = MaterialTheme.colors.onSecondary,
    bordered: Boolean = false,
    content: @Composable () -> Unit
) {
    val color = animate(if (isSelected) activeColor else inactiveColor)

    Box(
        shape = shape,
        backgroundColor = if (bordered) Color.Transparent else color,
        border = if (bordered) Border(2.dp, color) else null,
        paddingBottom = 4.dp,
        paddingTop = 4.dp,
        paddingStart = 16.dp,
        paddingEnd = 16.dp,
        gravity = Alignment.Center,
        modifier = Modifier.clip(shape).clickable(onClick = onClick)
    ) {
        Providers(ContentColorAmbient provides contentColor, children = content)
    }
}