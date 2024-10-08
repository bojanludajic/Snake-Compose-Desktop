package ui.screens

import ui.theme.DarkOliveGreen2
import ui.theme.DarkOliveGreen7
import ui.theme.DarkPurple
import ui.theme.RoyalPurple
import data.SnakeManager
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun Game(
    snakeManager: SnakeManager
) {
    val focusRequester = remember { FocusRequester() }
    var lastDirectionChange by remember { mutableStateOf(0L) }
    val debounceDelay = 150L


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .onKeyEvent { event ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastDirectionChange > debounceDelay) {
                    val newDirection = when (event.key) {
                        Key.W -> 0
                        Key.A -> 1
                        Key.S -> 2
                        Key.D -> 3
                        else -> null
                    }
                    newDirection?.let { newDir ->
                        if (snakeManager.direction != snakeManager.oppositeDirection(newDir)) {
                            snakeManager.direction = newDir
                            lastDirectionChange = currentTime
                        }
                    }
                }
                true
            }
            .focusable()
    ) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        val maxH = maxHeight / 23 * 1.01f

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                for (rowIndex in 0 until 16) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        for (boxIndex in 0 until 25) {
                            val bgColor = if ((boxIndex + rowIndex) % 2 == 0) DarkOliveGreen2 else DarkOliveGreen7
                            val bgColors = listOf(bgColor, Color.Black)
                            Box(
                                modifier = Modifier
                                    .background(bgColors[snakeManager.backGround])
                                    .weight(1f)
                            ) {
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    val radius = size.minDimension / 2f
                                    when {
                                        snakeManager.snakeList.value.first().first == boxIndex && snakeManager.snakeList.value.first().second == rowIndex ->
                                            drawCircle(
                                                color = RoyalPurple,
                                                radius = radius,
                                            )

                                        snakeManager.snakeList.value.contains(Pair(boxIndex, rowIndex)) -> {
                                            drawCircle(
                                                color = DarkPurple,
                                                radius = radius,
                                            )
                                        }

                                        boxIndex == snakeManager.appleX && rowIndex == snakeManager.appleY -> {
                                            drawCircle(
                                                color = Color.Red,
                                                radius = radius,
                                            )
                                        }

                                        else -> Unit
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = if(!snakeManager.gameOver) "SCORE: ${snakeManager.size - 2}" else "",
            style = MaterialTheme.typography.h4,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
                .padding(8.dp),
            color = Color.White,
        )
    }
    if (snakeManager.gameOver) {
        GameEndDialog(snakeManager) {
            snakeManager.gameOver = false
            snakeManager.startMoving()
            snakeManager.resetTimer = 5
        }
    }
}

