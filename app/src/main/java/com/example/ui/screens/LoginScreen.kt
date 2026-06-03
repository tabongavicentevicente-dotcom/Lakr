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
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF1E1A2B), Color(0xFF0C0C0E))
                            )
                        )
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(RosePrimary, Color.Transparent, RoseTertiary)
                            ),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = "LaKr Logo Heart",
                        tint = RoseSecondary,
                        modifier = Modifier.size(46.dp)
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
            }

            // Entry Form Card
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
                        text = "Conecte-se com sua metade",
                        style = MaterialTheme.typography.titleMedium,
                        color = CocoaDark,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    if (loading) {
                        CircularProgressIndicator(color = RosePrimary)
                    } else if (!isPhoneFlowSelected) {
                        // Choice Menu
                        Button(
                            onClick = { viewModel.loginWithGoogle() },
                            colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("google_login_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.LockOpen, contentDescription = "Google Icon", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Entrar com Google", style = MaterialTheme.typography.labelLarge)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedButton(
                            onClick = { isPhoneFlowSelected = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CocoaDark),
                            border = BorderStroke(1.dp, GoldMetallic),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("phone_login_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.Phone, contentDescription = "Phone Icon", modifier = Modifier.size(20.dp), tint = GoldMetallic)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Entrar com Telefone", style = MaterialTheme.typography.labelLarge, color = CocoaDark)
                        }
                    } else {
                        // Phone Auth Screen
                        if (!isOtpSent) {
                            Text(
                                text = "Insira o seu telefone de amor",
                                style = MaterialTheme.typography.bodyMedium,
                                color = RoseGray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                label = { Text("Número de celular") },
                                leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = RosePrimary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("phone_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RosePrimary,
                                    unfocusedBorderColor = GoldChampagne
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    if (phoneInput.length >= 8) {
                                        viewModel.triggerPhoneVerification(phoneInput)
                                    } else {
                                        showErrorMessage = "Por favor, insira um número válido!"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = RosePrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("send_otp_btn")
                            ) {
                                Text("Enviar Código do Amor")
                            }

                            TextButton(onClick = { isPhoneFlowSelected = false }) {
                                Text("Voltar", color = GoldDark)
                            }
                        } else {
                            // OTP verification Screen
                            Text(
                                text = "Digitando o Cupom de Acesso",
                                style = MaterialTheme.typography.titleMedium,
                                color = RosePrimary,
                                modifier = Modifier.padding(bottom = 4.dp),
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Para simular, use o dia do seu amor: 1214",
                                style = MaterialTheme.typography.bodySmall,
                                color = GoldDark,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = otpInput,
                                onValueChange = { otpInput = it },
                                label = { Text("Código de 4 dígitos") },
                                leadingIcon = { Icon(Icons.Filled.Key, contentDescription = null, tint = GoldMetallic) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("otp_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GoldChampagne,
                                    unfocusedBorderColor = RoseTertiary
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val success = viewModel.verifyPhoneOtp(otpInput)
                                    if (!success) {
                                        showErrorMessage = "Código inválido. Use '1214'!"
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldMetallic),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("confirm_otp_btn")
                            ) {
                                Text("Confirmar Cupido")
                            }

                            TextButton(onClick = { viewModel.isOtpSent.value = false }) {
                                Text("Reenviar código", color = RosePrimary)
                            }
                        }
                    }

                    showErrorMessage?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Footer info
            Text(
                text = "LaKr — Larissa & Kresley Forever 🔒❤️",
                style = MaterialTheme.typography.labelSmall,
                color = RoseGray,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )
        }

        // Google / OAuth Configuration Guidance Dialog / Sheet
        if (showOauthGuidance) {
            AlertDialog(
                onDismissRequest = { viewModel.currentOauthGuidanceVisible.value = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, contentDescription = null, tint = GoldMetallic, modifier = Modifier.size(28.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Configuração do Google", style = MaterialTheme.typography.titleLarge)
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Para habilitar o login real do Google com segurança, você precisa registrar as chaves SHA-1 no Firebase Console e arrumar o Google Identity.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CocoaDark
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Deseja testar o aplicativo entrando como um dos amantes agora mesmo?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = RosePrimary
                        )
                    }
                },
                confirmButton = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { viewModel.completeGoogleSimulation(config.partner1Name) },
                            colors = ButtonDefaults.buttonColors(containerColor = SoftButtonBorderPink()),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Entrar como ${config.partner1Name}", color = CocoaDark)
                        }
                        
                        Button(
                            onClick = { viewModel.completeGoogleSimulation(config.partner2Name) },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldSoft),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Entrar como ${config.partner2Name}", color = CocoaDark)
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.currentOauthGuidanceVisible.value = false }) {
                        Text("Cancelar", color = RoseGray)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = RoseWhite
            )
        }
    }
}

fun listWithGradient() = listOf(
    RoseBackground,
    RoseBackground,
    RoseWhite
)

fun SoftButtonBorderPink() = Color(0xFF3B0764)

