package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import com.example.ui.components.CyberCard
import com.example.ui.components.GlowText
import com.example.ui.theme.*
import com.example.ui.viewmodel.KittyViewModel

@Composable
fun IsolatedSandboxPlayer(
    viewModel: KittyViewModel,
    modifier: Modifier = Modifier
) {
    val currentApp by viewModel.launchedApp.collectAsState()
    val logs by viewModel.sandboxConsoleLogs.collectAsState()
    val cheatOverlay by viewModel.isCheatOverlayEnabled.collectAsState()
    val gpuTurbo by viewModel.isVirtualGpuTurboEnabled.collectAsState()
    val frameRate by viewModel.frameRate.collectAsState()
    val isConsoleOpen by viewModel.sandboxConsoleOpen.collectAsState()

    // Floating Overlay Tabs (0 = Engine, 1 = Inspect RVA, 2 = Offset Tester, 3 = Logs)
    val floatingTabSelected by viewModel.floatingTabSelected.collectAsState()
    val selectedEngineProfile by viewModel.overlayEngineProfile.collectAsState()
    
    // Inspect states
    val inspectionLogs by viewModel.inspectionTerminalLogs.collectAsState()
    val inspectedName by viewModel.inspectedItemName.collectAsState()
    val inspectedRva by viewModel.inspectedItemRva.collectAsState()
    val inspectedDetails by viewModel.inspectedItemDetails.collectAsState()

    // Offset Tester states
    val offsetAddress by viewModel.offsetTestAddress.collectAsState()
    val offsetHex by viewModel.offsetTestHex.collectAsState()
    val offsetStatus by viewModel.offsetStatusLogs.collectAsState()

    val app = currentApp ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030107))
            .padding(16.dp)
    ) {
        // Upper Sandbox toolbar header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B0C33)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MatrixGreen)
                }

                Column {
                    Text(
                        text = "VIRTUAL RUNNING: ${app.name}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sandbox VM Port PID: ${18000 + app.id} | Engine: ${app.engineType}",
                        color = textMuted,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            IconButton(
                onClick = { viewModel.exitSandbox() },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color(0xFF5A1124))
            ) {
                Icon(Icons.Default.Close, contentDescription = "Exit Sandbox", tint = Color.Red, modifier = Modifier.size(18.dp))
            }
        }

        Divider(color = Color(0xFF1B0739), thickness = 1.dp, modifier = Modifier.padding(bottom = 12.dp))

        // Simulated Graphical viewport (Interactive canvas placeholder)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, MatrixGreen.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .background(Color(0xFF0C061F)),
            contentAlignment = Alignment.Center
        ) {
            // Background grid patterns for hacking radar representation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonalVideo,
                    contentDescription = null,
                    tint = CyberCyan,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                GlowText(
                    text = "SANDBOX DISPLAY ONLINE",
                    glowColor = MatrixGreen,
                    textColor = Color.White,
                    fontSize = 16f
                )
                Text(
                    text = "Simulated dynamic process space active. Tap matching components below or activate the Floating Overlay for dynamic inspection mapping.",
                    color = textMuted,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp, start = 8.dp, end = 8.dp)
                )

                // Flashing frame rates counter
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("FPS RESOLVER", color = textMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("$frameRate FPS", color = MatrixGreen, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("LATENCY METER", color = textMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text("${8 + (frameRate % 6)} ms", color = CyberCyan, fontSize = 22.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }

            // GUARDIAN STYLE DRAGGABLE FLOATING CIRCLE ICON
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(CyberPink, Color(0xFF1F0314))))
                    .border(2.dp, Color.White, CircleShape)
                    .clickable { viewModel.toggleCheatOverlay() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TouchApp,
                    contentDescription = "Float Hub",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // KITTYSPY GAME GUARDIAN EXPANDED MOD-MENU OVERLAY PANEL
            androidx.compose.animation.AnimatedVisibility(
                visible = cheatOverlay,
                enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                exit = fadeOut() + shrinkOut(shrinkTowards = Alignment.Center),
                modifier = Modifier.align(Alignment.Center)
            ) {
                CyberCard(borderColor = CyberPink, modifier = Modifier.fillMaxWidth(0.92f).fillMaxHeight(0.92f)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Dialog header
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Hub, contentDescription = null, tint = CyberPink, modifier = Modifier.size(16.dp))
                                Text(
                                    "KITTYSPY COMPILER CONTROLLER",
                                    color = CyberPink,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Panel",
                                tint = textMuted,
                                modifier = Modifier.size(18.dp).clickable { viewModel.toggleCheatOverlay() }
                            )
                        }

                        Divider(color = Color(0xFF2D164E), modifier = Modifier.padding(bottom = 8.dp))

                        // Custom Tab Rows (Engine | Inspect | Offset Tester | Logs)
                        Row(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(Color(0xFF0F0624)),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            listOf("ENGINE", "INSPECT", "OFFSET", "LOGS").forEachIndexed { index, label ->
                                val selected = floatingTabSelected == index
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                        .clickable { viewModel.selectFloatingTab(index) }
                                        .background(if (selected) CyberPink.copy(alpha = 0.25f) else Color.Transparent)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (selected) Color.White else textMuted,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // TAB CONTENT REGIONS
                        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            when (floatingTabSelected) {
                                0 -> {
                                    // ENGINE SELECTION & SPEC PROFILES (At least 5 Engines supported!)
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            "Select active compiler parsing footprint:",
                                            color = textMuted,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )

                                        val enginesList = listOf("Unity (IL2CPP)", "Unreal Engine 4", "Cocos2D-x", "Godot Core", "CryEngine Native")
                                        
                                        // Grid listing engines
                                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            enginesList.forEach { engine ->
                                                val active = engine == selectedEngineProfile
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (active) Color(0xFF23103D) else Color(0xFF080211))
                                                        .clickable { viewModel.setOverlayEngineProfile(engine) }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(engine, color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                                    if (active) {
                                                        Box(
                                                            modifier = Modifier.size(8.dp).clip(CircleShape).background(MatrixGreen)
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        
                                        // Micro instructions
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF0D031A))
                                                .padding(8.dp)
                                        ) {
                                            Text(
                                                "Active Sandbox target detected: ${app.packageName}\nTarget mapping profiles configured securely in RAM dynamically.",
                                                color = terminalGreen,
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                lineHeight = 12.sp
                                            )
                                        }
                                    }
                                }

                                1 -> {
                                    // INTERACTIVE INSPECTOR DISCOVER ENGINE STRUCTURES
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            "Identify offsets of functional game loops:",
                                            color = textMuted,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )

                                        // Simulation buttons mapping to dynamic functions
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            listOf("ShootWeapon", "JumpSpeed", "SetAmmo", "ResetBase").forEach { tag ->
                                                Box(
                                                    contentAlignment = Alignment.Center,
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFF220F44))
                                                        .clickable { viewModel.analyzeInspectRva(tag) }
                                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                                ) {
                                                    Text(tag, color = CyberCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                                }
                                            }
                                        }

                                        // Trace Result Viewport Box
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.Black)
                                                .border(1.dp, Color(0xFF21104D), RoundedCornerShape(8.dp))
                                                .padding(8.dp)
                                        ) {
                                            if (inspectedName.isEmpty()) {
                                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                    Text(
                                                        "Tap any parameter tab above to inspect dynamic variables.",
                                                        color = textMuted,
                                                        fontSize = 11.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            } else {
                                                Column(modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())) {
                                                    Text(
                                                        text = inspectedDetails,
                                                        color = terminalGreen,
                                                        fontSize = 11.sp,
                                                        fontFamily = FontFamily.Monospace,
                                                        lineHeight = 14.sp
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                2 -> {
                                    // OFFSET TESTER TO PATCH AND RESTORE
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            "Input relative registers offsets to modify memory segments:",
                                            color = textMuted,
                                            fontSize = 9.sp,
                                            fontFamily = FontFamily.Monospace
                                        )

                                        // Row Fields inputs
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Column(modifier = Modifier.weight(1.2f)) {
                                                Text("Virtual RVA Address", color = textMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                                TextField(
                                                    value = offsetAddress,
                                                    onValueChange = { viewModel.setOffsetTestAddress(it) },
                                                    singleLine = true,
                                                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.White),
                                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFF0F0422)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.fillMaxWidth().height(40.dp)
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1.2f)) {
                                                Text("Hex Instruction (Bytes)", color = textMuted, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                                                TextField(
                                                    value = offsetHex,
                                                    onValueChange = { viewModel.setOffsetTestHex(it) },
                                                    singleLine = true,
                                                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.White),
                                                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFF0F0422)),
                                                    shape = RoundedCornerShape(6.dp),
                                                    modifier = Modifier.fillMaxWidth().height(40.dp)
                                                )
                                            }
                                        }

                                        // Patch / Restore Controls Row
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.patchCustomOffset() },
                                                colors = ButtonDefaults.buttonColors(containerColor = CyberPink),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("PATCH INSTRUCTION", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { viewModel.restoreCustomOffset() },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF281353)),
                                                shape = RoundedCornerShape(6.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("RESTORE DEFAULT", color = Color.LightGray, fontSize = 10.sp)
                                            }
                                        }

                                        // Log output console box
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(58.dp)
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color.Black)
                                                .border(1.dp, Color(0xFF190637), RoundedCornerShape(6.dp))
                                                .padding(6.dp)
                                        ) {
                                            Text(
                                                text = offsetStatus,
                                                color = terminalGreen,
                                                fontSize = 9.5.sp,
                                                fontFamily = FontFamily.Monospace,
                                                lineHeight = 12.sp
                                            )
                                        }
                                    }
                                }

                                3 -> {
                                    // STREAMING LOGS LIST
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.fillMaxSize().background(Color.Black).padding(6.dp)
                                    ) {
                                        items(inspectionLogs) { traceLog ->
                                            Text(
                                                text = traceLog,
                                                color = if (traceLog.contains("[TRACE]")) CyberCyan else terminalGreen,
                                                fontFamily = FontFamily.Monospace,
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactivity dashboard bottom button controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.toggleCheatOverlay() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (cheatOverlay) CyberPink else Color(0xFF1E1139)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1.3f)
            ) {
                Icon(Icons.Default.DeveloperMode, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Launch Guardian Overlay", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            Button(
                onClick = { viewModel.toggleGpuTurbo() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (gpuTurbo) MatrixGreen else Color(0xFF1E1139)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1.3f)
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (gpuTurbo) SpaceBackground else Color.White)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (gpuTurbo) "GPU Turbo: ACTIVE" else "GPU Turbo: IDLE",
                    color = if (gpuTurbo) SpaceBackground else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Button(
                onClick = { viewModel.toggleSandboxConsole() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF170E32)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isConsoleOpen) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = textPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Trace", color = textPrimary, fontSize = 11.sp)
            }
        }

        // Expanded log console listing running system outputs
        AnimatedVisibility(
            visible = isConsoleOpen,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .border(1.dp, Color(0xFF28154D), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    "SANDBOX CONTROLLER TRACE BUFFER",
                    color = MatrixGreen,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Divider(color = Color(0xFF1A0738), modifier = Modifier.padding(bottom = 6.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(logs) { logRow ->
                        Text(
                            text = logRow,
                            color = if (logRow.contains("SUCCESS") || logRow.contains("successfully")) MatrixGreen else Color.LightGray,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
