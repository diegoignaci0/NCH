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
import java.util.UUID

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val category: String,
    val amount: String,
    var isDone: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // Estado para la lista de transacciones
    val transactions = remember {
        mutableStateListOf(
            Transaction(category = "Alquiler", amount = "$45.000"),
            Transaction(category = "Supermercado", amount = "$12.500"),
            Transaction(category = "Servicios", amount = "$8.000"),
            Transaction(category = "Transporte", amount = "$5.200"),
            Transaction(category = "Gimnasio", amount = "$3.500")
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
            ExpenseCard(
                title = "meta 2026",
                amount = "$100.000"
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
                onClick = { 
                    // Por ahora agregamos un gasto genérico, luego puedes abrir un diálogo
                    transactions.add(Transaction(category = "Nuevo Gasto", amount = "$0"))
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
fun ExpenseCard(title: String, amount: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title.uppercase(),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = amount,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
            
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
