package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SpecialDate
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoupleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dates by viewModel.datesState.collectAsState()
    val config by viewModel.configState.collectAsState()

    var isAddingDate by remember { mutableStateOf(false) }

    // Form fields
    var dateTitle by remember { mutableStateOf("") }
    var dateDescription by remember { mutableStateOf("") }
    var dateCategory by remember { mutableStateOf("Viagem") }
    var eventDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var colorSelectedHex by remember { mutableStateOf("#8A3FFC") }

    val categories = listOf("Viagem", "Aniversário", "Encontro", "Conquistas", "Outro")
    val colorsList = listOf("#8A3FFC", "#A78BFA", "#06B6D4", "#10B981", "#E2E8F0")

    val dateFormatter = remember { SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale.getDefault()) }

    // Prepopulate some lovely defaults when empty
    LaunchedEffect(dates.size) {
        if (dates.isEmpty()) {
            val yr = Calendar.getInstance().get(Calendar.YEAR)
            viewModel.addSpecialDate(
                title = "Primeiro Encontro",
                description = "O dia que nossos mundos se cruzaram e tudo mudou. 💜",
                category = "Encontro",
                dateMillis = Calendar.getInstance().apply { set(yr - 2, 5, 12) }.timeInMillis, // June 12th
                colorHex = "#8A3FFC"
            )
            viewModel.addSpecialDate(
                title = "Primeira Viagem Juntos",
                description = "Nosso final de semana mágico sob os céus azuis da serra. 🏕️",
                category = "Viagem",
                dateMillis = Calendar.getInstance().apply { set(yr - 1, 9, 20) }.timeInMillis, // October 20th
                colorHex = "#06B6D4"
            )
            viewModel.addSpecialDate(
                title = "Primeiro Eu Te Amo",
                description = "As doces palavras sussurradas à meia-noite sob uma lua cheia. ✨",
                category = "Conquistas",
                dateMillis = Calendar.getInstance().apply { set(yr - 2, 7, 14) }.timeInMillis,
                colorHex = "#A78BFA"
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Datas Especiais", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CocoaDark)
                        Text("Nossa linha do tempo infinita", style = MaterialTheme.typography.bodySmall, color = RoseGray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingDate = true },
                containerColor = RosePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_date_fab")
            ) {
                Icon(Icons.Filled.Event, contentDescription = "Nova Data Romântica")
            }
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
            
            // Core Anniversary Reminder Card at top
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GoldSoft.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, GoldChampagne),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Stars, contentDescription = null, tint = GoldMetallic, modifier = Modifier.size(28.dp))
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Celebração do Dia Geral",
                            style = MaterialTheme.typography.labelSmall,
                            color = GoldDark,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "União do casal: ${dateFormatter.format(Date(config.anniversaryDate))}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CocoaDark
                        )
                    }
                }
            }

            // Dates timeline list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(dates) { dateMatch ->
                    CalendarTimelineItem(
                        dateItem = dateMatch,
                        onDeleteClick = { viewModel.deleteSpecialDate(dateMatch.id) }
                    )
                }
            }
        }
    }

    // Modal to schedule a new date
    if (isAddingDate) {
        val datePickerDialog = remember {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val selectedCal = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    }
                    eventDateMillis = selectedCal.timeInMillis
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        AlertDialog(
            onDismissRequest = { isAddingDate = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = RosePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Marcar Data Especial", style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Guarde um dia marcante na memória do casal:", style = MaterialTheme.typography.bodySmall, color = RoseGray)

                    OutlinedTextField(
                        value = dateTitle,
                        onValueChange = { dateTitle = it },
                        label = { Text("Nome do Evento (ex: Primeiro Beijo)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary)
                    )

                    OutlinedTextField(
                        value = dateDescription,
                        onValueChange = { dateDescription = it },
                        label = { Text("Breve memória fofa") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary),
                        modifier = Modifier.height(80.dp)
                    )

                    // Event Date Picker
                    Column {
                        Text("Quando aconteceu (ou vai acontecer)?", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = CocoaDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, GoldMetallic)
                        ) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = null, tint = GoldMetallic)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(dateFormatter.format(Date(eventDateMillis)), color = CocoaDark)
                        }
                    }

                    // Category Selector
                    Text("Categoria", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = CocoaDark)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            val active = dateCategory == cat
                            Card(
                                modifier = Modifier.clickable { dateCategory = cat },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (active) GoldMetallic else Color.White
                                ),
                                border = BorderStroke(1.dp, RoseTertiary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 11.sp,
                                    color = if (active) Color.White else CocoaDark,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Color tag selector
                    Text("Cor do Alfinete", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = CocoaDark)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        colorsList.forEach { colorString ->
                            val colorValue = Color(android.graphics.Color.parseColor(colorString))
                            val isSelectedColor = colorSelectedHex == colorString
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(colorValue)
                                    .border(
                                        width = if (isSelectedColor) 2.5.dp else 0.dp,
                                        color = if (isSelectedColor) CocoaDark else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { colorSelectedHex = colorString }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addSpecialDate(dateTitle, dateDescription, dateCategory, eventDateMillis, colorSelectedHex)
                        // Reset forms
                        dateTitle = ""
                        dateDescription = ""
                        dateCategory = "Viagem"
                        eventDateMillis = System.currentTimeMillis()
                        isAddingDate = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    enabled = dateTitle.isNotBlank()
                ) {
                    Text("Agendar")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddingDate = false }) {
                    Text("Cancelar", color = RoseGray)
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
fun CalendarTimelineItem(
    dateItem: SpecialDate,
    onDeleteClick: () -> Unit
) {
    val dateString = remember(dateItem.eventDate) {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        sdf.format(Date(dateItem.eventDate))
    }

    val daysDiffDetail = remember(dateItem.eventDate) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val itemCal = Calendar.getInstance().apply {
            timeInMillis = dateItem.eventDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val diff = itemCal - today
        val days = diff / (1000 * 60 * 60 * 24)

        when {
            days == 0L -> "Hoje! 🎉"
            days > 0 -> "Faltam $days dias! ⏳"
            else -> "Aconteceu há ${-days} dias! 💞"
        }
    }

    val bubbleColor = Color(android.graphics.Color.parseColor(dateItem.colorHex))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = RoseWhite),
        border = BorderStroke(1.dp, RoseTertiary),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pin colored circle with category icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(bubbleColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    val iconVector = when (dateItem.category) {
                        "Viagem" -> Icons.Filled.FlightTakeoff
                        "Aniversário" -> Icons.Filled.Cake
                        "Encontro" -> Icons.Filled.LocalCafe
                        "Conquistas" -> Icons.Filled.EmojiEvents
                        else -> Icons.Filled.CalendarMonth
                    }
                    Icon(iconVector, contentDescription = null, tint = bubbleColor, modifier = Modifier.size(22.dp))
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = dateItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = CocoaDark
                    )
                    Text(
                        text = dateItem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = RoseGray,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                    Text(
                        text = "$dateString • ${dateItem.category}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldMetallic
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .background(bubbleColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = daysDiffDetail,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (bubbleColor == GoldSoft || bubbleColor == GoldChampagne) CocoaDark else Color.White
                    )
                }
                
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(28.dp).padding(top = 4.dp)
                ) {
                    Icon(Icons.Filled.HighlightOff, contentDescription = "Deletar data", tint = RosePrimary.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
