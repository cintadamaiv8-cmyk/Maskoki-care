package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.WaterChange
import com.example.ui.MaskokiViewModel
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.OrangePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WaterChangeScreen(viewModel: MaskokiViewModel) {
    val waterChanges by viewModel.waterChanges.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = OrangePrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Ganti Air")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("Pergantian Air", style = MaterialTheme.typography.headlineMedium, color = OrangePrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (waterChanges.isEmpty()) {
                item {
                    Text("Belum ada jadwal pergantian air.", modifier = Modifier.padding(16.dp))
                }
            } else {
                items(waterChanges) { change ->
                    WaterChangeCard(change, viewModel)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (showAddDialog) {
            AddWaterChangeDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { percentage, notes ->
                    val time = System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7 // default to 7 days later
                    viewModel.addWaterChange(time, percentage, notes)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun WaterChangeCard(change: WaterChange, viewModel: MaskokiViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                val dateStr = dateFormat.format(Date(change.scheduledTime))
                Text("Jadwal: $dateStr", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OrangePrimary)
                Text("Ganti Air: ${change.percentage}%")
                Text("Catatan: ${change.notes}")
                if (change.isCompleted) {
                    Text("Selesai", color = MaterialTheme.colorScheme.primary)
                }
            }
            if (!change.isCompleted) {
                IconButton(onClick = { viewModel.updateWaterChangeStatus(change, true) }) {
                    Icon(Icons.Filled.Check, contentDescription = "Tandai Selesai", tint = OrangePrimary)
                }
            }
            IconButton(onClick = { viewModel.deleteWaterChange(change) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddWaterChangeDialog(onDismiss: () -> Unit, onAdd: (Int, String) -> Unit) {
    var percentageStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Jadwal Ganti Air") },
        text = {
            Column {
                OutlinedTextField(
                    value = percentageStr,
                    onValueChange = { percentageStr = it },
                    label = { Text("Persentase Air (%)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Kondisi Air") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                val percent = percentageStr.toIntOrNull() ?: 30
                onAdd(percent, notes) 
            }, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = MaterialTheme.colorScheme.onSurface) }
        }
    )
}
