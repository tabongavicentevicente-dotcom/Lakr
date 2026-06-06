package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.R
import com.example.data.model.LaKrAiMessage
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoupleViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaKrAiScreen(viewModel: CoupleViewModel) {
    val aiMessages by viewModel.aiMessagesState.collectAsState()
    val isAiLoading by viewModel.aiLoading.collectAsState()
    val activeUsername by viewModel.loginUsername.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var promptInput by remember { mutableStateOf("") }

    // Initialize welcome message if empty
    LaunchedEffect(Unit) {
        viewModel.initAiWelcomeMessageIfNeeded()
    }

    // Scroll to latest message when they arrive or start loading
    LaunchedEffect(aiMessages.size, isAiLoading) {
        if (aiMessages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(aiMessages.size - 1)
            }
        }
    }

    val suggestions = listOf(
        "🥂 Sugerir um date fofo" to "Sugerir um date romântico e criativo",
        "🌸 Criar uma poesia" to "Criar um poema fofo sobre o nosso amor",
        "📅 Quantos dias juntos?" to "Me lembrar o nosso aniversário de namoro e calcular quantos dias estamos juntos!",
        "🏆 Nossas metas" to "Quais metas temos cadastradas e como podemos planejá-las?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("lakr_ai_screen")
    ) {
        // --- Header Block ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(RosePrimary.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // AI Glowing Emblem
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(RosePrimary, GoldMetallic)
                                )
                            )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_lakr_logo),
                            contentDescription = "LaKr IA",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "LaKr IA",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CocoaDark
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50)) // Connected green status dot
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Assistente Virtual Exclusiva",
                                style = MaterialTheme.typography.bodySmall,
                                color = RoseGray
                            )
                        }
                    }
                }

                // Clear Chat button
                IconButton(
                    onClick = { viewModel.clearLaKrAiChat() },
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("lakr_ai_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.ClearAll,
                        contentDescription = "Limpar conversa",
                        tint = RoseGray
                    )
                }
            }
        }

        HorizontalDivider(color = RoseTertiary.copy(alpha = 0.5f), thickness = 1.dp)

        // --- Chat Area ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(aiMessages, key = { it.id }) { message ->
                    AiChatBubble(message = message)
                }

                if (isAiLoading) {
                    item {
                        AiTypingIndicatorBubble()
                    }
                }
            }

            // Scroll to bottom helper button if list scrolled up
            if (listState.firstVisibleItemIndex > 2) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = {
                            coroutineScope.launch {
                                if (aiMessages.isNotEmpty()) {
                                    listState.animateScrollToItem(aiMessages.size - 1)
                                }
                            }
                        },
                        containerColor = RosePrimary,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Rolar para o final")
                    }
                }
            }
        }

        // --- Suggestions Chips Bar ---
        if (aiMessages.size <= 2 && !isAiLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    suggestions.forEach { (label, fullQuery) ->
                        SuggestionChip(
                            onClick = {
                                viewModel.sendMessageToLaKrAi(fullQuery)
                            },
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CocoaDark,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = RoseWhite
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
        }

        // --- Bottom Input Area ---
        Surface(
            tonalElevation = 4.dp,
            color = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.ime)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    placeholder = {
                        Text(
                            text = "Fale com a LaKr IA, ${activeUsername ?: "amor"}...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RoseGray
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .testTag("lakr_ai_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RosePrimary,
                        unfocusedBorderColor = RoseTertiary,
                        focusedContainerColor = RoseBackground.copy(alpha = 0.4f),
                        unfocusedContainerColor = RoseBackground.copy(alpha = 0.4f)
                    ),
                    maxLines = 4,
                    singleLine = false
                )

                IconButton(
                    onClick = {
                        val text = promptInput.trim()
                        if (text.isNotEmpty() && !isAiLoading) {
                            viewModel.sendMessageToLaKrAi(text)
                            promptInput = ""
                        }
                    },
                    enabled = promptInput.isNotBlank() && !isAiLoading,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = if (promptInput.isNotBlank() && !isAiLoading) {
                                Brush.linearGradient(colors = listOf(RosePrimary, GoldMetallic))
                            } else {
                                Brush.linearGradient(colors = listOf(Color.LightGray, Color.LightGray))
                            },
                            shape = CircleShape
                        )
                        .testTag("lakr_ai_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Enviar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AiChatBubble(message: LaKrAiMessage) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Little LaKr AI Badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(end = 8.dp, top = 4.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(RosePrimary, GoldChampagne)
                        )
                    )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_lakr_logo),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        val bubbleShape = if (isUser) {
            RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 2.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 2.dp,
                bottomEnd = 16.dp
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    brush = if (isUser) {
                        Brush.linearGradient(colors = listOf(RosePrimary, RosePrimary.copy(alpha = 0.85f)))
                    } else {
                        Brush.linearGradient(colors = listOf(GoldSoft.copy(alpha = 0.5f), RoseWhite))
                    },
                    shape = bubbleShape
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                if (!isUser) {
                    Text(
                        text = "LaKr IA",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldDark,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                } else {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = White.copy(alpha = 0.85f),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                Text(
                    text = message.messageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else CocoaDark,
                    lineHeight = 20.sp
                )

                Text(
                    text = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date(message.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 9.sp,
                    color = if (isUser) Color.White.copy(alpha = 0.7f) else RoseGray,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AiTypingIndicatorBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(end = 8.dp, top = 4.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(RosePrimary, GoldChampagne)
                    )
                )
        ) {
            Icon(
                imageVector = Icons.Filled.AutoAwesome,
                contentDescription = "",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }

        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(colors = listOf(GoldSoft.copy(alpha = 0.3f), RoseWhite)),
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = 2.dp,
                        bottomEnd = 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "LaKr IA está pensando",
                    style = MaterialTheme.typography.bodySmall,
                    color = RoseGray
                )
                DotAnimation()
            }
        }
    }
}

@Composable
fun DotAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotCount = 3
    val animatedValues = List(dotCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteTransitionSpec(index),
            label = "dot-$index"
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        animatedValues.forEach { value ->
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .alpha(value.value)
                    .background(RosePrimary, CircleShape)
            )
        }
    }
}

private fun infiniteTransitionSpec(index: Int): InfiniteRepeatableSpec<Float> {
    return infiniteRepeatable(
        animation = tween(
            durationMillis = 600,
            delayMillis = index * 150,
            easing = LinearEasing
        ),
        repeatMode = RepeatMode.Reverse
    )
}
