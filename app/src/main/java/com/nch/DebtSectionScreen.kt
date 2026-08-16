package com.nch

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Payments
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
fun DebtSectionScreen(
    onBack: () -> Unit,
    debtList: SnapshotStateList<Transaction>,
    debtGoal: String,
    currentPaid: String,
    progress: Float,
    onEditGoal: (Long) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showGoalEditDialog by remember { mutableStateOf(false) }
    
    var nameInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var installmentsCountInput by remember { mutableStateOf("") }
    
    var editingDebt by remember { mutableStateOf<Transaction?>(null) }
    var editNameInput by remember { mutableStateOf("") }
    var editAmountInput by remember { mutableStateOf("") }
    var editInstallmentsCountInput by remember { mutableStateOf("") }
    
    var goalInput by remember { mutableStateOf(debtGoal.replace(Regex("[^\\d]"), "")) }

    // Diálogo Editar Meta
    if (showGoalEditDialog) {
        AlertDialog(
            onDismissRequest = { showGoalEditDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Editar Meta de Deuda", color = Color.White, fontWeight = FontWeight.Bold) },
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
                    label = { Text("Meta Total") },
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
            title = { Text("Añadir Deuda", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nombre de la deuda") },
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
                        label = { Text("Monto de cuota") },
                        prefix = { Text("$", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = installmentsCountInput,
                        onValueChange = { installmentsCountInput = it.replace(Regex("[^\\d]"), "") },
                        label = { Text("Cantidad de cuotas") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (nameInput.isNotBlank() && amountInput.isNotBlank() && installmentsCountInput.isNotBlank()) {
                        val installmentValue = amountInput.replace(Regex("[^\\d]"), "").toLongOrNull() ?: 0L
                        val count = installmentsCountInput.toLongOrNull() ?: 1L
                        val totalValue = installmentValue * count
                        
                        val formatter = NumberFormat.getInstance(Locale("es", "CL"))
                        val formattedTotal = "$${formatter.format(totalValue)}"
                        
                        debtList.add(
                            Transaction(
                                category = nameInput,
                                amount = formattedTotal,
                                installmentAmount = "$$amountInput",
                                totalAmount = formattedTotal,
                                installmentsCount = installmentsCountInput
                            )
                        )
                        nameInput = ""; amountInput = ""; installmentsCountInput = ""; showAddDialog = false
                    }
                }) { Text("Añadir") }
            }
        )
    }

    // Diálogo Editar
    if (showEditDialog && editingDebt != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Editar Deuda", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = { editNameInput = it },
                        label = { Text("Nombre") },
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
                        label = { Text("Monto de cuota") },
                        prefix = { Text("$", color = Color.White) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editInstallmentsCountInput,
                        onValueChange = { editInstallmentsCountInput = it.replace(Regex("[^\\d]"), "") },
                        label = { Text("Cantidad de cuotas") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val index = debtList.indexOf(editingDebt)
                    if (index != -1) {
                        val installmentValue = editAmountInput.replace(Regex("[^\\d]"), "").toLongOrNull() ?: 0L
                        val count = editInstallmentsCountInput.toLongOrNull() ?: 1L
                        val totalValue = installmentValue * count
                        
                        val formatter = NumberFormat.getInstance(Locale("es", "CL"))
                        val formattedTotal = "$${formatter.format(totalValue)}"

                        debtList[index] = editingDebt!!.copy(
                            category = editNameInput,
                            amount = formattedTotal,
                            installmentAmount = "$$editAmountInput",
                            totalAmount = formattedTotal,
                            installmentsCount = editInstallmentsCountInput
                        )
                    }
                    showEditDialog = false
                }) { Text("Guardar") }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SECCION DE DEUDAS", fontWeight = FontWeight.Black, color = Color.White, fontSize = 18.sp, letterSpacing = 1.sp) },
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
                title = "deuda total",
                amount = debtGoal,
                savingsAmount = currentPaid,
                savingsLabel = "mensual",
                containerColor = Color(0xFF1E1E1E),
                progress = progress,
                onEditClick = {
                    goalInput = debtGoal.replace(Regex("[^\\d]"), "")
                    showGoalEditDialog = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            debtList.forEach { debt ->
                TransactionItem(
                    transaction = debt,
                    onDelete = { debtList.remove(debt) },
                    onEdit = {
                        editingDebt = debt
                        editNameInput = debt.category
                        editAmountInput = debt.amount.replace(Regex("[^\\d]"), "")
                        showEditDialog = true
                    },
                    onDoneChange = { isDone ->
                        val index = debtList.indexOf(debt)
                        if (index != -1) debtList[index] = debt.copy(isDone = isDone)
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
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Añadir Deuda", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}
