package com.nch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

data class PurchaseItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val amount: String,
    val quantity: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseCategoryDetailScreen(
    categoryName: String,
    onBack: () -> Unit
) {
    val items = remember { mutableStateListOf<PurchaseItem>() }
    var showDialog by remember { mutableStateOf(false) }

    var nameInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var quantityInput by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Añadir elemento", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { input ->
                            val clean = input.replace(Regex("\\D"), "")
                            if (clean.isNotEmpty()) {
                                val parsed = clean.toLongOrNull() ?: 0L
                                val formatter = NumberFormat.getInstance(Locale("es", "CL"))
                                amountInput = formatter.format(parsed)
                            } else amountInput = ""
                        },
                        label = { Text("Monto") },
                        prefix = { Text("$", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = quantityInput,
                        onValueChange = { quantityInput = it },
                        label = { Text("Cantidad (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nameInput.isNotBlank() && amountInput.isNotBlank()) {
                            items.add(
                                PurchaseItem(
                                    name = nameInput,
                                    amount = "$$amountInput",
                                    quantity = if (quantityInput.isNotBlank()) quantityInput else null
                                )
                            )
                            nameInput = ""; amountInput = ""; quantityInput = ""; showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) { Text("Añadir", fontWeight = FontWeight.Bold) }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(categoryName, fontWeight = FontWeight.Black, color = Color.White, fontSize = 20.sp, letterSpacing = 2.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items.forEach { item ->
                PurchaseListItem(item) { items.remove(item) }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E), contentColor = Color.White)
            ) {
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Añadir elemento", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PurchaseListItem(item: PurchaseItem, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(item.amount, color = Color.White.copy(alpha = 0.7f), fontSize = 16.sp)
                if (item.quantity != null) {
                    Text("Cantidad: ${item.quantity}", color = Color.White.copy(alpha = 0.5f), fontSize = 14.sp)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, "Eliminar", tint = Color.DarkGray)
            }
        }
    }
}
