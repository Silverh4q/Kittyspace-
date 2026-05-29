package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.VirtualApp
import com.example.ui.components.AppIconRenderer
import com.example.ui.components.GlowText
import com.example.ui.theme.*
import com.example.ui.viewmodel.KittyViewModel
import com.example.ui.viewmodel.InstalledAppInfo

@Composable
fun KittySpaceScreen(
    viewModel: KittyViewModel,
    modifier: Modifier = Modifier
) {
    val apps by viewModel.apps.collectAsState()
    val deviceApps by viewModel.deviceInstalledApps.collectAsState()
    val isLoadingApps by viewModel.isLoadingApps.collectAsState()
    
    var isShowingDeviceAppsDialog by remember { mutableStateOf(false) }
    var selectedAppToClone by remember { mutableStateOf<InstalledAppInfo?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        
        GlowText(
            text = "KITTYSPACE",
            glowColor = MatrixGreen,
            textColor = Color.White,
            fontSize = 30f,
            letterSpacing = 6f
        )
        
        Text(
            text = "VIRTUAL SANDBOX ISOLATION RUNTIME",
            color = textMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
        )

        // Workspace cards list - represented as a high contrast glassy viewport box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .border(
                    1.2.dp, 
                    Color.White.copy(alpha = 0.12f), 
                    RoundedCornerShape(20.dp)
                )
                // Frosted Gradient Backing Glass Box
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC0E0520),
                            Color(0xEE05010C)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // First tile is always the "ADD / CLONE" button
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                viewModel.fetchDeviceApps()
                                isShowingDeviceAppsDialog = true
                            }
                    ) {
                        // Glass Box Addition button
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0x1F8CFFA8))
                                .border(1.5.dp, MatrixGreen.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Application",
                                tint = MatrixGreen,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Add App",
                            color = MatrixGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Cloned active apps displayed beautifully in Glassy boxes
                items(apps) { app ->
                    var showDetailDialog by remember { mutableStateOf(false) }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { showDetailDialog = true }
                    ) {
                        // GLASSY BOX - Frosted frame enclosing the icon
                        Box(
                            modifier = Modifier
                                .size(78.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0x14FFFFFF))
                                .border(
                                    1.2.dp, 
                                    if (app.engineType == "Unity") CyberCyan.copy(alpha = 0.4f) else CyberPink.copy(alpha = 0.4f), 
                                    RoundedCornerShape(20.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(modifier = Modifier.size(54.dp)) {
                                AppIconRenderer(iconName = app.iconName)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = app.name,
                            color = textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
                        
                        Text(
                            text = app.engineType.uppercase(),
                            color = if (app.engineType == "Unity") CyberCyan else CyberPink,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // DETAIL OPTIONS DIALOG FOR APPS IN THE ENVIRONMENT
                    if (showDetailDialog) {
                        Dialog(onDismissRequest = { showDetailDialog = false }) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.5.dp, Color(0xFF381566), RoundedCornerShape(24.dp))
                                    .padding(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Glassy app icon frame
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(22.dp))
                                            .background(Color(0x11FFFFFF))
                                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(22.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(modifier = Modifier.size(60.dp)) {
                                            AppIconRenderer(iconName = app.iconName)
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(14.dp))
                                    
                                    Text(
                                        text = app.name,
                                        color = textPrimary,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                    
                                    Text(
                                        text = app.packageName,
                                        color = textMuted,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Attribute properties row
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF0F0721))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("ENGINE", color = textMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                            Text(app.engineType, color = if (app.engineType == "Unity") CyberCyan else CyberPink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("SANDBOX", color = textMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                            Text("ACTIVE", color = MatrixGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("VERSION", color = textMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                            Text(app.version, color = Color.LightGray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // Primary Launch action buttons
                                    Button(
                                        onClick = {
                                            showDetailDialog = false
                                            viewModel.launchVirtualApp(app)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SpaceBackground)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Launch in Sandbox", color = SpaceBackground, fontWeight = FontWeight.Bold)
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = {
                                                showDetailDialog = false
                                                viewModel.setSelectedDumperApp(app)
                                                viewModel.setDumpSelectionMode("SelectedApp")
                                                viewModel.selectEngineSection(if (app.engineType == "Unity") 0 else 1)
                                                viewModel.setSpace(1) // Navigation: KittySpy Dumper Tab
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28134F)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("Inspect & Dump", color = textPrimary, fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                showDetailDialog = false
                                                viewModel.removeClone(app)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF531121)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Uninstall", color = Color.Red, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // PHYSICAL INSTALLED APPLICATIONS DISCOVERY SELECTOR SHEET/DIALOG
        if (isShowingDeviceAppsDialog) {
            Dialog(onDismissRequest = { isShowingDeviceAppsDialog = false }) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                        .border(1.5.dp, MatrixGreen, RoundedCornerShape(24.dp))
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "SELECT APPLICATION",
                            color = MatrixGreen,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        
                        Text(
                            text = "Scanned host applications found on your physical device storage",
                            color = textMuted,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Search box matching styles
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search installed packages...", color = textMuted.copy(alpha = 0.5f)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MatrixGreen) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF130926),
                                unfocusedContainerColor = Color(0xFF0C051A),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = MatrixGreen
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )

                        // Loading Indicators
                        if (isLoadingApps) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = MatrixGreen)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("Reading package registry...", color = textMuted, fontSize = 12.sp)
                                }
                            }
                        } else {
                            val filteredApps = deviceApps.filter {
                                it.name.contains(searchQuery, ignoreCase = true) ||
                                it.packageName.contains(searchQuery, ignoreCase = true)
                            }

                            if (filteredApps.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No matching applications found.", color = textMuted, fontSize = 12.sp)
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(filteredApps) { deviceApp ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFF140B2D))
                                                .clickable {
                                                    selectedAppToClone = deviceApp
                                                }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Tiny app icon circle representation
                                            Box(
                                                modifier = Modifier
                                                    .size(42.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF231448)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Gamepad,
                                                    contentDescription = null,
                                                    tint = if (deviceApp.defaultEngine == "Unity") CyberCyan else CyberPink,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = deviceApp.name,
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    text = deviceApp.packageName,
                                                    color = textMuted,
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            
                                            // Chip showing inferred core compiler profile
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(if (deviceApp.defaultEngine == "Unity") Color(0x3300E5FF) else Color(0x33FF007F))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = deviceApp.defaultEngine.uppercase(),
                                                    color = if (deviceApp.defaultEngine == "Unity") CyberCyan else CyberPink,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { isShowingDeviceAppsDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28134F)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Close", color = textPrimary)
                        }
                    }
                }
            }
        }

        // ASSIGNED CLONE CONFIRMATION DIALOG 
        if (selectedAppToClone != null) {
            val appToConfirm = selectedAppToClone!!
            
            Dialog(onDismissRequest = { selectedAppToClone = null }) {
                var selectedEngineType by remember { mutableStateOf(appToConfirm.defaultEngine) }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.5.dp, MatrixGreen, RoundedCornerShape(24.dp))
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "MOUNT SANDBOX CLONE",
                            color = MatrixGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text("Application Name", color = textMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            text = appToConfirm.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp, top = 2.dp)
                        )

                        Text("Package Namespace Identifier", color = textMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            text = appToConfirm.packageName,
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 16.dp, top = 2.dp)
                        )

                        Text("Select Compiler Engine Profile", color = textMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { selectedEngineType = "Unity" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedEngineType == "Unity") CyberCyan else Color(0xFF1E1338)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "UNITY (libil2cpp)", 
                                    color = if (selectedEngineType == "Unity") SpaceBackground else Color.White, 
                                    fontSize = 11.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = { selectedEngineType = "Unreal" },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedEngineType == "Unreal") CyberPink else Color(0xFF1E1338)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "UNREAL (libue4)", 
                                    color = if (selectedEngineType == "Unreal") SpaceBackground else Color.White, 
                                    fontSize = 11.sp, 
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { selectedAppToClone = null },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancel")
                            }

                            Button(
                                onClick = {
                                    viewModel.cloneApp(
                                        name = appToConfirm.name,
                                        pkg = appToConfirm.packageName,
                                        engine = selectedEngineType
                                    )
                                    selectedAppToClone = null
                                    isShowingDeviceAppsDialog = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Clone Game", color = SpaceBackground, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
