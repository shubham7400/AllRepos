package com.example.customcomponent.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun DrawWithContent(angle: Int) {
    val sweepAngle by animateFloatAsState(
        targetValue = angle.toFloat(),
        animationSpec = tween(1000)
    )

    Column(
        modifier = Modifier.aspectRatio(1f).padding(20.dp)
            .drawWithContent {
                drawContent()
                // draws a fully black area with a small keyhole at pointerOffset that’ll show part of the UI.
                drawArc(
                    brush = Brush.radialGradient(
                        listOf(Color.White, Color.Green, Color.Yellow, Color.Magenta, Color.Red, Color.Cyan, Color.Black),
                        radius = size.height/2,
                    ),
                    useCenter = true,
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                )
            }
    ) {
        // Your composables here
    }

}

@Preview
@Composable
fun PreviewDrawWithContent() {
    DrawWithContent(220)
}