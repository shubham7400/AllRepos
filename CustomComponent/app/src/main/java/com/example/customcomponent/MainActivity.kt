package com.example.customcomponent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.customcomponent.ui.DrawWithContent
import com.example.customcomponent.ui.MainViewModel
import com.example.customcomponent.ui.theme.CustomComponentTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomComponentTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Animate10()
                }
            }
        }
    }



    @Composable
    fun DrawGradient() {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(all = 20.dp)) {

            Text(
                "Hello Compose!",
                style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 40.sp, fontWeight = FontWeight.W400),
                modifier = Modifier
                    .drawWithCache {
                        val brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF9E82F0),
                                Color(0xFF42A5F5)
                            )
                        )
                        onDrawBehind {
                            drawRoundRect(
                                brush,
                                cornerRadius = CornerRadius(10.dp.toPx())
                            )
                        }
                    }
                    .padding(all = 10.dp)
            )
        }

    }

    @Composable
    fun TransparentPointer() {
        var pointerOffset by remember {
            mutableStateOf(Offset(0f, 0f))
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput("dragging") {
                    detectDragGestures { change, dragAmount ->
                        pointerOffset += dragAmount
                    }
                }
                .onSizeChanged {
                    pointerOffset = Offset(it.width / 2f, it.height / 2f)
                }
                .drawWithContent {
                    drawContent()
                    // draws a fully black area with a small keyhole at pointerOffset that’ll show part of the UI.
                    drawRect(
                        Brush.radialGradient(
                            listOf(Color.Transparent, Color.Black),
                            center = pointerOffset,
                            radius = 100.dp.toPx(),
                        )
                    )
                }
        ) {
            // Your composables here
        }
    }

    @Composable
    fun DrawBehind() {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                "Hello Compose!",
                modifier = Modifier
                    .padding(all = 20.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = Color.Red,
                            cornerRadius = CornerRadius(10.dp.toPx())
                        )
                    }
                    .padding(4.dp)
            )
        }

    }

    @Composable
    private fun ConfigureUi(mainViewModel: MainViewModel = viewModel()) {
        val sweepAngle = mainViewModel.uiState.collectAsState()
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            DrawWithContent(angle = sweepAngle.value)
            TextField(
                value = sweepAngle.value.toString(),
                onValueChange = {
                    val angle = if (it.isEmpty()) 0 else it.toInt()
                    mainViewModel.updateSweepAngle(angle)
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = { Text(text = "Your Label") },
                placeholder = { Text(text = "Your Placeholder/Hint") },
            )
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CustomComponentTheme {

    }
}