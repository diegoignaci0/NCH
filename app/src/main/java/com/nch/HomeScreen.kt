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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val isDone: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSavings: () -> Unit,
    savingsGoal: String,
    currentSavings: String,
    progress: Float,
    onEditGoal: (Long) -> Unit
) {
    val transactions = remember { mutableStateListOf<Transaction>() }

    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showGoalEditDialog by remember { mutableStateOf(false) }
    
    var categoryInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }

    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var editCategoryInput by remember { mutableStateOf("") }
    var editAmountInput by remember { mutableStateOf("") }
    
    var goalInput by remember { mutableStateOf(savingsGoal.replace(Regex("\\D"), "")) }

    if (showGoalEditDialog) {
        AlertDialog(
            onDismissRequest = { showGoalEditDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Editar Meta de Ahorro", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = goalInput,
                    onValueChange = { input ->
                        val clean = input.replace(Regex("\\D"), "")
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
                        val clean = goalInput.replace(Regex("\\D"), "")
                        val newGoal = clean.toLongOrNull() ?: 0L
                        onEditGoal(newGoal)
                        showGoalEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) { Text("Guardar", fontWeight = FontWeight.Bold) }
            }
        )
    }

    if (showEditDialog && editingTransaction != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Editar Gasto", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = editCategoryInput,
                        onValueChange = { editCategoryInput = it },
                        label = { Text("Nombre del gasto") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editAmountInput,
                        onValueChange = { input ->
                            val clean = input.replace(Regex("\\D"), "")
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
                Button(
                    onClick = {
                        val index = transactions.indexOf(editingTransaction)
                        if (index != -1) {
                            transactions[index] = editingTransaction!!.copy(category = editCategoryInput, amount = "$$editAmountInput")
                        }
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black)
                ) { Text("Guardar", fontWeight = FontWeight.Bold) }
            }
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Nuevo Gasto", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text("Nombre del gasto") },
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
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (categoryInput.isNotBlank() && amountInput.isNotBlank()) {
                            transactions.add(Transaction(category = categoryInput, amount = "$$amountInput"))
                            categoryInput = ""; amountInput = ""; showDialog = false
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
                title = { Text("NCH", fontWeight = FontWeight.Black, color = Color.White, fontSize = 20.sp, letterSpacing = 2.sp) },
                navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu, "Menu", tint = Color.White) } },
                actions = { IconButton(onClick = {}) { Box(Modifier.size(36.dp).clip(CircleShape).background(Color(0xFF222222))) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        bottomBar = { CustomBottomNavigation() },
        containerColor = Color.Black
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).background(Color.Black).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            SummaryCard(
                title = "meta 2026",
                amount = savingsGoal,
                savingsAmount = currentSavings,
                savingsLabel = "ahorro actual",
                containerColor = Color(0xFF1E1E1E),
                progress = progress,
                onEditClick = { 
                    goalInput = savingsGoal.replace(Regex("\\D"), "")
                    showGoalEditDialog = true 
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            transactions.forEach { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onDelete = { transactions.remove(transaction) },
                    onEdit = {
                        editingTransaction = transaction
                        editCategoryInput = transaction.category
                        editAmountInput = transaction.amount.replace(Regex("\\D"), "")
                        showEditDialog = true
                    },
                    onDoneChange = { isDone ->
                        val index = transactions.indexOf(transaction)
                        if (index != -1) transactions[index] = transaction.copy(isDone = isDone)
                    }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { showDialog = true }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E))) {
                Icon(Icons.Default.Add, null); Spacer(Modifier.width(8.dp)); Text("Añadir Gasto", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onNavigateToSavings, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E1E))) {
                Icon(Icons.Default.MonetizationOn, null); Spacer(Modifier.width(8.dp)); Text("Ahorro Mensual", fontWeight = FontWeight.Bold)
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
    isSmall: Boolean = false,
    savingsAmount: String? = null,
    savingsLabel: String = "Ahorrado",
    progress: Float = 0f,
    onEditClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Box {
            if (onEditClick != null) {
                IconButton(onClick = onEditClick, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    Icon(Icons.Default.Edit, "Editar", tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(16.dp))
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = if (isSmall) 20.dp else 32.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(title.uppercase(), color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
                Spacer(Modifier.height(8.dp))
                Text(amount, color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Bold)
                if (savingsAmount != null) {
                    Text("$savingsLabel: $savingsAmount", color = Color.White.copy(alpha = 0.6f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
                if (!isSmall) {
                    Spacer(Modifier.height(16.dp))
                    Box(Modifier.width(80.dp).height(3.dp).clip(CircleShape).background(Color(0xFF444444))) {
                        Box(Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).fillMaxHeight().background(Color.White))
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onDelete: () -> Unit, onEdit: () -> Unit, onDoneChange: (Boolean) -> Unit) {
    val isDone = transaction.isDone
    val backgroundColor by animateColorAsState(if (isDone) Color(0xFF0A0A0A) else Color(0xFF1A1A1A))
    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).alpha(if (isDone) 0.3f else 1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(backgroundColor)) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(transaction.category, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(transaction.amount, color = Color.White, fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit, Modifier.size(36.dp)) { Icon(Icons.Default.Edit, "Editar", tint = Color.Gray, modifier = Modifier.size(18.dp)) }
                IconButton(onClick = onDelete, Modifier.size(36.dp)) { Icon(Icons.Default.Close, "Eliminar", tint = Color.DarkGray, modifier = Modifier.size(18.dp)) }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = { onDoneChange(!isDone) }, Modifier.size(36.dp).clip(CircleShape).background(if (isDone) Color.White else Color(0xFF222222))) {
                    Icon(Icons.Default.Check, null, tint = if (isDone) Color.Black else Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigation() {
    var selectedItem by remember { mutableIntStateOf(0) }
    Box(Modifier.fillMaxWidth().padding(bottom = 30.dp, start = 20.dp, end = 20.dp), Alignment.Center) {
        Row(modifier = Modifier.background(Color(0xFF080808), RoundedCornerShape(100.dp)).padding(6.dp).wrapContentSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            BottomNavItem(Icons.Default.MonetizationOn, "Dinero", selectedItem == 0) { selectedItem = 0 }
            BottomNavItem(Icons.Default.FitnessCenter, "Gym", selectedItem == 1) { selectedItem = 1 }
        }
    }
}

@Composable
fun BottomNavItem(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val contentColor = if (isSelected) Color.White else Color(0xFF888888)
    Box(Modifier.clip(RoundedCornerShape(100.dp)).background(if (isSelected) Color(0xFF1E1E1E) else Color.Transparent).clickable { onClick() }.padding(horizontal = 30.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, label, tint = contentColor, modifier = Modifier.size(26.dp))
            Spacer(Modifier.height(2.dp))
            Text(label, color = contentColor, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HomeScreenPreview() {
    NchTheme(darkTheme = true) {
        HomeScreen({}, "$0", "$0", 0f, {})
    }
}
