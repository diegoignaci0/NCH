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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import coil3.compose.AsyncImage
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySavingsScreen(
    onBack: () -> Unit,
    savingsList: SnapshotStateList<Transaction>,
    savingsGoal: String,
    currentSavings: String,
    progress: Float,
    onEditGoal: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showGoalEditDialog by remember { mutableStateOf(false) }
    
    var monthInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    
    var editingSaving by remember { mutableStateOf<Transaction?>(null) }
    var editMonthInput by remember { mutableStateOf("") }
    var editAmountInput by remember { mutableStateOf("") }
    
    var goalInput by remember { mutableStateOf(savingsGoal.replace(Regex("[^\\d]"), "")) }

    // Diálogo Editar Meta
    if (showGoalEditDialog) {
        AlertDialog(
            onDismissRequest = { showGoalEditDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Editar Meta de Ahorro", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = { input ->
                        val clean = input.replace(Regex("[^\\d]"), "")
                        if (clean.isNotEmpty()) {
                            val parsed = clean.toLongOrNull() ?: 0L
                            val formatter = NumberFormat.getInstance(Locale("es", "CL"))
                            goalInput = formatter.format(parsed)
                        } else goalInput = ""
                    },
                    label = { Text("Nueva Meta") },
                    prefix = { Text("$", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.DarkGray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clean = goalInput.replace(Regex("[^\\d]"), "")
                        val newGoal = clean.toLongOrNull() ?: 0L
                        onEditGoal(newGoal)
                        showGoalEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) { Text("Guardar", fontWeight = FontWeight.Bold) }
            }
        )
    }

    // Diálogo Añadir
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Añadir Ahorro", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = monthInput,
                        onValueChange = { monthInput = it },
                        label = { Text("Mes (ej: Enero)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = amountInput,
                        onValueChange = { input ->
                            val clean = input.replace(Regex("[^\\d]"), "")
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
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (monthInput.isNotBlank() && amountInput.isNotBlank()) {
                        savingsList.add(Transaction(category = monthInput, amount = "$$amountInput"))
                        monthInput = ""; amountInput = ""; showAddDialog = false
                    }
                }) { Text("Añadir") }
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
                Column {
                    OutlinedTextField(
                        value = editMonthInput,
                        onValueChange = { editMonthInput = it },
                        label = { Text("Mes") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editAmountInput,
                        onValueChange = { input ->
                            val clean = input.replace(Regex("[^\\d]"), "")
                            if (clean.isNotEmpty()) {
                                val parsed = clean.toLongOrNull() ?: 0L
                                val formatter = NumberFormat.getInstance(Locale("es", "CL"))
                                editAmountInput = formatter.format(parsed)
                            } else editAmountInput = ""
                        },
                        label = { Text("Monto") },
                        prefix = { Text("$", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val index = savingsList.indexOf(editingSaving)
                    if (index != -1) {
                        savingsList[index] = editingSaving!!.copy(category = editMonthInput, amount = "$$editAmountInput")
                    }
                    showEditDialog = false
                }) { Text("Guardar") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AHORRO MENSUAL", fontWeight = FontWeight.Black, color = Color.White, fontSize = 18.sp, letterSpacing = 1.sp) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White) } },
                actions = {
                    IconButton(onClick = {}) {
                        AsyncImage(
                            model = "https://avatars.githubusercontent.com/u/144415849?v=4",
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF222222))
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color.Black).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            SummaryCard(
                title = "meta de ahorro",
                amount = savingsGoal,
                savingsAmount = currentSavings,
                savingsLabel = "ahorro actual",
                containerColor = Color(0xFF1E1E1E),
                progress = progress,
                onEditClick = {
                    goalInput = savingsGoal.replace(Regex("[^\\d]"), "")
                    showGoalEditDialog = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            savingsList.forEach { saving ->
                TransactionItem(
                    transaction = saving,
                    onDelete = { savingsList.remove(saving) },
                    onEdit = {
                        editingSaving = saving
                        editMonthInput = saving.category
                        editAmountInput = saving.amount.replace(Regex("[^\\d]"), "")
                        showEditDialog = true
                    },
                    onDoneChange = { isDone ->
                        val index = savingsList.indexOf(saving)
                        if (index != -1) savingsList[index] = saving.copy(isDone = isDone)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E), contentColor = Color.White)
            ) {
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Añadir Ahorro", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
