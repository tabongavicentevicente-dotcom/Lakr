package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoupleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: CoupleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var phoneInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var isPhoneFlowSelected by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf<String?>(null) }
    var customGoogleNameInput by remember { mutableStateOf("") }
    
    val isOtpSent by viewModel.isOtpSent.collectAsState()
    val showOauthGuidance by viewModel.currentOauthGuidanceVisible.collectAsState()
    val loading by viewModel.loginLoading.collectAsState()
    val config by viewModel.configState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listWithGradient()
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Overlay delicate gold sparks or stars
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF1E1A2B), Color(0xFF0C0C0E))
                            )
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(RosePrimary, Color.Transparent, RoseTertiary)
                            ),
                            shape = RoundedCornerShape(32.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_lakr_logo),
                        contentDescription = "LaKr Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(32.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "LaKr",
                    style = MaterialTheme.typography.headlineLarge,
                    color = CocoaDark,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )

                Text(
                    text = "LARISSA & KRESLEY • AI & PRIVACY",
                    style = MaterialTheme.typography.labelLarge,
                    color = RoseSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }            // Entry Form Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(RosePrimary.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                colors = CardDefaults.cardColors(containerColor = RoseWhite),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Nosso Espaço de Amor",
                        style = MaterialTheme.typography.titleMedium,
                        color = CocoaDark,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Um cantinho especial e exclusivo para nós dois. Clique abaixo para entrar e celebrar cada marco de nossa linda história juntos! ✨💖",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RoseGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (loading) {
                        CircularProgressIndicator(color = RosePrimary)
                    } else {
                        Button(
                            onClick = { 
                                // Direct login: uses partner2Name (Larissa) as default initializer
                                viewModel.completeGoogleSimulation(config.partner2Name)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("entrar_nosso_espaco_btn"),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(Icons.Filled.Favorite, contentDescription = "Heart Icon", modifier = Modifier.size(22.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Entrar no Nosso Espaço", 
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Footer info
                    Text(
                        text = "LaKr — Larissa & Kresley Forever 🔒❤️",
                        style = MaterialTheme.typography.labelSmall,
                        color = RoseGray,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}

fun listWithGradient() = listOf(
    RoseBackground,
    RoseBackground,
    RoseWhite
)

