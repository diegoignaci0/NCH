package com.nch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySavingsScreen(onBack: () -> Unit) {
    // Estado para la lista de ahorros (reutilizamos Transaction por ahora o creamos uno igual)
    val savingsList = remember { mutableStateListOf<Transaction>() }

    // Estados para diálogos
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingSaving by remember { mutableStateOf<Transaction?>(null) }
    
    var monthInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    
    var editMonthInput by remember { mutableStateOf("") }
    var editAmountInput by remember { mutableStateOf("") }

    // Diálogo Añadir
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Añadir Ahorro", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = monthInput,
                        onValueChange = { monthInput = it },
                        label = { Text("Mes (ej: Enero)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { input ->
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
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (monthInput.isNotBlank() && amountInput.isNotBlank()) {
                            savingsList.add(Transaction(category = monthInput, amount = "$$amountInput"))
                            monthInput = ""
                            amountInput = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text("Añadir", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    // Diálogo Editar
    if (showEditDialog && editingSaving != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Editar Ahorro", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedTextField(
                        value = editMonthInput,
                        onValueChange = { editMonthInput = it },
                        label = { Text("Mes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editAmountInput,
                        onValueChange = { input ->
                            val clean = input.replace(Regex("[^\\d]"), "")
                            if (clean.isEmpty()) {
                                editAmountInput = ""
                            } else {
                                val parsed = clean.toLongOrNull() ?: 0L
                                val formatter = NumberFormat.getInstance(Locale("es", "CL"))
                                editAmountInput = formatter.format(parsed)
                            }
                        },
                        label = { Text("Monto") },
                        prefix = { Text("$", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.DarkGray,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.Gray
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editMonthInput.isNotBlank() && editAmountInput.isNotBlank()) {
                            val index = savingsList.indexOf(editingSaving)
                            if (index != -1) {
                                savingsList[index] = editingSaving!!.copy(
                                    category = editMonthInput,
                                    amount = "$$editAmountInput"
                                )
                            }
                            showEditDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text("Guardar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
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
                        text = "AHORRO MENSUAL",
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
            SummaryCard(
                title = "Total Ahorrado",
                amount = "$60.000",
                containerColor = Color(0xFF1E1E1E)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Lista de ahorros
            savingsList.forEach { saving ->
                TransactionItem(
                    transaction = saving,
                    onDelete = { savingsList.remove(saving) },
                    onEdit = {
                        editingSaving = saving
                        editMonthInput = saving.category
                        editAmountInput = saving.amount.replace(Regex("[^\\d]"), "")
                        if (editAmountInput.isNotEmpty()) {
                            val parsed = editAmountInput.toLongOrNull() ?: 0L
                            val formatter = NumberFormat.getInstance(Locale("es", "CL"))
                            editAmountInput = formatter.format(parsed)
                        }
                        showEditDialog = true
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Añadir Ahorro
            Button(
                onClick = { showAddDialog = true },
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
                Text("Añadir Ahorro", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
