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
    
    // Cálculo de ahorro actual
    val currentSavingsValue = remember {
        derivedStateOf {
            savingsList.filter { it.isDone }.sumOf { 
                it.amount.replace(Regex("\\D"), "").toLongOrNull() ?: 0L
            }
        }
    }

    val progress = remember {
        derivedStateOf {
            if (savingsGoal > 0) currentSavingsValue.value.toFloat() / savingsGoal else 0f
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

    when (currentScreen) {
        "home" -> HomeScreen(
            onNavigateToSavings = { currentScreen = "savings" },
            onNavigateToPurchases = { currentScreen = "purchases" },
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
