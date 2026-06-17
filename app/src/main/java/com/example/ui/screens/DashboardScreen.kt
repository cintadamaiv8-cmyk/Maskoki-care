package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.MaskokiViewModel
import com.example.ui.theme.*

@Composable
fun DashboardScreen(viewModel: MaskokiViewModel, navController: androidx.navigation.NavController) {
    val userName by viewModel.userName.collectAsState()
    val dashboardImageUri by viewModel.dashboardImageUri.collectAsState()
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.updateDashboardImage(it.toString()) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        contentPadding = PaddingValues(16.dp, 8.dp)
    ) {
        item {
            // Header Area
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(32.dp).background(OrangePrimary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Pets, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Maskoki Care", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = WhiteText)
                }
                IconButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier.size(40.dp).background(WarningRed.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                ) {
                    Icon(Icons.Filled.Notifications, contentDescription = null, tint = WarningRed, modifier = Modifier.size(24.dp))
                }
            }

            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(32.dp))
                    .clickable { launcher.launch("image/*") }
                    .background(DarkSurface),
                contentAlignment = Alignment.Center
            ) {
                if (dashboardImageUri != null) {
                    AsyncImage(
                        model = dashboardImageUri,
                        contentDescription = "Dashboard Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.maskoki_hero_1781681229908),
                        contentDescription = "Default Hero",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                
                // Dim gradient overlay
                Box(modifier = Modifier.matchParentSize().background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.2f), Color.Black.copy(alpha = 0.8f))
                    )
                ))

                // Hero Content
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                ) {
                    Text(
                        text = "PROFIL PEMILIK", 
                        color = OrangePrimary, 
                        fontSize = 10.sp, 
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = userName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("STATUS TANK", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, letterSpacing = 1.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(8.dp).background(StatusGreen, RoundedCornerShape(4.dp)))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sangat Baik", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color.White.copy(alpha = 0.1f)))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            val water = viewModel.waterChanges.collectAsState().value
                            val nextWater = water.firstOrNull { !it.isCompleted }?.let { "Ganti Air ${it.percentage}%" } ?: "Air Stabil"
                            Text("TARGET BERIKUTNYA", color = Color.White.copy(alpha = 0.5f), fontSize = 10.sp, letterSpacing = 1.sp)
                            Text(nextWater, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val feeding by viewModel.feedingSchedules.collectAsState()
            val water by viewModel.waterChanges.collectAsState()
            val tank by viewModel.maintenances.collectAsState()

            val nextFeed = feeding.firstOrNull()
            val nextFeedTime = nextFeed?.time ?: "T/A"
            val nextFeedType = nextFeed?.foodType ?: "Belum ada jadwal"

            val nextWater = water.firstOrNull { !it.isCompleted }
            val nextWaterText = if (nextWater != null) "Tersedia" else "Aman"

            val nextTank = tank.firstOrNull { !it.isCompleted }

            // Feeding History Stats
            val history by viewModel.feedingHistory.collectAsState()
            
            val todayStart = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
            
            val feedsToday = history.count { it.timestamp >= todayStart }
            val lastFeed = history.firstOrNull()
            val lastFeedTime = lastFeed?.let {
                val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale("id", "ID"))
                format.format(java.util.Date(it.timestamp))
            } ?: "-"
            val lastFeedType = lastFeed?.foodType?.takeIf { it.isNotBlank() } ?: "Belum Ada Data"

            // High Density Grid
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Info Pakan Stats
                Row(
                    modifier = Modifier.fillMaxWidth().background(DarkSurface, RoundedCornerShape(16.dp)).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Terakhir Makan", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                        Text(lastFeedTime, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        Text(lastFeedType, fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(Color.White.copy(alpha = 0.1f)))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Total Hari Ini", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f))
                        Text("$feedsToday Kali", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(" ", fontSize = 10.sp) // Spacer alignment
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(
                        title = "Pakan Selanjutnya",
                        value = nextFeedTime,
                        subtitle = nextFeedType,
                        icon = Icons.Filled.Pets,
                        iconColor = OrangePrimary,
                        iconBgColor = OrangePrimary.copy(alpha = 0.1f),
                        statusText = if (nextFeed != null) "AKTIF" else null,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Ganti Air",
                        value = nextWaterText,
                        subtitle = "Sesuai Jadwal",
                        icon = Icons.Filled.WaterDrop,
                        iconColor = StatusBlue,
                        iconBgColor = StatusBlue.copy(alpha = 0.1f),
                        statusText = null,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                WideMetricCard(
                    title = "Kondisi Terakhir Tank",
                    value = if (nextTank != null) "Ada perawatan tertunda" else "Semua bersih (Selesai)",
                    subtitle = "Terjadwal rapi",
                    icon = Icons.Filled.Build,
                    iconColor = StatusGreen,
                    iconBgColor = StatusGreen.copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            // Quick Actions / Notifications Summary
            var showManualFeedDialog by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth().background(OrangePrimary, RoundedCornerShape(16.dp)).padding(10.dp).clickable { showManualFeedDialog = true },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                    Icon(Icons.Filled.Fastfood, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("PAKAN CEPAT DILUAR JADWAL", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.8f), letterSpacing = 1.sp)
                    Text("Ketuk untuk mencatat pemberian pakan sekarang.", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }

            if (showManualFeedDialog) {
                AlertDialog(
                    onDismissRequest = { showManualFeedDialog = false },
                    title = { Text("Konfirmasi") },
                    text = { Text("Apakah ikan sudah diberi pakan?") },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.addManualFeeding()
                            showManualFeedDialog = false
                        }, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
                            Text("Ya, Sudah")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showManualFeedDialog = false }) { Text("Belum", color = Color.White) }
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    statusText: String? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(140.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BorderWhite)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.background(iconBgColor, RoundedCornerShape(12.dp)).padding(8.dp)) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                }
                if (statusText != null) {
                    Text(
                        text = statusText,
                        color = iconColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.background(iconBgColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Column {
                Text(text = title, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
                Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                if (subtitle.isNotEmpty()) {
                    Text(text = subtitle, fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                }
            }
        }
    }
}

@Composable
fun WideMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBgColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, BorderWhite)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.background(iconBgColor, RoundedCornerShape(12.dp)).padding(10.dp)) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 12.sp, color = Color.White.copy(alpha = 0.5f))
                Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                if (subtitle.isNotEmpty()) {
                    Text(text = subtitle, fontSize = 10.sp, color = Color.White.copy(alpha = 0.3f))
                }
            }
            Button(
                onClick = { /* Handle click */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.05f), contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Detail", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

