package com.example.customcomponent

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp



@Composable
fun Animate10(){
    val infiniteTransition = rememberInfiniteTransition()
    val color by infiniteTransition.animateColor(
        initialValue = Color.Red,
        targetValue = Color.Green,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(Modifier.fillMaxSize().background(color))
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Animate9(){
    var selected by remember { mutableStateOf(false) }
// Animates changes when `selected` is changed.
    val transition = updateTransition(selected, label = "selected state")
    val borderColor by transition.animateColor(label = "border color") { isSelected ->
        if (isSelected) Color.Magenta else Color.White
    }
    val elevation by transition.animateDp(label = "elevation") { isSelected ->
        if (isSelected) 10.dp else 2.dp
    }

    Column {

        Button(onClick = {
            selected = !selected
        }, modifier = Modifier.padding(all = 20.dp)) {
            Text(text = "click me")
        }
        Surface(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, borderColor),
            tonalElevation = elevation
        ) {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
                Text(text = "Hello, world!")
                // AnimatedVisibility as a part of the transition.
                transition.AnimatedVisibility(
                    visible = { targetSelected -> targetSelected },
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Text(text = "It is fine today.")
                }
                // AnimatedContent as a part of the transition.
                transition.AnimatedContent { targetState ->
                    if (targetState) {
                        Text(text = "Selected")
                    } else {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = "Phone")
                    }
                }
            }
        }
    }
}

@Composable
fun Animate8() {
    var state by remember { mutableStateOf(true) }

    Column {

        Button(onClick = {
            state = !state
        }, modifier = Modifier.padding(all = 20.dp)) {
            Text(text = "click me")
        }
        Crossfade(targetState = state) { screen ->
            when (screen) {
                true -> Text("Page A")
                false -> Text("Page B")
            }
        }
    }
}

@Composable
fun Animate7(){
    var state by remember { mutableStateOf(true) }

    Column {

        Button(onClick = {
            state = !state
        }) {
            Text(text = "click me")
        }
        Box(
            modifier = Modifier
                .animateContentSize()
        ) { Text(text = if (state) "verdfdlksfjs fdfjsldf fsdfjsdlakf" else "fjsdlf dfjsdfsd ", modifier = Modifier.padding(all = 20.dp)) }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Animate1(){
    var visible by remember { mutableStateOf(true) }

    Column {
        Button(onClick = {
            visible = !visible
        }) {
            Text(text = "click me")
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            // Fade in/out the background and the foreground.
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)) {
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .animateEnterExit(
                            // Slide in/out the inner box.
                            enter = slideInVertically(),
                            exit = slideOutVertically()
                        )
                        .sizeIn(minWidth = 256.dp, minHeight = 64.dp)
                        .background(Color.Red)
                ) {
                    // Content of the notification…
                }
            }
        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Animate2() {
    var visible by remember { mutableStateOf(true) }

    Column {
        Button(onClick = {
            visible = !visible
        }) {
            Text(text = "click me")
        }


        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) { // this: AnimatedVisibilityScope
            // Use AnimatedVisibilityScope#transition to add a custom animation
            // to the AnimatedVisibility.
            val background by transition.animateColor(label = "color") { state ->
                if (state == EnterExitState.Visible) Color.Blue else Color.Yellow
            }
            Box(modifier = Modifier
                .size(128.dp)
                .background(background))
        }
    }


}

@Composable
fun Animate3() {
    var enabled by remember { mutableStateOf(true) }

    Column {

        Button(onClick = {
            enabled = !enabled
        }) {
            Text(text = "click me")
        }

        val alpha: Float by animateFloatAsState(if (enabled) 1f else 0.5f)
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer(alpha = alpha)
                .background(Color.Red)
        )
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Animate4(){
    Row {
        var count by remember { mutableStateOf(0) }
        Button(onClick = { count++ }) {
            Text("Add")
        }
        AnimatedContent(targetState = count) { targetCount ->
            // Make sure to use `targetCount`, not `count`.
            Text(text = "Count: $targetCount")
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Animate5(){
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
        var count by remember { mutableStateOf(0) }
        Button(onClick = { count++ }) {
            Text("Add")
        }
        Button(onClick = { count-- }) {
            Text("remove")
        }
        Box(modifier = Modifier
            .border(2.dp, Color.DarkGray)
            .padding(all = 20.dp)){

            AnimatedContent(
                targetState = count,
                transitionSpec = {
                    // Compare the incoming number with the previous number.
                    if (targetState > initialState) {
                        // If the target number is larger, it slides up and fades in
                        // while the initial (smaller) number slides up and fades out.
                        slideInVertically { height -> height } + fadeIn() with
                                slideOutVertically { height -> -height } + fadeOut()
                    } else {
                        // If the target number is smaller, it slides down and fades in
                        // while the initial number slides down and fades out.
                        slideInVertically { height -> -height } + fadeIn() with
                                slideOutVertically { height -> height } + fadeOut()
                    }.using(
                        // Disable clipping since the faded slide-in/out should
                        // be displayed out of bounds.
                        SizeTransform(clip = false)
                    )
                }
            ) { targetCount ->
                Text(text = "$targetCount")
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Animate6() {
    var expanded by remember { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.primary,
        onClick = { expanded = !expanded }
    ) {
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                fadeIn(animationSpec = tween(150, 150)) with
                        fadeOut(animationSpec = tween(150)) using
                        SizeTransform { initialSize, targetSize ->
                            if (targetState) {
                                keyframes {
                                    // Expand horizontally first.
                                    IntSize(targetSize.width, initialSize.height) at 150
                                    durationMillis = 300
                                }
                            } else {
                                keyframes {
                                    // Shrink vertically first.
                                    IntSize(initialSize.width, targetSize.height) at 150
                                    durationMillis = 300
                                }
                            }
                        }
            }
        ) { targetExpanded ->
            if (targetExpanded) {
                Expanded()

            } else {
                ContentIcon()
            }
        }
    }
}

@Composable
fun ContentIcon() {
    Column(modifier = Modifier.padding(all = 20.dp)) {

        Icon(Icons.Default.Call, "menu", modifier = Modifier
            .width(24.dp)
            .height(24.dp))
    }
}

@Composable
fun Expanded() {
    Text(text = "abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc abc",
        modifier = Modifier.padding(all = 20.dp)
        )
}

