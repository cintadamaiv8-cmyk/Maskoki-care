package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.FeedingSchedule
import com.example.ui.MaskokiViewModel
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.OrangePrimary

@Composable
fun FeedingScreen(viewModel: MaskokiViewModel, navController: androidx.navigation.NavController) {
    val schedules by viewModel.feedingSchedules.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = OrangePrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Pakan")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Jadwal Pakan", style = MaterialTheme.typography.headlineMedium, color = OrangePrimary, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { navController.navigate("feeding_history") }) {
                        Icon(androidx.compose.material.icons.Icons.Filled.History, contentDescription = "Riwayat", tint = OrangePrimary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (schedules.isEmpty()) {
                item {
                    Text("Belum ada jadwal pakan.", modifier = Modifier.padding(16.dp))
                }
            } else {
                items(schedules) { schedule ->
                    FeedingCard(schedule, viewModel)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        if (showAddDialog) {
            AddFeedingDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { time, freq, type ->
                    viewModel.addFeedingSchedule(time, freq, type)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun FeedingCard(schedule: FeedingSchedule, viewModel: MaskokiViewModel) {
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
                Text(schedule.time, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = OrangePrimary)
                Text("Frekuensi: ${schedule.frequency}")
                Text("Pakan: ${schedule.foodType}")
            }
            IconButton(onClick = { viewModel.markFed(schedule.id, scheduleName = "Jadwal ${schedule.time}", foodType = schedule.foodType) }) {
                Icon(Icons.Filled.Check, contentDescription = "Tandai Diberi Makan", tint = OrangePrimary)
            }
            IconButton(onClick = { viewModel.deleteFeedingSchedule(schedule) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddFeedingDialog(onDismiss: () -> Unit, onAdd: (String, String, String) -> Unit) {
    var time by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("Harian") }
    var foodType by remember { mutableStateOf("") }

    val frequencies = listOf("Sekali", "Harian", "Mingguan")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Jadwal Pakan") },
        text = {
            Column {
                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    label = { Text("Waktu (Contoh: 08:00)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = foodType,
                    onValueChange = { foodType = it },
                    label = { Text("Jenis Pakan") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Frekuensi")
                Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                    frequencies.forEach { freq ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = (frequency == freq),
                                onClick = { frequency = freq }
                            )
                            Text(freq, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(time, frequency, foodType) }, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = MaterialTheme.colorScheme.onSurface) }
        }
    )
}
