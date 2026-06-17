package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.MaskokiViewModel
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.OrangePrimary

@Composable
fun SettingsScreen(viewModel: MaskokiViewModel) {
    val userName by viewModel.userName.collectAsState()
    var editName by remember { mutableStateOf(userName) }
    
    var showResetDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }
    
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        Text("Pengaturan", style = MaterialTheme.typography.headlineMedium, color = OrangePrimary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Ubah Nama Pengguna", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = editName,
                    onValueChange = { 
                        editName = it
                        viewModel.updateUserName(it)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Tentang Aplikasi", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = OrangePrimary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aplikasi pencatat jadwal perawatan ikan maskoki secara offline.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Versi 1.0", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                Text("100% Offline | Tidak memerlukan internet", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Semua Data")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { showExitDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Keluar Aplikasi", color = OrangePrimary)
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Konfirmasi Reset") },
            text = { Text("Apakah Anda yakin ingin menghapus semua jadwal dan riwayat perawatan? Data yang dihapus tidak dapat dikembalikan.") },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.resetAllData()
                        showResetDialog = false 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus Semua")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Batal", color = MaterialTheme.colorScheme.onSurface) }
            }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Konfirmasi Keluar") },
            text = { Text("Apakah Anda yakin ingin keluar aplikasi?") },
            confirmButton = {
                Button(
                    onClick = { 
                        showExitDialog = false 
                        (context as? Activity)?.finish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Keluar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) { Text("Batal", color = MaterialTheme.colorScheme.onSurface) }
            }
        )
    }
}
