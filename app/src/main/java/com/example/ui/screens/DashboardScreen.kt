package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoupleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val config by viewModel.configState.collectAsState()
    
    var isEditingProfile by remember { mutableStateOf(false) }
    var partner1NameInput by remember { mutableStateOf(config.partner1Name) }
    var partner2NameInput by remember { mutableStateOf(config.partner2Name) }
    var vaultPinInput by remember { mutableStateOf(config.vaultPin) }
    var anniversaryDateInput by remember { mutableStateOf(config.anniversaryDate) }

    // Synchronize editing states with real config
    LaunchedEffect(config) {
        partner1NameInput = config.partner1Name
        partner2NameInput = config.partner2Name
        vaultPinInput = config.vaultPin
        anniversaryDateInput = config.anniversaryDate
    }

    // Heart beating animation
    val infiniteTransition = rememberInfiniteTransition(label = "HeartbeatTransition")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeartScale"
    )

    // Interactive user custom sparks trigger
    var localClickSparksCount by remember { mutableStateOf(0) }

    // Date formatting helper
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Math for days together
    val today = System.currentTimeMillis()
    val diffMillis = today - config.anniversaryDate
    val daysTogether = if (diffMillis > 0) diffMillis / (1000 * 60 * 60 * 24) else 0

    // Calendar detailed periods calculation
    val calendarPeriod = remember(config.anniversaryDate) {
        val startCal = Calendar.getInstance().apply { timeInMillis = config.anniversaryDate }
        val endCal = Calendar.getInstance()
        var years = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)
        var months = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH)
        var days = endCal.get(Calendar.DAY_OF_MONTH) - startCal.get(Calendar.DAY_OF_MONTH)
        
        if (days < 0) {
            months--
            val prevMonth = (endCal.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
            days += prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        if (months < 0) {
            years--
            months += 12
        }
        if (years < 0) Triple(0, 0, 0) else Triple(years, months, days)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Aos Olhos de ${config.currentActiveUser}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = CocoaDark
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isEditingProfile = true },
                        modifier = Modifier.testTag("edit_profile_btn")
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Configurações", tint = GoldMetallic)
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Simulator Switching Strip
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = RoseTertiary.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .clickable { viewModel.toggleActiveUser() }
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Sync, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Testando app? Mudar ponto de vista",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = CocoaDark
                        )
                    }
                    Text(
                        text = "Ver como ${if (config.currentActiveUser == config.partner1Name) config.partner2Name else config.partner1Name}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Coupled Profile Avatars row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Partner 1 Card
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(GoldSoft)
                            .border(2.dp, GoldMetallic, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Face,
                            contentDescription = config.partner1Name,
                            tint = GoldDark,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(config.partner1Name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CocoaDark)
                    Text("Ele 💙", style = MaterialTheme.typography.bodySmall, color = RoseGray)
                }

                // Animated Pulse Heart
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        localClickSparksCount++
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Beating Heart Connection",
                        tint = RosePrimary,
                        modifier = Modifier
                            .size(54.dp)
                            .scale(heartScale)
                            .graphicsLayer {
                                rotationZ = if (localClickSparksCount % 2 == 0) 0f else 15f
                            }
                    )
                    
                    Text(
                        text = if (localClickSparksCount > 0) "💖 +$localClickSparksCount Batidas!" else "Toque no Amor",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoseGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                // Partner 2 Card
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(RoseTertiary)
                            .border(2.dp, RosePrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Face3,
                            contentDescription = config.partner2Name,
                            tint = RosePrimary,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(config.partner2Name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CocoaDark)
                    Text("Ela 💗", style = MaterialTheme.typography.bodySmall, color = RoseGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Counter Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = RoseWhite),
                border = BorderStroke(1.dp, RosePrimary.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Já se passaram",
                        style = MaterialTheme.typography.titleSmall,
                        color = RoseGray,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "$daysTogether",
                        style = MaterialTheme.typography.headlineLarge.copy(fontSize = 58.sp),
                        fontWeight = FontWeight.ExtraBold,
                        color = RosePrimary
                    )

                    Text(
                        text = "dias de felicidade",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CocoaDark,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Divider(color = RoseTertiary, thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp))

                    // Detailed breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${calendarPeriod.first}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldMetallic)
                            Text("anos", style = MaterialTheme.typography.bodySmall, color = CocoaDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${calendarPeriod.second}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldMetallic)
                            Text("meses", style = MaterialTheme.typography.bodySmall, color = CocoaDark)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${calendarPeriod.third}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldMetallic)
                            Text("dias", style = MaterialTheme.typography.bodySmall, color = CocoaDark)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Desde ${dateFormatter.format(Date(config.anniversaryDate))} 👩‍❤️‍👨",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoseGray,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            // Love Quote Section (Changes periodically)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = GoldSoft.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, GoldChampagne),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = GoldMetallic, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "\"O amor não se mede em dias, mas sim no brilho de cada beijo e na segurança de cada abraço compartilhados.\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.SansSerif,
                            color = CocoaDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "— Dedicado a ${config.partner1Name} & ${config.partner2Name}",
                            style = MaterialTheme.typography.labelSmall,
                            color = RoseGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Love Actions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, RoseTertiary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Filled.Call, contentDescription = null, tint = RosePrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ele", style = MaterialTheme.typography.labelLarge, color = CocoaDark)
                        Text(config.partner1Phone, style = MaterialTheme.typography.bodySmall, color = RoseGray)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = BorderStroke(1.dp, RoseTertiary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Filled.Call, contentDescription = null, tint = GoldMetallic)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ela", style = MaterialTheme.typography.labelLarge, color = CocoaDark)
                        Text(config.partner2Phone, style = MaterialTheme.typography.bodySmall, color = RoseGray)
                    }
                }
            }
        }
    }

    // Edit Profile Setup dialog (Anniversary + Names customization)
    if (isEditingProfile) {
        val datePickerDialog = remember {
            val calendar = Calendar.getInstance().apply { timeInMillis = config.anniversaryDate }
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val selectedCal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
                    anniversaryDateInput = selectedCal.timeInMillis
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        AlertDialog(
            onDismissRequest = { isEditingProfile = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Favorite, contentDescription = null, tint = RosePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Configurar o Casal", style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Personalize as informações oficiais do seu amor para atualizar o app inteiro:", style = MaterialTheme.typography.bodySmall, color = RoseGray)

                    OutlinedTextField(
                        value = partner1NameInput,
                        onValueChange = { partner1NameInput = it },
                        label = { Text("Nome do Namorado (Ele)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary)
                    )

                    OutlinedTextField(
                        value = partner2NameInput,
                        onValueChange = { partner2NameInput = it },
                        label = { Text("Nome da Namorada (Ela)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary)
                    )

                    OutlinedTextField(
                        value = vaultPinInput,
                        onValueChange = { vaultPinInput = it },
                        label = { Text("PIN do Cofre de Cartas (4 dígitos)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldMetallic)
                    )

                    // Pick anniversary button
                    Column {
                        Text("Data de Início da União", style = MaterialTheme.typography.labelLarge, color = CocoaDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, GoldMetallic)
                        ) {
                            Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = GoldMetallic)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(dateFormatter.format(Date(anniversaryDateInput)), color = CocoaDark)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCoupleConfig(
                            partner1 = partner1NameInput,
                            partner2 = partner2NameInput,
                            pin = vaultPinInput,
                            dateMillis = anniversaryDateInput
                        )
                        isEditingProfile = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic)
                ) {
                    Text("Salvar Amor")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditingProfile = false }) {
                    Text("Cancelar", color = RoseGray)
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
