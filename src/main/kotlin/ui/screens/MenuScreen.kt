package ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import data.SnakeManager
import data.repository.GameRepository
import ui.items.Background

@Suppress("FunctionName")
@Composable
fun Menu(
    snakeManager: SnakeManager,
    gameRepository: GameRepository,
    onNavigate: (String) -> Unit
) {
    var tfText by remember { mutableStateOf("") }
    val MAX_NAME_LENGTH = 20
    var checked by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Background(
            snakeManager
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .width(230.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Turn background on/off",
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                )
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        snakeManager.toggleBg()
                        checked = !checked
                    }
                )
            }

            TextField(
                value = tfText,
                onValueChange = { newValue ->
                    if (newValue.length <= MAX_NAME_LENGTH || newValue.length < tfText.length) {
                        tfText = newValue
                    }
                },
                modifier = Modifier
                    .background(Color.White),
                textStyle = TextStyle(textAlign = TextAlign.Center),
                maxLines = 1,
                label = { Text(
                    text = "Name:"
                ) }
            )
            Row(

            ) {
                Button(
                    onClick = { if (tfText != "")  {
                        val allUsers = gameRepository.getUsers()
                        val userByName = allUsers.find { it.name == tfText }
                        val userId = if(userByName != null) {
                            userByName.userId
                        } else {
                            gameRepository.insertUser(tfText)
                            val updatedUsers = gameRepository.getUsers()
                            val newUser = updatedUsers.find { it.name == tfText }
                            newUser?.userId
                        }
                        if (userId != null) {
                            snakeManager.currentPlayer = userId
                            onNavigate("Game")
                            snakeManager.startMoving()
                        }
                    }
                              },
                    modifier = Modifier
                        .padding(5.dp)
                ) {
                    Text(
                        text = "PLAY"
                    )
                }
                Button(
                    onClick = { onNavigate("Scores") },
                    modifier = Modifier
                        .padding(5.dp)
                ) {
                    Text(
                        text = "TOP SCORES"
                    )
                }
            }
        }

    }
}

