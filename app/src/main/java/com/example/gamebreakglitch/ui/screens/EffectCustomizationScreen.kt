package com.example.gamebreakglitch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamebreakglitch.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EffectCustomizationScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Visual Effect Customization") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Effect Intensity Level", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    listOf("MILD", "MEDIUM", "EXTREME").forEach { level ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.intensityLevel == level,
                                onClick = { viewModel.updateSettings(settings.copy(intensityLevel = level)) }
                            )
                            Text(level)
                        }
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Toggle Artifacts", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    EffectToggle("LCD Line Artifacts", settings.lcdLinesEnabled) {
                        viewModel.updateSettings(settings.copy(lcdLinesEnabled = it))
                    }
                    EffectToggle("Screen Flicker", settings.screenFlickerEnabled) {
                        viewModel.updateSettings(settings.copy(screenFlickerEnabled = it))
                    }
                    EffectToggle("Pixel Corruption Blocks", settings.pixelCorruptionEnabled) {
                        viewModel.updateSettings(settings.copy(pixelCorruptionEnabled = it))
                    }
                    EffectToggle("Color Shift & Tint Distortion", settings.colorDistortionEnabled) {
                        viewModel.updateSettings(settings.copy(colorDistortionEnabled = it))
                    }
                    EffectToggle("Fake GPU Error Dialogs", settings.fakeErrorDialogsEnabled) {
                        viewModel.updateSettings(settings.copy(fakeErrorDialogsEnabled = it))
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Auto Recovery Timeout: ${settings.autoRecoveryDurationMinutes} Minutes", fontWeight = FontWeight.Bold)
                    Slider(
                        value = settings.autoRecoveryDurationMinutes.toFloat(),
                        onValueChange = {
                            viewModel.updateSettings(settings.copy(autoRecoveryDurationMinutes = it.toInt()))
                        },
                        valueRange = 1f..30f,
                        steps = 29
                    )
                }
            }
        }
    }
}

@Composable
fun EffectToggle(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
