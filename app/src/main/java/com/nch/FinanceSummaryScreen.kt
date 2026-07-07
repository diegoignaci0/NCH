package com.nch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceSummaryScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "RESUMEN FINANZAS",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 18.sp,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Black)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Card Ingresos (Verde) - Movida desde Home
            SummaryCard(
                title = "ingresos extras de sueldo",
                amount = "$0",
                containerColor = Color(0xFF1B5E20),
                isSmall = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Card Gastos Extras (Rojo) - Movida desde Home
            SummaryCard(
                title = "gastos extras",
                amount = "$0",
                containerColor = Color(0xFFB71C1C),
                isSmall = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Aquí podrías agregar más detalles estadísticos en el futuro
            Text(
                text = "Detalles de movimientos próximamente...",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
