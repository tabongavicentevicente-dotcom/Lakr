package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.CoupleViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaKrTheme {
                val viewModel: CoupleViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(RoseBackground, RoseWhite, RoseBackground)
                                )
                            )
                            .padding(innerPadding)
                    ) {
                        if (isLoggedIn) {
                            LaKrAppContainer(viewModel = viewModel)
                        } else {
                            LoginScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LaKrAppContainer(viewModel: CoupleViewModel) {
    var selectedScreenId by remember { mutableStateOf("home") }

    val navBarItems = listOf(
        NavBarItem("home", "Início", Icons.Filled.Favorite),
        NavBarItem("chat", "Conversar", Icons.Filled.Sms),
        NavBarItem("album", "Mural", Icons.Filled.PhotoLibrary),
        NavBarItem("calendar", "Datas", Icons.Filled.CalendarMonth),
        NavBarItem("lakr_ai", "LaKr IA", Icons.Filled.AutoAwesome),
        NavBarItem("more", "Baú", Icons.Filled.CardGiftcard)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = RoseWhite,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("app_navigation_bar")
            ) {
                navBarItems.forEach { item ->
                    val isActive = selectedScreenId == item.id
                    
                    NavigationBarItem(
                        selected = isActive,
                        onClick = { selectedScreenId = item.id },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                color = if (isActive) MaterialTheme.colorScheme.onSurface else Color.Gray
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = SoftButtonBorderPink
                        ),
                        modifier = Modifier.testTag("nav_item_${item.id}")
                    )
                }
            }
        },
        containerColor = Color.Transparent,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = selectedScreenId,
                transitionSpec = {
                    fadeIn(tween(250)) togetherWith fadeOut(tween(250))
                },
                label = "MainScreenNavigation"
            ) { targetScreen ->
                when (targetScreen) {
                    "home" -> DashboardScreen(viewModel = viewModel)
                    "chat" -> ChatScreen(viewModel = viewModel)
                    "album" -> AlbumScreen(viewModel = viewModel)
                    "calendar" -> CalendarScreen(viewModel = viewModel)
                    "lakr_ai" -> LaKrAiScreen(viewModel = viewModel)
                    "more" -> MoreFeaturesScreen(viewModel = viewModel)
                }
            }
        }
    }
}

data class NavBarItem(
    val id: String,
    val label: String,
    val icon: ImageVector
)
