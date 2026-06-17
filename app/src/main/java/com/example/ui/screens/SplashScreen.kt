package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.OrangePrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    var progress by remember { mutableStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 4000)
    )

    LaunchedEffect(Unit) {
        progress = 1f
        delay(4200) // Tunggu animasi selesai ditambah sedikit jeda
        navController.navigate("dashboard") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.maskoki_care_icon_1781681215185),
            contentDescription = "App Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(32.dp))
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Maskoki Care",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "${(animatedProgress * 100).toInt()}%",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = OrangePrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = OrangePrimary,
            trackColor = Color.White.copy(alpha = 0.1f)
        )
    }
}
