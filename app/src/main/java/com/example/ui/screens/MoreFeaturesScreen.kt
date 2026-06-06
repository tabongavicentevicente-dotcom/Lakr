package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.CoupleGoal
import com.example.data.model.MemoryPin
import com.example.data.model.PrivateLetter
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoupleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreFeaturesScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf("metas") } // "metas", "mapa", "cartas"

    val subTabsList = listOf(
        Triple("metas", "Metas", Icons.Filled.ListAlt),
        Triple("mapa", "Mapa de Memórias", Icons.Filled.Map),
        Triple("cartas", "Cofre de Cartas", Icons.Filled.Lock)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Nosso Baú Secreto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CocoaDark)
                        Text("Planos, mapas e segredos partilhados", style = MaterialTheme.typography.bodySmall, color = RoseGray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 14.dp)
        ) {
            
            // Sub Tabs Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                subTabsList.forEach { (tabId, label, icon) ->
                    val isActive = activeSubTab == tabId
                    Button(
                        onClick = { activeSubTab = tabId },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("tab_$tabId"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) GoldMetallic else Color.White
                        ),
                        border = BorderStroke(1.dp, if (isActive) GoldMetallic else RoseTertiary),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = null, tint = if (isActive) Color.White else GoldMetallic, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(label, fontSize = 11.sp, color = if (isActive) Color.White else CocoaDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Display based on Sub Tab
            AnimatedContent(
                targetState = activeSubTab,
                transitionSpec = {
                    fadeIn(tween(250)) togetherWith fadeOut(tween(250))
                },
                label = "SubTabTransition"
            ) { targetTab ->
                when (targetTab) {
                    "metas" -> CoupleGoalsTab(viewModel)
                    "mapa" -> MemoriesMapTab(viewModel)
                    "cartas" -> PrivateLettersTab(viewModel)
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------------
// TAB 1: Couple Goals List
// ---------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoupleGoalsTab(viewModel: CoupleViewModel) {
    val goals by viewModel.goalsState.collectAsState()
    var isAddingGoal by remember { mutableStateOf(false) }

    var goalTitle by remember { mutableStateOf("") }
    var goalNote by remember { mutableStateOf("") }

    // Prepopulate empty goals
    LaunchedEffect(goals.size) {
        if (goals.isEmpty()) {
            viewModel.addGoal("Ver o pôr do sol na praia 🌅", "Tirando fotos deitados na canga")
            viewModel.addGoal("Fazer um piquenique romântico 🍓", "Com queijos, uva verde e vinho branco suave")
            viewModel.addGoal("Viajar para gramado juntos ❄️", "Comer founde de chocolate no friozinho")
        }
    }

    // Progress Bar percent
    val completedCount = goals.count { it.status == 2 }
    val progressPercent = if (goals.isNotEmpty()) (completedCount.toFloat() / goals.size) else 0f

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = RoseWhite),
            border = BorderStroke(1.dp, RoseTertiary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Nossa Jornada de Metas", fontWeight = FontWeight.Bold, color = CocoaDark)
                    Text("${(progressPercent * 100).toInt()}% Concluído", fontWeight = FontWeight.Bold, color = RosePrimary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progressPercent,
                    color = RosePrimary,
                    trackColor = RoseTertiary,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sonhos e Objetivos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = CocoaDark)
            
            OutlinedButton(
                onClick = { isAddingGoal = true },
                colors = ButtonDefaults.outlinedButtonColors(containerColor = RoseWhite),
                border = BorderStroke(1.dp, GoldMetallic),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = GoldMetallic)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Adicionar Meta", color = CocoaDark)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(goals) { goal ->
                GoalItemCard(
                    goal = goal,
                    onStatusChange = { next -> viewModel.updateGoalStatus(goal, next) },
                    onDelete = { viewModel.deleteGoal(goal.id) }
                )
            }
        }
    }

    if (isAddingGoal) {
        AlertDialog(
            onDismissRequest = { isAddingGoal = false },
            title = { Text("Novo Objetivo Juntos", color = CocoaDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Escreva um plano fofo para vocês cumprirem lado a lado:", style = MaterialTheme.typography.bodySmall, color = RoseGray)
                    
                    OutlinedTextField(
                        value = goalTitle,
                        onValueChange = { goalTitle = it },
                        label = { Text("Nome da Meta (ex: Ver aurora boreal)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary)
                    )

                    OutlinedTextField(
                        value = goalNote,
                        onValueChange = { goalNote = it },
                        label = { Text("Detalhes, promessas ou recompensa") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary),
                        modifier = Modifier.height(80.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addGoal(goalTitle, goalNote)
                        goalTitle = ""
                        goalNote = ""
                        isAddingGoal = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    enabled = goalTitle.isNotBlank()
                ) {
                    Text("Cadastrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddingGoal = false }) {
                    Text("Cancelar", color = RoseGray)
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun GoalItemCard(
    goal: CoupleGoal,
    onStatusChange: (Int) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RoseWhite),
        border = BorderStroke(1.dp, RoseTertiary),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                // Interactive Checkbox Box based on status
                IconButton(
                    onClick = {
                        val nextStatus = (goal.status + 1) % 3
                        onStatusChange(nextStatus)
                    }
                ) {
                    val (icon, color) = when (goal.status) {
                        0 -> Pair(Icons.Filled.RadioButtonUnchecked, RoseTertiary)
                        1 -> Pair(Icons.Filled.HourglassBottom, GoldMetallic)
                        else -> Pair(Icons.Filled.CheckCircle, RosePrimary)
                    }
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = goal.title,
                        fontWeight = FontWeight.Bold,
                        color = CocoaDark,
                        fontSize = 15.sp,
                        style = if (goal.status == 2) MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp) else MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = goal.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = RoseGray
                    )
                    
                    // State label tag
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .background(
                                color = when (goal.status) {
                                    0 -> RoseTertiary.copy(alpha = 0.4f)
                                    1 -> GoldSoft
                                    else -> SoftButtonBorderPink
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (goal.status) {
                                0 -> "Planejado"
                                1 -> "Em Andamento"
                                else -> "Concluído! 🎉💖"
                            },
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = CocoaDark
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.DeleteOutline, contentDescription = null, tint = RosePrimary)
            }
        }
    }
}


// ---------------------------------------------------------------------------------
// TAB 2: Maps of Memories (Scenic 2D Interactive simulation)
// ---------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesMapTab(viewModel: CoupleViewModel) {
    val pins by viewModel.pinsState.collectAsState()
    var isAddingPin by remember { mutableStateOf(false) }

    var tapOffset by remember { mutableStateOf(Offset(0.5f, 0.5f)) } // Relative percentages
    var pinTitle by remember { mutableStateOf("") }
    var pinDescription by remember { mutableStateOf("") }
    var pinImageUrl by remember { mutableStateOf("") }

    var selectedPinToShowDetails by remember { mutableStateOf<MemoryPin?>(null) }

    // Prepopulate map pins
    LaunchedEffect(pins.size) {
        if (pins.isEmpty()) {
            viewModel.addMemoryPin("Viagem à Serra Doce", "Onde andamos de tirolesa e comemos chocolate.", 0.35, 0.28)
            viewModel.addMemoryPin("Primeiro Café de Manhã", "Na varanda amarela daquela cafeteria de esquina.", 0.65, 0.55)
            viewModel.addMemoryPin("Show de Jazz", "Nas fileiras da frente rindo muito.", 0.48, 0.72)
        }
    }

    val mapScenicShortcuts = listOf(
        Pair("Cinema Doce", "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?q=80&w=400"),
        Pair("Parque de Flores", "https://images.unsplash.com/photo-1526047932273-341f2a7631f9?q=80&w=400"),
        Pair("Praia de Sal", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=400"),
        Pair("Jantar de Ouro", "https://images.unsplash.com/photo-1544816155-12df9643f363?q=80&w=400")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = GoldSoft.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            Text(
                text = "📌 Dica: Toque em qualquer parte do Mapa de Amor para guardar uma recordação física do mapa espacial!",
                style = MaterialTheme.typography.bodySmall,
                color = GoldDark,
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        // Animated Interactive Stylized 2D canvas representing couples map
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, GoldChampagne, RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(RoseWhite, GoldSoft, RoseBackground)
                    )
                )
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Convert absolute coordinates offset into general percentage
                        val relX = offset.x / size.width
                        val relY = offset.y / size.height
                        tapOffset = Offset(relX, relY)
                        isAddingPin = true
                    }
                }
        ) {
            val canvasWidth = maxWidth
            val canvasHeight = maxHeight

            // Draw stylized geographic grids or lines
            Canvas(modifier = Modifier.fillMaxSize()) {
                val gridSpacing = 40.dp.toPx()
                for (x in 0..(size.width / gridSpacing).toInt()) {
                    drawLine(
                        color = GoldChampagne.copy(alpha = 0.15f),
                        start = Offset(x * gridSpacing, 0f),
                        end = Offset(x * gridSpacing, size.height)
                    )
                }
                for (y in 0..(size.height / gridSpacing).toInt()) {
                    drawLine(
                        color = GoldChampagne.copy(alpha = 0.15f),
                        start = Offset(0f, y * gridSpacing),
                        end = Offset(size.width, y * gridSpacing)
                    )
                }

                // Draw central heart lake
                drawCircle(
                    color = RoseTertiary.copy(alpha = 0.3f),
                    radius = 90.dp.toPx(),
                    center = Offset(size.width / 2, size.height / 2)
                )
            }

            // Central lake marker
            Text(
                text = "Lago do Amor",
                fontSize = 10.sp,
                color = RosePrimary.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            // Draw all Memory Pins as overlay hearts
            pins.forEach { pin ->
                val posX = canvasWidth * pin.latitude.toFloat()
                val posY = canvasHeight * pin.longitude.toFloat()

                Box(
                    modifier = Modifier
                        .offset(x = posX - 16.dp, y = posY - 16.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.5.dp, GoldMetallic, CircleShape)
                        .clickable { selectedPinToShowDetails = pin },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PinDrop,
                        contentDescription = pin.title,
                        tint = RosePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "Alfinetes marcados: ${pins.size}",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 80.dp),
            color = RoseGray
        )
    }

    // Modal to create custom pin
    if (isAddingPin) {
        AlertDialog(
            onDismissRequest = { isAddingPin = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.AddLocationAlt, contentDescription = null, tint = RosePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finque um Ponto de Amor", style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Guarde um ponto do planeta onde vocês viveram algo maravilhoso:", style = MaterialTheme.typography.bodySmall, color = RoseGray)

                    OutlinedTextField(
                        value = pinTitle,
                        onValueChange = { pinTitle = it },
                        label = { Text("Título da Lembrança") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary)
                    )

                    OutlinedTextField(
                        value = pinDescription,
                        onValueChange = { pinDescription = it },
                        label = { Text("O que aconteceu de especial?") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary),
                        modifier = Modifier.height(80.dp)
                    )

                    OutlinedTextField(
                        value = pinImageUrl,
                        onValueChange = { pinImageUrl = it },
                        label = { Text("Endereço da Foto (opcional)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldMetallic),
                        placeholder = { Text("https://...") }
                    )

                    // Template choice
                    Text("Recomendação de imagens fofas:", style = MaterialTheme.typography.labelSmall, color = GoldDark)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        mapScenicShortcuts.forEach { (name, url) ->
                            Card(
                                modifier = Modifier
                                    .width(60.dp)
                                    .clickable { pinImageUrl = url },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, GoldChampagne),
                                colors = CardDefaults.cardColors(containerColor = RoseWhite)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = name,
                                        modifier = Modifier.height(35.dp).fillMaxWidth(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(name, fontSize = 7.sp, color = CocoaDark)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val img = if (pinImageUrl.isBlank()) null else pinImageUrl
                        viewModel.addMemoryPin(pinTitle, pinDescription, tapOffset.x.toDouble(), tapOffset.y.toDouble(), img)
                        pinTitle = ""
                        pinDescription = ""
                        pinImageUrl = ""
                        isAddingPin = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    enabled = pinTitle.isNotBlank()
                ) {
                    Text("Marcar Ponto")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddingPin = false }) {
                    Text("Cancelar", color = RoseGray)
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Modal to display marker details
    selectedPinToShowDetails?.let { pin ->
        AlertDialog(
            onDismissRequest = { selectedPinToShowDetails = null },
            title = {
                Text(pin.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldMetallic)
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pin.imageUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = pin.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Text(
                        text = pin.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = CocoaDark,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = "Apelidado por: ${pin.uploaderName}",
                        fontSize = 10.sp,
                        color = RoseGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePin(pin.id)
                        selectedPinToShowDetails = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                ) {
                    Text("Fincar Fora (Apagar)")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPinToShowDetails = null }) {
                    Text("Fechar", color = RoseGray)
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }
}


// ---------------------------------------------------------------------------------
// TAB 3: Padlock locked Secret letters chest (Mensagens Privadas)
// ---------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateLettersTab(viewModel: CoupleViewModel) {
    val letters by viewModel.lettersState.collectAsState()
    val isUnlocked by viewModel.isVaultUnlocked.collectAsState()
    val config by viewModel.configState.collectAsState()
    val errorMsg by viewModel.vaultError.collectAsState()

    var letterPasswordInput by remember { mutableStateOf("") }
    var isWritingLetter by remember { mutableStateOf(false) }

    // Forms fields
    var letterTitle by remember { mutableStateOf("") }
    var letterContent by remember { mutableStateOf("") }

    var expandedLetterToShow by remember { mutableStateOf<PrivateLetter?>(null) }

    // Prepopulate letters if empty
    LaunchedEffect(letters.size) {
        if (letters.isEmpty()) {
            viewModel.sendPrivateLetter(
                title = "Carta para ler no Futuro",
                content = "Se você está lendo isso, saiba que continuo te amando cada dia mais. Obrigado por ser minha porto seguro nos dias calmos e minha âncora nas tempestades. Você é o amor da minha vida para todo o sempre! ❤️✨\n\nAbraços de seu amor eterno.",
                recipient = config.partner2Name
            )
            viewModel.sendPrivateLetter(
                title = "Promessa Secreta",
                content = "Prometo que sempre manterei o nosso calor vivo. Que quando você estiver cansada, eu farei o jantar, e que nunca deixaremos de caminhar de mãos dadas pelas ruas escuras da cidade. Nosso amor é um voto silencioso.",
                recipient = config.partner1Name
            )
        }
    }

    if (!isUnlocked) {
        // Vault Lock Screen representation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(White, RoundedCornerShape(24.dp))
                    .border(1.dp, GoldChampagne, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(GoldSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = "Cofre Fechado", tint = GoldMetallic, modifier = Modifier.size(44.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Baú das Mensagens Privadas",
                    fontWeight = FontWeight.Bold,
                    color = CocoaDark,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Contêm cartas de promessas secretas e votos eternos. Use o PIN do Casal para abrir.",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = RoseGray,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = letterPasswordInput,
                    onValueChange = { if (it.length <= 4) letterPasswordInput = it },
                    label = { Text("Senha do Cofre (4 dígitos)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword
                    ),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier
                        .width(200.dp)
                        .testTag("pin_chest_input"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldMetallic)
                )

                errorMsg?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val correct = viewModel.unlockVault(letterPasswordInput)
                        if (correct) {
                            letterPasswordInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic),
                    modifier = Modifier.width(160.dp).testTag("unlock_chest_btn")
                ) {
                    Text("Abrir Baú", color = CocoaDark)
                }
            }
        }
    } else {
        // Vault Unlocked letters manager
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Cartas Secretas (Baú Aberto)",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldDark
                )
                
                Row {
                    IconButton(onClick = { viewModel.lockVault() }) {
                        Icon(Icons.Filled.LockOpen, contentDescription = "Trancar Baú", tint = GoldMetallic)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    OutlinedButton(
                        onClick = { isWritingLetter = true },
                        border = BorderStroke(1.dp, RosePrimary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.EditNote, contentDescription = null, tint = RosePrimary)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Escrever Carta", color = CocoaDark)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(letters) { letter ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedLetterToShow = letter },
                        colors = CardDefaults.cardColors(containerColor = RoseWhite),
                        border = BorderStroke(1.dp, RoseTertiary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(RoseTertiary.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Mail, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        letter.title,
                                        fontWeight = FontWeight.Bold,
                                        color = CocoaDark
                                    )
                                    Text(
                                        "De: ${letter.senderName} para ${letter.recipientName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = RoseGray
                                    )
                                }
                            }
                            Icon(Icons.Filled.NavigateNext, contentDescription = null, tint = GoldMetallic)
                        }
                    }
                }
            }
        }
    }

    // Modal to compose a new private love letter
    if (isWritingLetter) {
        val nextRecipient = if (config.currentActiveUser == config.partner1Name) config.partner2Name else config.partner1Name

        AlertDialog(
            onDismissRequest = { isWritingLetter = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MarkEmailUnread, contentDescription = null, tint = RosePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Escrever Carta Romântica", style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("De: ${config.currentActiveUser} • Para: $nextRecipient", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = RosePrimary)

                    OutlinedTextField(
                        value = letterTitle,
                        onValueChange = { letterTitle = it },
                        label = { Text("Título da Carta") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary)
                    )

                    OutlinedTextField(
                        value = letterContent,
                        onValueChange = { letterContent = it },
                        label = { Text("Conteúdo da carta de amor...") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary),
                        modifier = Modifier.height(180.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.sendPrivateLetter(letterTitle, letterContent, nextRecipient)
                        letterTitle = ""
                        letterContent = ""
                        isWritingLetter = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    enabled = letterTitle.isNotBlank() && letterContent.isNotBlank()
                ) {
                    Text("Enviar Voto")
                }
            },
            dismissButton = {
                TextButton(onClick = { isWritingLetter = false }) {
                    Text("Descartar", color = RoseGray)
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Interactive Full Letters Modal frame (Display cursive manuscript)
    expandedLetterToShow?.let { matchLetter ->
        val fullDatef = remember { SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'às' HH:mm", Locale.getDefault()) }

        AlertDialog(
            onDismissRequest = { expandedLetterToShow = null },
            title = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Drafts, contentDescription = null, tint = GoldMetallic, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(matchLetter.title, style = MaterialTheme.typography.headlineMedium, color = CocoaDark, textAlign = TextAlign.Center)
                    Text("De: ${matchLetter.senderName} 💖 Para: ${matchLetter.recipientName}", style = MaterialTheme.typography.labelSmall, color = GoldSharedColor())
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 350.dp)
                        .background(GoldSoft.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = matchLetter.content,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = FontFamily.SansSerif,
                        color = CocoaDark,
                        lineHeight = 26.sp
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "Votado em: ${fullDatef.format(Date(matchLetter.timestamp))}",
                        fontSize = 9.sp,
                        color = RoseGray,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(
                        onClick = {
                            viewModel.deleteLetter(matchLetter.id)
                            expandedLetterToShow = null
                        }
                    ) {
                        Text("Retirar Definitivamente", color = RosePrimary, fontSize = 11.sp)
                    }
                    
                    Button(
                        onClick = { expandedLetterToShow = null },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic)
                    ) {
                        Text("Fechar", color = CocoaDark)
                    }
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
