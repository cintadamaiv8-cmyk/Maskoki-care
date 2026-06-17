package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.Maintenance
import com.example.ui.MaskokiViewModel
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.OrangePrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MaintenanceScreen(viewModel: MaskokiViewModel) {
    val maintenances by viewModel.maintenances.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.addMaintenance(System.currentTimeMillis()) },
                containerColor = OrangePrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Tambah Perawatan Tank")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text("Perawatan Tank", style = MaterialTheme.typography.headlineMedium, color = OrangePrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }
            if (maintenances.isEmpty()) {
                item {
                    Text("Belum ada catatan perawatan tank.", modifier = Modifier.padding(16.dp))
                }
            } else {
                items(maintenances) { main ->
                    MaintenanceCard(main, viewModel)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun MaintenanceCard(maintenance: Maintenance, viewModel: MaskokiViewModel) {
    var cFilter by remember { mutableStateOf(maintenance.isFilterClean) }
    var cGlass by remember { mutableStateOf(maintenance.isGlassClean) }
    var cSubstrate by remember { mutableStateOf(maintenance.isSubstrateClean) }
    var cFish by remember { mutableStateOf(maintenance.isFishOk) }

    fun updateData() {
        viewModel.updateMaintenanceStatus(maintenance, cFilter, cGlass, cSubstrate, cFish)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                val dateStr = dateFormat.format(Date(maintenance.scheduledTime))
                Text(dateStr, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OrangePrimary, modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.deleteMaintenance(maintenance) }) {
                    Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }

            CheckboxRow("Filter Dibersihkan", cFilter) { cFilter = it; updateData() }
            CheckboxRow("Kaca Dibersihkan", cGlass) { cGlass = it; updateData() }
            CheckboxRow("Dasar Tank Dibersihkan", cSubstrate) { cSubstrate = it; updateData() }
            CheckboxRow("Kondisi Ikan Baik", cFish) { cFish = it; updateData() }
            
            if (maintenance.isCompleted) {
                Text("Selesai!", color = OrangePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun CheckboxRow(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange, colors = CheckboxDefaults.colors(checkedColor = OrangePrimary))
        Text(label)
    }
}
