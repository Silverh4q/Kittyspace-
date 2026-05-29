package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DumpHistory
import com.example.data.model.VirtualApp
import com.example.ui.components.CyberCard
import com.example.ui.components.GlowText
import com.example.ui.theme.*
import com.example.ui.viewmodel.KittyViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun KittyDumperScreen(
    viewModel: KittyViewModel,
    modifier: Modifier = Modifier
) {
    val apps by viewModel.apps.collectAsState()
    val dumps by viewModel.dumps.collectAsState()
    val selectedEngineSection by viewModel.selectedEngineSection.collectAsState() // 0 = Unity, 1 = Unreal
    val selectionMode by viewModel.dumpSelectionMode.collectAsState() // "SelectedApp", "Folder"
    val selectedApp by viewModel.selectedDumperApp.collectAsState()
    
    // Paths
    val unityLibPath by viewModel.unityLibPathSelected.collectAsState()
    val unityMetadataPath by viewModel.unityMetadataPathSelected.collectAsState()
    val unrealLibPath by viewModel.unrealLibPathSelected.collectAsState()
    
    // Live Terminal
    val consoleLogs by viewModel.dumperConsoleLogs.collectAsState()
    val progress by viewModel.dumperProgress.collectAsState()
    val isDumping by viewModel.isDumping.collectAsState()
    val terminalCommand by viewModel.terminalCommand.collectAsState()
    
    // Errors
    val errorMessage by viewModel.errorMessage.collectAsState()

    // File browser state
    val browsableItems by viewModel.browsableItems.collectAsState()
    val currentDir by viewModel.currentDir.collectAsState()
    var showingFileSelectorType by remember { mutableStateOf<String?>(null) } // "unityLib", "unityMeta", "unrealLib" or null
    
    var showAppSelector by remember { mutableStateOf(false) }
    var selectedViewDump by remember { mutableStateOf<DumpHistory?>(null) }
    
    val listState = rememberLazyListState()

    // Scroll automatically
    LaunchedEffect(consoleLogs.size) {
        if (consoleLogs.isNotEmpty()) {
            listState.animateScrollToItem(consoleLogs.size - 1)
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpaceBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Title Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GlowText(
                    text = "KITTYSPY",
                    glowColor = CyberPink,
                    textColor = Color.White,
                    fontSize = 32f,
                    letterSpacing = 5f
                )
                Text(
                    text = "NATIVE BINARY DUMPER PIPELINE",
                    color = textMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Active Engine Toggle Block (Unity vs Unreal selected by user)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.selectEngineSection(0) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedEngineSection == 0) Color(0xFF0F2B30) else Color(0xFF1E1438)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(
                            1.2.dp,
                            if (selectedEngineSection == 0) CyberCyan else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = if (selectedEngineSection == 0) CyberCyan else textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "UNITY ENGINE",
                        color = if (selectedEngineSection == 0) Color.White else textMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = { viewModel.selectEngineSection(1) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedEngineSection == 1) Color(0xFF3B152F) else Color(0xFF1E1438)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .border(
                            1.2.dp,
                            if (selectedEngineSection == 1) CyberPink else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        tint = if (selectedEngineSection == 1) CyberPink else textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "UNREAL SDK",
                        color = if (selectedEngineSection == 1) Color.White else textMuted,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Selection Target & Coordinates Block
        item {
            CyberCard(borderColor = if (selectedEngineSection == 0) CyberCyan else CyberPink) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (selectedEngineSection == 0) "I. UNITY DUMP CONFIGURATION" else "I. UNREAL DUMP CONFIGURATION",
                        color = if (selectedEngineSection == 0) CyberCyan else CyberPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    // Source Mode Selector Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0F0422)),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectionMode == "SelectedApp") Color(0xFF1B0741) else Color.Transparent)
                                .clickable { viewModel.setDumpSelectionMode("SelectedApp") }
                        ) {
                            Text(
                                "Virtual App Target",
                                color = if (selectionMode == "SelectedApp") Color.White else textMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectionMode == "Folder") Color(0xFF1B0741) else Color.Transparent)
                                .clickable { viewModel.setDumpSelectionMode("Folder") }
                        ) {
                            Text(
                                if (selectedEngineSection == 0) "Select from Storage" else "Select from Folder",
                                color = if (selectionMode == "Folder") Color.White else textMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // ACTIVE MODE UI INTERFACES
                    if (selectionMode == "SelectedApp") {
                        // Display dropdown button to choose virtual application targets
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0D031A))
                                .border(1.dp, Color(0xFF381B6D), RoundedCornerShape(10.dp))
                                .clickable { showAppSelector = true }
                                .padding(horizontal = 12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsEsports,
                                contentDescription = null,
                                tint = if (selectedEngineSection == 0) CyberCyan else CyberPink
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = selectedApp?.name ?: "Select app from sandwich list...",
                                    color = if (selectedApp != null) Color.White else textMuted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                               )
                                if (selectedApp != null) {
                                    Text(
                                        text = selectedApp?.packageName ?: "",
                                        color = textMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = textMuted)
                        }
                    } else {
                        // MANUAL FOLDER SELECTORS SECTIONS
                        if (selectedEngineSection == 0) {
                            // UNITY MANUALLY DEFINED FILE COORDINATES
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                // Field 1: libil2cpp.so selector
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "Select path to libil2cpp.so binary:",
                                        color = textMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF04010B))
                                            .border(1.dp, Color(0xFF34175E), RoundedCornerShape(8.dp))
                                            .clickable { showingFileSelectorType = "unityLib" }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Folder, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (unityLibPath.isEmpty()) "Tap to select libil2cpp.so file..." else File(unityLibPath).name,
                                            color = if (unityLibPath.isNotEmpty()) Color.White else textMuted,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(Icons.Default.Launch, contentDescription = null, tint = textMuted, modifier = Modifier.size(16.dp))
                                    }
                                }

                                // Field 2: global-metadata.dat selector
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        "Select path to global-metadata.dat:",
                                        color = textMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF04010B))
                                            .border(1.dp, Color(0xFF34175E), RoundedCornerShape(8.dp))
                                            .clickable { showingFileSelectorType = "unityMeta" }
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.FileCopy, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (unityMetadataPath.isEmpty()) "Tap to select global-metadata.dat..." else File(unityMetadataPath).name,
                                            color = if (unityMetadataPath.isNotEmpty()) Color.White else textMuted,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Icon(Icons.Default.Launch, contentDescription = null, tint = textMuted, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        } else {
                            // UNREAL MANUALLY DEFINED FILE COORDINATES (ONLY 1 FIELD)
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    "Select path to libue4.so file:",
                                    color = textMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF04010B))
                                        .border(1.dp, Color(0xFF34175E), RoundedCornerShape(8.dp))
                                        .clickable { showingFileSelectorType = "unrealLib" }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = CyberPink, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (unrealLibPath.isEmpty()) "Tap to select libue4.so binary..." else File(unrealLibPath).name,
                                        color = if (unrealLibPath.isNotEmpty()) Color.White else textMuted,
                                        fontSize = 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(Icons.Default.Launch, contentDescription = null, tint = textMuted, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live error dialog block popup
        if (errorMessage != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x22FF0000))
                        .border(1.dp, Color.Red, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = Color.Red,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { viewModel.setErrorMessage(null) }
                    )
                }
            }
        }

        // Live Terminal Console System Box
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF040108))
                    .border(1.5.dp, if (selectedEngineSection == 0) CyberCyan.copy(alpha = 0.5f) else CyberPink.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Console bar title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F0421))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Red))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.Yellow))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MatrixGreen))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (selectedEngineSection == 1) "EMBEDDED RADARE2 TERMINAL" else "UNITY ANALYSIS SHELL",
                            color = textPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear Terminal",
                        tint = textMuted,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { viewModel.setTerminalCommand("clear"); viewModel.runTerminalCommand() }
                    )
                }

                // Scrolling console outputs list
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(consoleLogs) { log ->
                            Text(
                                text = log,
                                color = when {
                                    log.contains("SUCCESS") || log.contains("COMPLETED") -> MatrixGreen
                                    log.contains("[FOUND]") -> CyberCyan
                                    log.startsWith(">") -> Color.White
                                    log.contains("[ERROR]") -> Color.Red
                                    log.contains("radare2") || log.contains("rabin2") -> CyberPink
                                    else -> terminalGreen
                                },
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // Interactive terminal CLI command input bar
                Column {
                    HorizontalDivider(color = Color(0xFF190637), thickness = 1.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF020005))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "kittyspy# ",
                            color = if (selectedEngineSection == 0) CyberCyan else CyberPink,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        androidx.compose.foundation.text.BasicTextField(
                            value = terminalCommand,
                            onValueChange = { viewModel.setTerminalCommand(it) },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = { viewModel.runTerminalCommand() }),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 6.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Send Command",
                            tint = if (selectedEngineSection == 0) CyberCyan else CyberPink,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { viewModel.runTerminalCommand() }
                        )
                    }
                }
            }
        }

        // Live compiling progress bar representation
        if (isDumping || progress > 0f) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (progress >= 1.0f) "DUMP PIPELINE COMPLETED" else "EXTRACTING ENGINE STRUCTURES...",
                            color = if (progress >= 1.0f) MatrixGreen else CyberCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            color = if (progress >= 1.0f) MatrixGreen else CyberCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        color = if (progress >= 1.0f) MatrixGreen else if (selectedEngineSection == 0) CyberCyan else CyberPink,
                        trackColor = Color(0xFF140728),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }

        // Main start compilation pipeline action trigger button
        item {
            Button(
                onClick = { viewModel.startDumper() },
                enabled = !isDumping && (selectionMode == "Folder" || selectedApp != null),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedEngineSection == 0) CyberCyan else CyberPink,
                    disabledContainerColor = Color(0xFF33164F)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .border(
                        1.2.dp,
                        if (isDumping.not()) MatrixGreen else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
            ) {
                if (isDumping) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Deobfuscating & Sorting Assets...", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Layers, contentDescription = null, tint = SpaceBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "START COMPILER ENGINE DUMPER",
                        color = SpaceBackground,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }

        // Saved Compiled Dumps History Section representation
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "II. COMPILED STRUCTURE SAVES HISTORY",
                    color = textMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (dumps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .border(1.dp, Color(0xFF1D0E39), RoundedCornerShape(10.dp))
                            .background(Color(0xFF0D031D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No dumps executed in this session yet.\nStart dumper to save compiled structures to storage.",
                            color = textMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )
                    }
                } else {
                    dumps.forEach { dump ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFF3E1F73), RoundedCornerShape(10.dp))
                                .clickable { selectedViewDump = dump },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = dump.appName,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Saved Path: ${dump.dumpPath}",
                                        color = textMuted,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = dump.dumpSummary,
                                        color = MatrixGreen,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (dump.engineType == "Unity") Color(0xFF0F3A41) else Color(0xFF551433))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = dump.engineType,
                                            color = if (dump.engineType == "Unity") CyberCyan else CyberPink,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }

                                    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
                                    Text(
                                        text = format.format(Date(dump.timestamp)),
                                        color = textMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // VIRTUAL FILE DIRECTORY EXPLORER DIALOG representation
    if (showingFileSelectorType != null) {
        val type = showingFileSelectorType!!
        
        Dialog(onDismissRequest = { showingFileSelectorType = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.75f)
                    .border(1.5.dp, Color(0xFF421D7A), RoundedCornerShape(20.dp))
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp)
                ) {
                    Text(
                        text = "SELECT FILE STORAGE",
                        color = CyberCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    
                    Text(
                        text = "Target directory: ${currentDir.replace(".*/files/".toRegex(), "/sandbox/")}",
                        color = textMuted,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Navigation items lists
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(browsableItems) { fileItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF100624))
                                    .clickable {
                                        if (fileItem.isDirectory) {
                                            if (fileItem.name.contains("Up")) {
                                                viewModel.navigateUp()
                                            } else {
                                                viewModel.navigateToDir(fileItem.path)
                                            }
                                        } else {
                                            // Handle file selection callback
                                            when (type) {
                                                "unityLib" -> viewModel.selectUnityLibPath(fileItem.path)
                                                "unityMeta" -> viewModel.selectUnityMetadataPath(fileItem.path)
                                                "unrealLib" -> viewModel.selectUnrealLibPath(fileItem.path)
                                            }
                                            showingFileSelectorType = null
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (fileItem.isDirectory) Icons.Default.FolderOpen else Icons.Default.InsertDriveFile,
                                    contentDescription = null,
                                    tint = if (fileItem.isDirectory) Color(0xFFFFB300) else Color.LightGray,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = fileItem.name,
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = if (fileItem.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (!fileItem.isDirectory) {
                                        Text(
                                            text = fileItem.sizeLabel,
                                            color = textMuted,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                
                                Icon(
                                    imageVector = if (fileItem.isDirectory) Icons.Default.ChevronRight else Icons.Default.Check,
                                    contentDescription = null,
                                    tint = textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showingFileSelectorType = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C1E4F)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = textPrimary)
                        }
                    }
                }
            }
        }
    }

    // CLONED SELECTOR APP DIALOG OVERLAY
    if (showAppSelector) {
        Dialog(onDismissRequest = { showAppSelector = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFF381B6D), RoundedCornerShape(20.dp))
                    .padding(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CHOOSE TARGET CLONE app",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Empty check
                    if (apps.isEmpty()) {
                        Text(
                            text = "Please add active apps inside the KittySpace panel first before choosing targets here.",
                            color = textMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 14.dp)
                        )
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.heightIn(max = 240.dp)) {
                            items(apps) { app ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF0F0422))
                                        .clickable {
                                            viewModel.setSelectedDumperApp(app)
                                            showAppSelector = false
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(app.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(app.packageName, color = textMuted, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (app.engineType == "Unity") Color(0x3300E5FF) else Color(0x33FF007F))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = app.engineType,
                                            color = if (app.engineType == "Unity") CyberCyan else CyberPink,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { showAppSelector = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF28134F)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dismiss", color = textPrimary)
                    }
                }
            }
        }
    }

    // SIMULATED FILE CODE HIGHLIGHT VIEWER MODAL representation
    if (selectedViewDump != null) {
        val dump = selectedViewDump!!
        Dialog(onDismissRequest = { selectedViewDump = null }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF030107)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .border(1.5.dp, CyberCyan, RoundedCornerShape(20.dp))
                    .padding(4.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF130628))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (dump.engineType == "Unity") "il2cpp.h (Header Dump Output)" else "SDK.h (Unreal Structures SDK)",
                                color = CyberCyan,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Saved Path: ${dump.dumpPath}",
                                color = textMuted,
                                fontSize = 9.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable { selectedViewDump = null }
                        )
                    }

                    val codeContent = remember(dump.id) {
                        if (dump.engineType == "Unity") {
                            """
                            // Generated by KittySpy il2cpp-dumper pipeline v3.4.1
                            // Target: ${dump.appName} (${dump.packageName})
                            // Dumping Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(dump.timestamp))}
                            
                            #include "il2cpp-config.h"
                            #include "il2cpp-class-metadata.h"
                            
                            // Namespace: UnityEngine.CoreModule
                            public class Camera : Behaviour {
                                // Fields
                                public static Camera main; // Offset: 0xEE2A42F0
                                private float fieldOfView; // Offset: 0xEE2A4304
                                private float aspect; // Offset: 0xEE2A4308
                                
                                // Properties
                                public float Aspect { get; }
                                public float FieldOfView { get; set; }
                                
                                // Methods
                                public void Render(); // Address: 0xEE14C800
                                public static Camera GetMainCamera(); // Address: 0xEE14CA12
                                private void ResetAspect(); // Address: 0xEE14D120
                            }
                            
                            // Namespace: Game.Logic.Engine
                            public class PlayerController : MonoBehaviour {
                                // Fields
                                public float moveSpeed; // Offset: 0xFE120AA0
                                public int playerIndex; // Offset: 0xFE120AA4
                                public bool isMoving; // Offset: 0xFE120AA8
                                private string currentWeaponName; // Offset: 0xFE120ABC
                                
                                // Methods
                                public void SetSpeed(float speed); // Address: 0xFE45B602
                                public float GetCurrentHealth(); // Address: 0xFE45B724
                                public void FireWeapon(); // Address: 0xFE45F440
                                private void HandleJumpMotion(); // Address: 0xFE462D00
                            }
                            
                            // Ghidra script generation segment:
                            // Name: ghidra_il2cpp_helper.py
                            import ghidra.util.task.TaskMonitor
                            print("[KITTYSPY] Script loaded. Marking RVA offsets in program database...")
                            currentProgram.setImageBase(toAddr(0xFE450000))
                            createLabel(toAddr(0xFE45B602), "PlayerController_SetSpeed", True)
                            createLabel(toAddr(0xFE45B724), "PlayerController_GetCurrentHealth", True)
                            createLabel(toAddr(0xFE45F440), "PlayerController_FireWeapon", True)
                            print("[KITTYSPY] Successfully marked offsets inside Ghidra database!")
                            """.trimIndent()
                        } else {
                            """
                            // Generated by KittySpy UnrealSDK dumper pipeline v3.4.1
                            // Target: ${dump.appName} (${dump.packageName})
                            // Dumping Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(dump.timestamp))}
                            
                            #pragma once
                            #include "SDK_Core.h"
                            
                            // Class Engine.Actor
                            // Size: 0x0250 (Inherited: 0x00A0)
                            class AActor : public UObject {
                            public:
                                float CustomTimeDilation; // 0x00A0 (0x0004)
                                bool bActorEnableCollision; // 0x00A4 (0x0001)
                                struct FVector RelativeLocation; // 0x0114 (0x000C)
                                struct FRotator RelativeRotation; // 0x0120 (0x000C)
                                
                                // Functions
                                void SetActorLocation(struct FVector NewLocation, bool bSweep); // Function Engine.Actor.SetActorLocation
                                void K2_DestroyActor(); // Function Engine.Actor.K2_DestroyActor
                                float GetDistanceTo(class AActor* OtherActor); // Function Engine.Actor.GetDistanceTo
                            };
                            
                            // Class ShooterGame.ShooterCharacter
                            // Size: 0x04C0 (Inherited: 0x0250)
                            class AShooterCharacter : public AActor {
                            public:
                                float Health; // 0x0250 (0x0004)
                                float MaxHealth; // 0x0254 (0x0004)
                                float TargetFov; // 0x0312 (0x0004)
                                struct FString CharacterName; // 0x0320 (0x0010)
                                class USkeletalMeshComponent* CharacterMesh; // 0x0418 (0x0008)
                                
                                // Functions
                                void ShootMainWeapon(); // Function ShooterGame.ShooterCharacter.ShootMainWeapon
                                bool IsLocallyControlled(); // Function ShooterGame.ShooterCharacter.IsLocallyControlled
                                void AddRecoil(float PitchAmt, float YawAmt); // Function ShooterGame.ShooterCharacter.AddRecoil
                            };
                            """.trimIndent()
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF020006))
                            .padding(14.dp)
                    ) {
                        item {
                            Text(
                                text = codeContent,
                                color = terminalGreen,
                                fontSize = 10.5.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Button(
                        onClick = { selectedViewDump = null },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text("Exit SDK Viewer", color = SpaceBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
