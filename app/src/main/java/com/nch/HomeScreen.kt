package com.nch

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nch.ui.theme.NchTheme
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val amount: String,
    var isDone: Boolean = false
)

fun formatCLP(amount: String): String {
    val cleanString = amount.replace(Regex("[^\\d]"), "")
    val parsed = cleanString.toLongOrNull() ?: 0L
    val formatter = NumberFormat.getInstance(Locale("es", "CL"))
    return "$${formatter.format(parsed)}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // Estado para la lista de transacciones
    val transactions = remember {
        mutableStateListOf<Transaction>()
    }

    // Estado para el diálogo
    var showDialog by remember { mutableStateOf(false) }
    var categoryInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { 
                Text(
                    "Nuevo Gasto", 
                    color = Color.White, 
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ) 
            },
            text = {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text("Nombre del gasto") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { input ->
                            // Formateo en tiempo real con puntos
                            val clean = input.replace(Regex("[^\\d]"), "")
                            if (clean.isEmpty()) {
                                amountInput = ""
                            } else {
                                val parsed = clean.toLongOrNull() ?: 0L
                                val formatter = NumberFormat.getInstance(Locale("es", "CL"))
                                amountInput = formatter.format(parsed)
                            }
                        },
                        label = { Text("Monto") },
                        prefix = { Text("$", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (categoryInput.isNotBlank() && amountInput.isNotBlank()) {
                            // Ya viene formateado con puntos desde el input
                            transactions.add(Transaction(category = categoryInput, amount = "$$amountInput"))
                            categoryInput = ""
                            amountInput = ""
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text("Añadir", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "NCH",
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 20.sp,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Open Drawer */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Profile */ }) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF222222))
                        ) {
                            // Placeholder for profile image
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        },
        bottomBar = {
            CustomBottomNavigation()
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
            // Card Principal
            SummaryCard(
                title = "meta 2026",
                amount = "$100.000",
                containerColor = Color(0xFF1E1E1E)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Card Ingresos (Verde)
            SummaryCard(
                title = "ingresos extras de sueldo",
                amount = "$0",
                containerColor = Color(0xFF1B5E20), // Verde oscuro
                isSmall = true
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Card Gastos Extras (Rojo)
            SummaryCard(
                title = "gastos extras",
                amount = "$0",
                containerColor = Color(0xFFB71C1C), // Rojo oscuro
                isSmall = true
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Renderizado dinámico de la lista
            transactions.forEach { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onDelete = { transactions.remove(transaction) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón para agregar nueva card
            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir Gasto", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botón para Resumen Finanzas
            Button(
                onClick = { 
                    /* TODO: Navegación a Resumen Finanzas */ 
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Default.BarChart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Resumen Finanzas", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    amount: String,
    containerColor: Color,
    isSmall: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (isSmall) 20.dp else 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = if (containerColor == Color(0xFF1E1E1E)) Color.Gray else Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(if (isSmall) 12.dp else 16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title.uppercase(),
                    color = if (containerColor == Color(0xFF1E1E1E)) Color.Gray else Color.White.copy(alpha = 0.7f),
                    fontSize = if (isSmall) 10.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(if (isSmall) 4.dp else 8.dp))
            
            Text(
                text = amount,
                color = Color.White,
                fontSize = if (isSmall) 28.sp else 42.sp,
                fontWeight = FontWeight.Bold
            )
            
            if (!isSmall) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(3.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF444444))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .fillMaxHeight()
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onDelete: () -> Unit) {
    var isDone by remember { mutableStateOf(transaction.isDone) }
    
    val backgroundColor by animateColorAsState(
        targetValue = if (isDone) Color(0xFF0A0A0A) else Color(0xFF1A1A1A),
        label = "backgroundColor"
    )
    
    val contentAlpha = if (isDone) 0.3f else 1.0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .alpha(contentAlpha),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.category,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = transaction.amount,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(4.dp))
                
                IconButton(
                    onClick = { 
                        isDone = !isDone
                        transaction.isDone = isDone
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDone) Color.White else Color(0xFF222222))
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completar",
                        tint = if (isDone) Color.Black else Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigation() {
    var selectedItem by remember { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 30.dp, start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xFF080808), RoundedCornerShape(100.dp))
                .padding(6.dp)
                .wrapContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BottomNavItem(
                icon = Icons.Default.MonetizationOn,
                label = "Dinero",
                isSelected = selectedItem == 0,
                onClick = { selectedItem = 0 }
            )
            BottomNavItem(
                icon = Icons.Default.FitnessCenter,
                label = "Gym",
                isSelected = selectedItem == 1,
                onClick = { selectedItem = 1 }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFF1E1E1E) else Color.Transparent
    val contentColor = if (isSelected) Color.White else Color(0xFF888888)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 30.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HomeScreenPreview() {
    NchTheme(darkTheme = true) {
        HomeScreen()
    }
}
