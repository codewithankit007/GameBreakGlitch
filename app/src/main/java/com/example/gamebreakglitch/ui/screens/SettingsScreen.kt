package com.example.gamebreakglitch.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gamebreakglitch.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val settings by viewModel.settings.collectAsState()
    var newPin by remember { mutableStateOf("") }
    var pinUpdatedMessage by remember { mutableStateOf(false) }
    var monitoredApps by remember { mutableStateOf(settings.monitoredPackages) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parent Security Settings") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Change Parent PIN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { newPin = it },
                        label = { Text("New 4-Digit Security PIN") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (newPin.length >= 4) {
                            viewModel.updatePin(newPin)
                            pinUpdatedMessage = true
                            newPin = ""
                        }
                    }) {
                        Text("Update PIN")
                    }
                    if (pinUpdatedMessage) {
                        Text("Security PIN successfully updated!", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
                    }
                }
            }

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Monitored Game Package Names", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Comma-separated list of target app package names:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = monitoredApps,
                        onValueChange = {
                            monitoredApps = it
                            viewModel.updateSettings(settings.copy(monitoredPackages = it))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false
                    )
                }
            }
        }
    }
}
