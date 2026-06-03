package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ChatMessage
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoupleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messagesState.collectAsState()
    val config by viewModel.configState.collectAsState()

    var inputMessageText by remember { mutableStateOf("") }
    var attachmentSheetVisible by remember { mutableStateOf(false) }
    var selectedPhotoToDelete by remember { mutableStateOf<ChatMessage?>(null) }

    val lazyListState = rememberLazyListState()
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    // Scroll to bottom when a new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lazyListState.animateScrollToItem(messages.size - 1)
        }
    }

    // Romantic Sticker Quick Replies List
    val loveStickers = listOf(
        "Te amo! ❤️",
        "Estou com saudades! 💕",
        "Você é meu sol! ☀️",
        "Um beijo bem doce! 😘",
        "Mal posso esperar pra te ver! 👩‍❤️‍👨"
    )

    // Unsplash Romantic URLs for simulated visual chat attachments
    val attachmentTemplates = listOf(
        Pair("Flores Lindas", "https://images.unsplash.com/photo-1526047932273-341f2a7631f9?q=80&w=400"),
        Pair("Pôr do Sol", "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?q=80&w=400"),
        Pair("Jantar Doce", "https://images.unsplash.com/photo-1544816155-12df9643f363?q=80&w=400"),
        Pair("Chocolate", "https://images.unsplash.com/photo-1511381939415-e44015466834?q=80&w=400"),
        Pair("Par de Alianças", "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=400")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Chat em Dobro", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CocoaDark)
                        Text("Mensagens criptografadas pelo amor", style = MaterialTheme.typography.bodySmall, color = RoseGray)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearChat() }) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Limpar Conversa", tint = RosePrimary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            
            // Empty State
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(RoseWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Sms, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhuma mensagem ainda",
                            style = MaterialTheme.typography.titleMedium,
                            color = CocoaDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Envie uma figurinha fofa ou escreva algo doce para iniciar o dia de seu amor!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = RoseGray
                        )
                    }
                }
            } else {
                // Messages List View
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { message ->
                        val isMe = message.senderName == config.currentActiveUser
                        ChatMessageBubble(
                            msg = message,
                            isMe = isMe,
                            timeString = timeFormatter.format(Date(message.timestamp)),
                            onLongClick = { selectedPhotoToDelete = message }
                        )
                    }
                }
            }

            // Quick Love Sticker strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                loveStickers.forEach { sticker ->
                    OutlinedButton(
                        onClick = { viewModel.sendChatMessage(sticker) },
                        border = BorderStroke(1.dp, RoseTertiary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = RoseWhite
                        ),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(sticker, style = MaterialTheme.typography.bodySmall, color = CocoaDark)
                    }
                }
            }

            // Input Row Bottom
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                border = BorderStroke(1.dp, GoldChampagne),
                shadowElevation = 3.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    // Attachment Icon
                    IconButton(
                        onClick = { attachmentSheetVisible = true },
                        modifier = Modifier.size(48.dp).testTag("attach_btn")
                    ) {
                        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = "Anexar foto do amor", tint = GoldMetallic)
                    }

                    // Text input field
                    OutlinedTextField(
                        value = inputMessageText,
                        onValueChange = { inputMessageText = it },
                        placeholder = { Text("Diga algo fofo...", color = RoseGray) },
                        maxLines = 4,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_text"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    // Send Button
                    IconButton(
                        onClick = {
                            if (inputMessageText.isNotBlank()) {
                                viewModel.sendChatMessage(inputMessageText)
                                inputMessageText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(RosePrimary)
                            .testTag("send_msg_btn"),
                        enabled = inputMessageText.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Attachment Selector Panel Sheet
    if (attachmentSheetVisible) {
        AlertDialog(
            onDismissRequest = { attachmentSheetVisible = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Brush, contentDescription = null, tint = GoldMetallic)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Escolha de Lembrança", style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column {
                    Text("Envie uma lembrança afetiva direto no chat:", style = MaterialTheme.typography.bodySmall, color = RoseGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        attachmentTemplates.forEach { (name, url) ->
                            Card(
                                modifier = Modifier
                                    .width(100.dp)
                                    .clickable {
                                        viewModel.sendChatMessage("📸 Enviou um momento: $name", imageUrl = url)
                                        attachmentSheetVisible = false
                                    },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, RoseTertiary),
                                colors = CardDefaults.cardColors(containerColor = RoseWhite)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = name,
                                        modifier = Modifier
                                            .height(80.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CocoaDark,
                                        modifier = Modifier.padding(4.dp),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { attachmentSheetVisible = false }) {
                    Text("Fechar", color = RosePrimary)
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Delete message safety validation
    selectedPhotoToDelete?.let { msg ->
        AlertDialog(
            onDismissRequest = { selectedPhotoToDelete = null },
            title = { Text("Excluir Mensagem?") },
            text = { Text("Tem certeza que deseja apagar essa mensagem de amor?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteMessage(msg.id)
                        selectedPhotoToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                ) {
                    Text("Apagar")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPhotoToDelete = null }) {
                    Text("Manter")
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageBubble(
    msg: ChatMessage,
    isMe: Boolean,
    timeString: String,
    onLongClick: () -> Unit
) {
    val bubbleShape = if (isMe) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    val bubbleColor = if (isMe) RosePrimary else GoldSoft.copy(alpha = 0.8f)
    val textAndIconColor = if (isMe) Color.White else CocoaDark

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (!isMe) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(RoseSecondary)
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = msg.senderName.take(1).uppercase(Locale.getDefault()), 
                    style = MaterialTheme.typography.labelSmall, 
                    fontWeight = FontWeight.Bold,
                    color = CocoaDark
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            modifier = Modifier
                .widthIn(max = 260.dp)
                .combinedClickable(
                    onClick = {},
                    onLongClick = onLongClick
                )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // If it contains a media item
                msg.imageUrl?.let { imgUrl ->
                    AsyncImage(
                        model = imgUrl,
                        contentDescription = "Imagem Compartilhada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .padding(bottom = 6.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = msg.messageText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textAndIconColor
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timeString,
                        fontSize = 9.sp,
                        color = if (isMe) Color.White.copy(alpha = 0.8f) else RoseGray
                    )
                    if (isMe) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(Icons.Filled.DoneAll, contentDescription = null, sizeCheckMini(), tint = Color.White)
                    }
                }
            }
        }
    }
}

fun sizeCheckMini() = Modifier.size(10.dp)
