package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CatPawLogo
import com.example.ui.components.GlowText
import com.example.ui.theme.*
import com.example.ui.viewmodel.KittyViewModel

@Composable
fun MainMasterScreen(
    viewModel: KittyViewModel,
    modifier: Modifier = Modifier
) {
    val currentSpace by viewModel.currentSpace.collectAsState()
    val launchedApp by viewModel.launchedApp.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (launchedApp == null) {
                NavigationBar(
                    containerColor = Color(0xFF100525),
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentSpace == 0,
                        onClick = { viewModel.setSpace(0) },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text("KittySpace", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SpaceBackground,
                            selectedTextColor = MatrixGreen,
                            indicatorColor = MatrixGreen,
                            unselectedIconColor = textMuted,
                            unselectedTextColor = textMuted
                        )
                    )

                    NavigationBarItem(
                        selected = currentSpace == 1,
                        onClick = { viewModel.setSpace(1) },
                        icon = { Icon(Icons.Default.FolderZip, contentDescription = null) },
                        label = { Text("KittySpy Dumper", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SpaceBackground,
                            selectedTextColor = CyberPink,
                            indicatorColor = CyberPink,
                            unselectedIconColor = textMuted,
                            unselectedTextColor = textMuted
                        )
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceBackground)
                .padding(innerPadding)
        ) {
            // IF A GAME IS EXECUTED IN ISOLATED RUNTIME PORT
            if (launchedApp != null) {
                IsolatedSandboxPlayer(viewModel = viewModel)
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header Area with Cat Eye Symbol & Branding Logo
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF060111))
                            .padding(vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CatPawLogo(
                            iconSize = 54.dp,
                            glowColor = if (currentSpace == 0) MatrixGreen else CyberPink
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        GlowText(
                            text = "KITTYSYSTEM",
                            glowColor = if (currentSpace == 0) MatrixGreen else CyberPink,
                            textColor = Color.White,
                            fontSize = 20f,
                            letterSpacing = 6f
                        )
                    }

                    // Content Space Viewports with sliding animations
                    Box(modifier = Modifier.weight(1f)) {
                        AnimatedContent(
                            targetState = currentSpace,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInHorizontally(animationSpec = tween(300)) { width -> width } + fadeIn(animationSpec = tween(300)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> -width } + fadeOut(animationSpec = tween(300)))
                                } else {
                                    (slideInHorizontally(animationSpec = tween(300)) { width -> -width } + fadeIn(animationSpec = tween(300)))
                                        .togetherWith(slideOutHorizontally(animationSpec = tween(300)) { width -> width } + fadeOut(animationSpec = tween(300)))
                                }
                            },
                            label = "space_selector_animation"
                        ) { space ->
                            when (space) {
                                0 -> KittySpaceScreen(viewModel = viewModel)
                                1 -> KittyDumperScreen(viewModel = viewModel)
                            }
                        }
                    }

                    // Bottom Space Switcher Bar Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF04000D))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentSpace == 0) "MODE: KITTYSPACE LANDS" else "MODE: KITTYSPY DUMPER",
                            color = if (currentSpace == 0) MatrixGreen else CyberPink,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )

                        // Arrow Button (Changes space between kittySpace and dump space)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { viewModel.setSpace(0) },
                                enabled = currentSpace != 0,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (currentSpace != 0) Color(0xFF1E1139) else Color(0xFF100725))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "To KittySpace",
                                    tint = if (currentSpace != 0) MatrixGreen else textMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.setSpace(1) },
                                enabled = currentSpace != 1,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (currentSpace != 1) Color(0xFF1E1139) else Color(0xFF100725))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "To KittySpy Dumper",
                                    tint = if (currentSpace != 1) CyberPink else textMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
