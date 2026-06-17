package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FeedingHistory
import com.example.ui.MaskokiViewModel
import com.example.ui.theme.BorderWhite
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.OrangePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedingHistoryScreen(viewModel: MaskokiViewModel) {
    val histories by viewModel.feedingHistory.collectAsState()
    var showClearDialog by remember { mutableStateOf(false) }
    var filterToday by remember { mutableStateOf(false) }

    val filteredHistories = if (filterToday) {
        val todayStart = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis
        histories.filter { it.timestamp >= todayStart }
    } else {
        histories
    }

    Scaffold(
        floatingActionButton = {
            if (filteredHistories.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { showClearDialog = true },
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus Semua")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("Riwayat Pakan", style = MaterialTheme.typography.headlineMedium, color = OrangePrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !filterToday,
                        onClick = { filterToday = false },
                        label = { Text("Semua") }
                    )
                    FilterChip(
                        selected = filterToday,
                        onClick = { filterToday = true },
                        label = { Text("Hari Ini") }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (filteredHistories.isEmpty()) {
                item {
                    Text("Belum ada riwayat pakan.", modifier = Modifier.padding(16.dp))
                }
            } else {
                items(filteredHistories) { history ->
                    FeedingHistoryCard(history, viewModel)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        
        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("Hapus Semua Riwayat") },
                text = { Text("Apakah Anda yakin ingin menghapus semua riwayat pemberian pakan?") },
                confirmButton = {
                    Button(onClick = {
                        viewModel.clearFeedingHistory()
                        showClearDialog = false
                    }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                        Text("Hapus Semua")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}

@Composable
fun FeedingHistoryCard(history: FeedingHistory, viewModel: MaskokiViewModel) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale("id", "ID"))
    val dateObj = Date(history.timestamp)
    val dateStr = dateFormat.format(dateObj)
    val timeStr = timeFormat.format(dateObj)

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
                Text(history.scheduleName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OrangePrimary)
                if (history.foodType.isNotEmpty()) {
                    Text("Pakan: ${history.foodType}", fontSize = 14.sp)
                }
                Text("$dateStr pukul $timeStr", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text("Status: ${history.status}", fontSize = 12.sp, color = com.example.ui.theme.StatusGreen)
            }
            IconButton(onClick = { viewModel.deleteFeedingHistory(history) }) {
                Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
