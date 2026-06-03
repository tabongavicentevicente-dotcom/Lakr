package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.SharedPhoto
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoupleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlbumScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    val photos by viewModel.photosState.collectAsState()
    val config by viewModel.configState.collectAsState()

    var selectedCategory by remember { mutableStateOf("Todos") }
    var isAddingPhoto by remember { mutableStateOf(false) }
    var scalePhotoViewer by remember { mutableStateOf<SharedPhoto?>(null) }

    // Forms fields
    var photoTitle by remember { mutableStateOf("") }
    var photoCaption by remember { mutableStateOf("") }
    var photoCategory by remember { mutableStateOf("Romântico") }
    var photoUrl by remember { mutableStateOf("") }

    val categories = listOf("Todos", "Romântico", "Viagem", "Festas", "Dia-a-dia")
    val dateForm = remember { SimpleDateFormat("dd 'de' MMM, yyyy", Locale.getDefault()) }

    // Filter photos based on category
    val filteredPhotos = remember(photos, selectedCategory) {
        if (selectedCategory == "Todos") photos else photos.filter { it.category == selectedCategory }
    }

    // Default Unsplash presets for quick visual richness
    val photoShortcuts = listOf(
        Pair("Abraço de Inverno", "https://images.unsplash.com/photo-1544816155-12df9643f363?q=80&w=600"),
        Pair("Beijo ao Pôr do Sol", "https://images.unsplash.com/photo-1511285560929-80b456fea0bc?q=80&w=600"),
        Pair("Praia de Verão", "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?q=80&w=600"),
        Pair("Mãos Dadas", "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=600"),
        Pair("Piquenique Doce", "https://images.unsplash.com/photo-1533105079780-92b9be482077?q=80&w=600")
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Álbum Compartilhado", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = CocoaDark)
                        Text("Mural dos nossos eternos instantes", style = MaterialTheme.typography.bodySmall, color = RoseGray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingPhoto = true },
                containerColor = RosePrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_photo_fab")
            ) {
                Icon(Icons.Filled.AddAPhoto, contentDescription = "Novo Evento Fotográfico")
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
            
            // Category Selector Horizontal Scroll Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    Card(
                        modifier = Modifier.clickable { selectedCategory = cat },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) GoldMetallic else Color.White
                        ),
                        border = BorderStroke(1.dp, if (isSelected) GoldMetallic else RoseTertiary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else CocoaDark,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Photo Album Grid
            if (filteredPhotos.isEmpty()) {
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
                                .background(GoldSoft),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.PhotoAlbum, contentDescription = null, tint = GoldMetallic, modifier = Modifier.size(36.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nosso painel visual está vazio",
                            style = MaterialTheme.typography.titleMedium,
                            color = CocoaDark,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Toque em '+' no canto inferior para colocar a primeira foto de nossa caminhada de amor!",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodySmall,
                            color = RoseGray
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredPhotos) { photo ->
                        PhotoCardItem(
                            photo = photo,
                            onClick = { scalePhotoViewer = photo }
                        )
                    }
                }
            }
        }
    }

    // Modal to write a new Photo Frame
    if (isAddingPhoto) {
        AlertDialog(
            onDismissRequest = { isAddingPhoto = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null, tint = RosePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardanapo Fotográfico", style = MaterialTheme.typography.titleLarge)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Poste um momento marcante de vocês no mural:", style = MaterialTheme.typography.bodySmall, color = RoseGray)

                    OutlinedTextField(
                        value = photoTitle,
                        onValueChange = { photoTitle = it },
                        label = { Text("Título da Foto") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary)
                    )

                    OutlinedTextField(
                        value = photoCaption,
                        onValueChange = { photoCaption = it },
                        label = { Text("Legenda Afetiva") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = RosePrimary),
                        modifier = Modifier.height(80.dp)
                    )

                    // Category Selector Segment
                    Text("Categoria", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = CocoaDark)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.drop(1).forEach { cat ->
                            val active = photoCategory == cat
                            Card(
                                modifier = Modifier.clickable { photoCategory = cat },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (active) RosePrimary else Color.White
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

                    OutlinedTextField(
                        value = photoUrl,
                        onValueChange = { photoUrl = it },
                        label = { Text("Endereço da imagem (URL)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GoldMetallic),
                        placeholder = { Text("https://...") }
                    )

                    // Unsplash Shortcut select row
                    Text("Dica: Toque em um modelo romântico para preencher a URL:", style = MaterialTheme.typography.labelSmall, color = GoldSharedColor())
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        photoShortcuts.forEach { (name, url) ->
                            Card(
                                modifier = Modifier
                                    .width(70.dp)
                                    .clickable { photoUrl = url },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, RoseTertiary),
                                colors = CardDefaults.cardColors(containerColor = RoseWhite)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = url,
                                        contentDescription = name,
                                        modifier = Modifier.height(45.dp).fillMaxWidth(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(
                                        text = name,
                                        fontSize = 8.sp,
                                        color = CocoaDark,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalUrl = if (photoUrl.isBlank()) "https://images.unsplash.com/photo-1518199266791-5375a83190b7?q=80&w=600" else photoUrl
                        viewModel.addSharedPhoto(photoTitle, photoCaption, photoCategory, finalUrl)
                        // Reset forms
                        photoTitle = ""
                        photoCaption = ""
                        photoCategory = "Romântico"
                        photoUrl = ""
                        isAddingPhoto = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                    enabled = photoTitle.isNotBlank()
                ) {
                    Text("Postar")
                }
            },
            dismissButton = {
                TextButton(onClick = { isAddingPhoto = false }) {
                    Text("Cancelar", color = RoseGray)
                }
            },
            containerColor = RoseWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Immersive Large Photo Light-box Viewer Modal
    scalePhotoViewer?.let { photo ->
        var showDeleteDialog by remember { mutableStateOf(false) }

        ModalBottomSheet(
            onDismissRequest = { scalePhotoViewer = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = CocoaDark,
            dragHandle = { BottomSheetDefaults.DragHandle(color = GoldSoft) }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CocoaDark)
                    .systemBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Title Bar in Large View
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(photo.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = GoldMetallic)
                            Text("Enviado por: ${photo.uploaderName}", style = MaterialTheme.typography.bodySmall, color = RoseTertiary)
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.DeleteForever, contentDescription = "Deletar Momento", tint = RoseSecondary)
                        }
                    }

                    // Display Frame
                    AsyncImage(
                        model = photo.imageUrl,
                        contentDescription = photo.title,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Fit
                    )

                    // Descriptive Footer
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "\"${photo.caption}\"",
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = FontFamily.SansSerif,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dateForm.format(Date(photo.timestamp)),
                            style = MaterialTheme.typography.bodySmall,
                            color = GoldSoft
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { scalePhotoViewer = null },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Voltar ao Álbum", color = CocoaDark)
                    }
                }
            }

            // Interior validation
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Remover Recordação?", color = CocoaDark) },
                    text = { Text("Iremos retirar esta foto de nossas lembranças, quer mesmo deletar?", color = RoseGray) },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deletePhoto(photo.id)
                                showDeleteDialog = false
                                scalePhotoViewer = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RosePrimary)
                        ) {
                            Text("Retirar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Manter", color = RoseGray)
                        }
                    },
                    containerColor = RoseWhite,
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }
    }
}

@Composable
fun PhotoCardItem(
    photo: SharedPhoto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RoseWhite),
        border = BorderStroke(1.5.dp, RoseTertiary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = photo.imageUrl,
                contentDescription = photo.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Category tag top corner
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .background(RoseSecondary, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(photo.category, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = cocoaBold())
            }

            // Vignette gradient for text readability at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )

            // Text info bottom corner
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = photo.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "por ${photo.uploaderName}",
                    fontSize = 9.sp,
                    color = RoseTertiary
                )
            }
        }
    }
}

fun GoldSharedColor() = Color(0xFFC5A059)
fun cocoaBold() = Color(0xFF4A3438)
