package com.nch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.nch.ui.theme.NchTheme
import java.text.NumberFormat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NchTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    var currentScreen by remember { mutableStateOf("home") }
    var selectedCategory by remember { mutableStateOf("") }
    
    // Estado compartido de ahorros
    var savingsGoal by remember { mutableLongStateOf(0L) }
    val savingsList = remember { mutableStateListOf<Transaction>() }

    // Estado compartido de deudas
    var debtGoal by remember { mutableLongStateOf(0L) }
    val debtList = remember { mutableStateListOf<Transaction>() }
    
    // Cálculo de ahorro actual
    val currentSavingsValue = remember {
        derivedStateOf {
            savingsList.filter { it.isDone }.sumOf { 
                it.amount.replace(Regex("\\D"), "").toLongOrNull() ?: 0L
            }
        }
    }

    // Cálculo de pagado actual (deudas)
    val currentPaidValue = remember {
        derivedStateOf {
            debtList.filter { it.isDone }.sumOf {
                it.totalAmount?.replace(Regex("\\D"), "")?.toLongOrNull() ?: 0L
            }
        }
    }

    // Cálculo de cuota mensual total (solo de deudas NO pagadas)
    val totalMonthlyInstallments = remember {
        derivedStateOf {
            debtList.filter { !it.isDone }.sumOf {
                it.installmentAmount?.replace(Regex("\\D"), "")?.toLongOrNull() ?: 0L
            }
        }
    }

    // Cálculo de Deuda Total Pendiente (suma de los totales de deudas NO pagadas)
    val totalPendingDebt = remember {
        derivedStateOf {
            debtList.filter { !it.isDone }.sumOf {
                it.totalAmount?.replace(Regex("\\D"), "")?.toLongOrNull() ?: 0L
            }
        }
    }

    val progress = remember {
        derivedStateOf {
            if (savingsGoal > 0) currentSavingsValue.value.toFloat() / savingsGoal else 0f
        }
    }

    val debtProgress = remember {
        derivedStateOf {
            val total = debtList.sumOf { it.totalAmount?.replace(Regex("\\D"), "")?.toLongOrNull() ?: 0L }
            if (total > 0) currentPaidValue.value.toFloat() / total else 0f
        }
    }

    val formattedGoal = remember(savingsGoal) {
        val formatter = NumberFormat.getInstance(Locale("es", "CL"))
        "$${formatter.format(savingsGoal)}"
    }

    val formattedCurrent = remember(currentSavingsValue.value) {
        val formatter = NumberFormat.getInstance(Locale("es", "CL"))
        "$${formatter.format(currentSavingsValue.value)}"
    }

    val formattedDebtTotal = remember(totalPendingDebt.value) {
        val formatter = NumberFormat.getInstance(Locale("es", "CL"))
        "$${formatter.format(totalPendingDebt.value)}"
    }

    val formattedMonthlyDebt = remember(totalMonthlyInstallments.value) {
        val formatter = NumberFormat.getInstance(Locale("es", "CL"))
        "$${formatter.format(totalMonthlyInstallments.value)}"
    }

    when (currentScreen) {
        "home" -> HomeScreen(
            onNavigateToSavings = { currentScreen = "savings" },
            onNavigateToPurchases = { currentScreen = "purchases" },
            onNavigateToDebts = { currentScreen = "debts" },
            savingsGoal = formattedGoal,
            currentSavings = formattedCurrent,
            progress = progress.value,
            onEditGoal = { newGoal: Long -> savingsGoal = newGoal }
        )
        "savings" -> MonthlySavingsScreen(
            onBack = { currentScreen = "home" },
            savingsList = savingsList,
            savingsGoal = formattedGoal,
            currentSavings = formattedCurrent,
            progress = progress.value,
            onEditGoal = { newGoal: Long -> savingsGoal = newGoal }
        )
        "debts" -> DebtSectionScreen(
            onBack = { currentScreen = "home" },
            debtList = debtList,
            debtGoal = formattedDebtTotal,
            currentPaid = formattedMonthlyDebt,
            progress = debtProgress.value,
            onEditGoal = { /* Deshabilitado por ahora ya que es automático */ }
        )
        "purchases" -> PurchasesScreen(
            onBack = { currentScreen = "home" },
            onNavigateToCategory = { category ->
                selectedCategory = category
                currentScreen = "purchase_detail"
            }
        )
        "purchase_detail" -> PurchaseCategoryDetailScreen(
            categoryName = selectedCategory,
            onBack = { currentScreen = "purchases" }
        )
    }
}
