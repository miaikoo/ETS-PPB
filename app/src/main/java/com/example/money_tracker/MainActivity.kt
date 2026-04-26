package com.example.money_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.material.icons.filled.Home
import com.example.money_tracker.ui.theme.Money_TrackerTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Search

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Money_TrackerTheme {
                MoneyTrackerApp()
            }
        }
    }
}

// Daftar kategori untuk setiap jenis transaksi
val incomeCategories = listOf(
    "Gaji", "Bonus", "Investasi", "Hibah", "Penjualan", "Lainnya"
)

val expenseCategories = listOf(
    "Makanan", "Transportasi", "Belanja", "Hiburan", "Pendidikan",
    "Kesehatan", "Tagihan", "Rumah", "Lainnya"
)

// Palet Warna UI
val BgColor = Color(0xFF0F172A)
val CardSurface = Color(0xFF1E293B)
val TextMuted = Color(0xFF94A3B8)
val IncomeGreen = Color(0xFF10B981)
val ExpenseRed = Color(0xFFF43F5E)



// Modifikasi model Transaction dengan menambahkan kategori
data class Transaction(
    val name: String,
    val type: String,
    val amount: String,
    val date: String,
    val category: String // Menambahkan field kategori
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyTrackerHomeScreen(transactions: MutableList<Transaction>) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTransactionType by remember { mutableStateOf("Pengeluaran") }
    var filterType by remember { mutableStateOf("All") }
    var filterMonth by remember { mutableStateOf("Semua Bulan") }

    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val availableMonths = remember(transactions) {
        listOf("Semua Bulan") + transactions.mapNotNull {
            val parts = it.date.split(" ")
            if (parts.size >= 3) "${parts[1]} ${parts[2]}" else null
        }.distinct()
    }

    var selectedTransactionIndex by remember { mutableStateOf(-1) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

    var searchQuery by remember { mutableStateOf("") }

    val displayedTransactions = remember(transactions, filterType, filterMonth, searchQuery) {
        transactions.filter { transaction ->

            val matchType =
                if (filterType == "All") true
                else transaction.type == filterType

            val parts = transaction.date.split(" ")
            val monthYear =
                if (parts.size >= 3) "${parts[1]} ${parts[2]}"
                else ""

            val matchMonth =
                if (filterMonth == "Semua Bulan") true
                else monthYear == filterMonth

            // SEARCH
            val query = searchQuery.trim().lowercase()

            val matchSearch =
                query.isEmpty() ||
                        transaction.name.lowercase().contains(query) ||
                        transaction.category.lowercase().contains(query) ||
                        transaction.date.lowercase().contains(query)

            matchType && matchMonth && matchSearch

        }.sortedByDescending {
            try {
                dateFormat.parse(it.date)
            } catch (e: Exception) {
                Date(0)
            }
        }
    }

    val totalIncome = transactions.filter { it.type == "Pemasukan" }
        .sumOf { it.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 }
    val totalExpense = transactions.filter { it.type == "Pengeluaran" }
        .sumOf { it.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 }
    val balance = totalIncome - totalExpense

    if (showAddDialog) {
        AddTransactionDialog(
            transactionType = selectedTransactionType,
            onTypeChange = { selectedTransactionType = it },
            onAddTransaction = { name: String, amount: String, category: String, date: String ->
                val cleanAmount = amount.replace(Regex("[^0-9]"), "")
                val formattedAmount = cleanAmount.toIntOrNull()?.format() ?: "0"

                transactions.add(
                    Transaction(
                        name = name,
                        type = selectedTransactionType,
                        amount = formattedAmount,
                        date = date,
                        category = category
                    )
                )
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    if (showEditDialog && editingTransaction != null) {
        EditTransactionDialog(
            transaction = editingTransaction!!,
            onUpdateTransaction = { name: String, type: String, amount: String, category: String, date: String ->
                val cleanAmount = amount.replace(Regex("[^0-9]"), "")
                val formattedAmount = cleanAmount.toIntOrNull()?.format() ?: "0"

                val updated = Transaction(
                    name = name,
                    type = type,
                    amount = formattedAmount,
                    date = date,
                    category = category
                )

                if (selectedTransactionIndex in transactions.indices) {
                    transactions[selectedTransactionIndex] = updated
                }
                showEditDialog = false
                editingTransaction = null
            },
            onDeleteTransaction = {
                if (selectedTransactionIndex in transactions.indices) {
                    transactions.removeAt(selectedTransactionIndex)
                }
                showEditDialog = false
                editingTransaction = null
            },
            onDismiss = {
                showEditDialog = false
                editingTransaction = null
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF6366F1),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Transaksi", fontWeight = FontWeight.Bold) }
            )
        },
        containerColor = BgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Halo, Hikmia!", color = TextMuted, fontSize = 14.sp)
                    Text("Kelola uangmu hari ini", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                // Filter Bulan
                var expandedMonth by remember { mutableStateOf(false) }
                Box {
                    Surface(
                        onClick = { expandedMonth = true },
                        color = CardSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(filterMonth, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    DropdownMenu(expanded = expandedMonth, onDismissRequest = { expandedMonth = false }) {
                        availableMonths.forEach { month ->
                            DropdownMenuItem(text = { Text(month) }, onClick = { filterMonth = month; expandedMonth = false })
                        }
                    }
                }
            }

            // Wallet Card
            var isBalanceVisible by remember { mutableStateOf(true) }

            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                colors = CardDefaults.cardColors(containerColor = CardSurface),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    //Text("Total Saldo", color = TextMuted, fontSize = 14.sp)
                    //Text("Rp${balance.format()}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Saldo", color = TextMuted, fontSize = 14.sp)

                        Icon(
                            imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { isBalanceVisible = !isBalanceVisible }
                        )
                    }

                    Text(
                        text = if (isBalanceVisible) "Rp${balance.format()}" else "Rp ******",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoColumn("Pemasukan", "Rp${totalIncome.format()}", IncomeGreen, Icons.Default.KeyboardArrowDown, Modifier.weight(1f))
                        InfoColumn("Pengeluaran", "Rp${totalExpense.format()}", ExpenseRed, Icons.Default.KeyboardArrowUp, Modifier.weight(1f), Alignment.End)
                    }
                }
            }

            // Chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Pemasukan", "Pengeluaran").forEach { type ->
                    FilterChip(
                        selected = filterType == type,
                        onClick = { filterType = type },
                        label = { Text(type, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary.copy(0.2f), selectedLabelColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari nama, tanggal, atau kategori...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = TextMuted,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
                }
            )
            // Transaction List
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(displayedTransactions.size) { index ->
                    val globalIndex = transactions.indexOf(displayedTransactions[index])
                    TransactionItem(displayedTransactions[index]) {
                        selectedTransactionIndex = globalIndex
                        editingTransaction = transactions[globalIndex]
                        showEditDialog = true
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, amount: String, color: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, align: Alignment.Horizontal = Alignment.Start) {
    Column(modifier = modifier, horizontalAlignment = align) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (align == Alignment.Start) Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            Text(label, color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp))
            if (align == Alignment.End) Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
        }
        Text(amount, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit,
    initialDate: Date
) {
    val calendar = Calendar.getInstance()
    calendar.time = initialDate
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )

    // Tema Kalender
    MaterialTheme(
        colorScheme = darkColorScheme(
            surface = CardSurface,
            onSurface = Color.White,
            primary = Color(0xFF6366F1),
            onPrimary = Color.White
        )
    ) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onDateSelected(Date(it))
                    }
                }) {
                    Text("Pilih", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Batal", color = TextMuted)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = CardSurface
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = CardSurface,
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White,
                    weekdayContentColor = TextMuted,
                    subheadContentColor = TextMuted,
                    yearContentColor = Color.White,
                    currentYearContentColor = Color(0xFF6366F1),
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = Color(0xFF6366F1),
                    dayContentColor = Color.White,
                    selectedDayContainerColor = Color(0xFF6366F1),
                    selectedDayContentColor = Color.White,
                    todayContentColor = Color(0xFF6366F1),
                    todayDateBorderColor = Color(0xFF6366F1)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    transactionType: String,
    onTypeChange: (String) -> Unit,
    onAddTransaction: (name: String, amount: String, category: String, date: String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    val accentColor = if (transactionType == "Pemasukan") IncomeGreen else ExpenseRed

    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    val categories = if (transactionType == "Pemasukan") incomeCategories else expenseCategories
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    LaunchedEffect(transactionType) {
        val newCategories = if (transactionType == "Pemasukan") incomeCategories else expenseCategories
        selectedCategory = newCategories.first()
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { selectedDate = it; showDatePicker = false },
            initialDate = selectedDate
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.95f),
        content = {
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Tambah Transaksi",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Switcher Tipe Transaksi
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgColor, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        listOf("Pemasukan", "Pengeluaran").forEach { type ->
                            val isSelected = transactionType == type
                            val chipColor = if (type == "Pemasukan") IncomeGreen else ExpenseRed

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) chipColor.copy(alpha = 0.2f) else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { onTypeChange(type) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if (isSelected) chipColor else TextMuted,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Transaksi") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Jumlah (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        prefix = { Text("Rp ", color = TextMuted) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date & Category Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Date Picker Field
                        OutlinedTextField(
                            value = dateFormat.format(selectedDate),
                            onValueChange = {},
                            label = { Text("Tanggal") },
                            readOnly = true,
                            modifier = Modifier.weight(1f).clickable { showDatePicker = true },
                            trailingIcon = {
                                Icon(Icons.Default.DateRange, null, tint = TextMuted, modifier = Modifier.clickable { showDatePicker = true })
                            },
                            colors = OutlinedTextFieldDefaults.colors(unfocusedTextColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Dropdown yang lebih rapi
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                focusedLabelColor = accentColor,
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(CardSurface)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category, color = Color.White) },
                                    onClick = { selectedCategory = category; expanded = false },
                                    colors = MenuDefaults.itemColors(
                                        textColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal", color = TextMuted)
                        }

                        Button(
                            onClick = {
                                if (name.isNotBlank() && amount.isNotBlank()) {
                                    onAddTransaction(name, amount, selectedCategory, dateFormat.format(selectedDate))
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(12.dp),
                            enabled = name.isNotBlank() && amount.isNotBlank()
                        ) {
                            Text("Simpan Transaksi", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onUpdateTransaction: (name: String, type: String, amount: String, category: String, date: String) -> Unit,
    onDeleteTransaction: () -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(transaction.name) }
    var type by remember { mutableStateOf(transaction.type) }
    var amount by remember { mutableStateOf(transaction.amount) }

    // State untuk tanggal
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    var selectedDate by remember {
        mutableStateOf(
            try {
                dateFormat.parse(transaction.date) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        )
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteWarning by remember { mutableStateOf(false) }

    // Menentukan daftar kategori berdasarkan jenis transaksi
    val categories = if (type == "Pemasukan") incomeCategories else expenseCategories
    var selectedCategory by remember { mutableStateOf(transaction.category) }

    val accentColor = if (type == "Pemasukan") IncomeGreen else ExpenseRed

    // Update kategori saat jenis transaksi berubah
    LaunchedEffect(type) {
        val newCategories = if (type == "Pemasukan") incomeCategories else expenseCategories
        // Jika kategori sebelumnya tidak ada dalam kategori baru, pilih kategori pertama
        if (!newCategories.contains(selectedCategory)) {
            selectedCategory = newCategories.first()
        }
    }

    // Dialog pemilih tanggal
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = {
                selectedDate = it
                showDatePicker = false
            },
            initialDate = selectedDate
        )
    }

    // Warning delete
    if (showDeleteWarning) {
        AlertDialog(
            onDismissRequest = { showDeleteWarning = false },
            title = { Text("Hapus Transaksi", color = Color.White) },
            text = { Text("Apakah Anda yakin ingin menghapus transaksi ini?", color = Color.White) },
            containerColor = CardSurface,
            confirmButton = {
                TextButton(onClick = {
                    onDeleteTransaction()
                    showDeleteWarning = false
                }) {
                    Text("Hapus", color = ExpenseRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteWarning = false }) {
                    Text("Batal", color = TextMuted)
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier.fillMaxWidth(0.95f),
        content = {
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Edit Transaksi",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Switcher Tipe Transaksi
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BgColor, RoundedCornerShape(12.dp))
                            .padding(4.dp)
                    ) {
                        listOf("Pemasukan", "Pengeluaran").forEach { typeOption ->
                            val isSelected = type == typeOption
                            val chipColor = if (typeOption == "Pemasukan") IncomeGreen else ExpenseRed

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) chipColor.copy(alpha = 0.2f) else Color.Transparent,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { type = typeOption }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = typeOption,
                                    color = if (isSelected) chipColor else TextMuted,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Input Fields
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama Transaksi") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Jumlah (Rp)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = accentColor,
                            focusedLabelColor = accentColor,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        prefix = { Text("Rp ", color = TextMuted) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Date
                    OutlinedTextField(
                        value = dateFormat.format(selectedDate),
                        onValueChange = {},
                        label = { Text("Tanggal") },
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                        trailingIcon = {
                            Icon(Icons.Default.DateRange, null, tint = TextMuted, modifier = Modifier.clickable { showDatePicker = true })
                        },
                        colors = OutlinedTextFieldDefaults.colors(unfocusedTextColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Kategori") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = accentColor,
                                focusedLabelColor = accentColor,
                                unfocusedTextColor = Color.White,
                                focusedTextColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(CardSurface)
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category, color = Color.White) },
                                    onClick = { selectedCategory = category; expanded = false },
                                    colors = MenuDefaults.itemColors(
                                        textColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Delete Button (kecil di kiri)
                        IconButton(
                            onClick = { showDeleteWarning = true },
                            modifier = Modifier.background(ExpenseRed.copy(0.1f), RoundedCornerShape(12.dp))
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ExpenseRed)
                        }

                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Batal", color = TextMuted)
                        }

                        Button(
                            onClick = {
                                if (name.isNotBlank() && amount.isNotBlank()) {
                                    onUpdateTransaction(name, type, amount, selectedCategory, dateFormat.format(selectedDate))
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(12.dp),
                            enabled = name.isNotBlank() && amount.isNotBlank()
                        ) {
                            Text("Update", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    val isIncome = transaction.type == "Pemasukan"
    val iconColor = if (isIncome) IncomeGreen else ExpenseRed
    val bgColor = if (isIncome) IncomeGreen.copy(alpha = 0.15f) else ExpenseRed.copy(alpha = 0.15f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(bgColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncome) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(transaction.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.category,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(" • ", color = TextMuted, fontSize = 12.sp)
                    Text(transaction.date, color = TextMuted, fontSize = 12.sp)
                }
            }
        }

        Text(
            text = if (isIncome) "+Rp${transaction.amount}" else "-Rp${transaction.amount}",
            color = if (isIncome) IncomeGreen else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

fun Int.format(): String = "%,d".format(this)

@Preview(showBackground = true)
@Composable
fun MoneyTrackerApp() {
    // Data simulasi 4 bulan
    val transactions = remember {
        mutableStateListOf(

            // Januari
            Transaction("Gaji Januari", "Pemasukan", "8.000.000", "01 Januari 2026", "Gaji"),
            Transaction("Kebutuhan Kost", "Pengeluaran", "1.500.000", "02 Januari 2026", "Rumah"),
            Transaction("Makan & Harian", "Pengeluaran", "1.200.000", "10 Januari 2026", "Makanan"),
            Transaction("Transport", "Pengeluaran", "500.000", "15 Januari 2026", "Transportasi"),
            Transaction("Nabung", "Pengeluaran", "2.000.000", "25 Januari 2026", "Investasi"),

            // Februari
            Transaction("Gaji Februari", "Pemasukan", "8.000.000", "01 Februari 2026", "Gaji"),
            Transaction("Bonus Freelance", "Pemasukan", "1.500.000", "12 Februari 2026", "Bonus"),
            Transaction("Kebutuhan Kost", "Pengeluaran", "1.500.000", "02 Februari 2026", "Rumah"),
            Transaction("Makan & Harian", "Pengeluaran", "1.000.000", "18 Februari 2026", "Makanan"),
            Transaction("Transport", "Pengeluaran", "400.000", "20 Februari 2026", "Transportasi"),

            // Maret
            Transaction("Gaji Maret", "Pemasukan", "8.000.000", "01 Maret 2026", "Gaji"),
            Transaction("Beli HP Baru", "Pengeluaran", "6.000.000", "05 Maret 2026", "Belanja"),
            Transaction("Service Laptop", "Pengeluaran", "1.200.000", "10 Maret 2026", "Lainnya"),
            Transaction("Makan & Harian", "Pengeluaran", "1.200.000", "18 Maret 2026", "Makanan"),
            Transaction("Hangout", "Pengeluaran", "800.000", "25 Maret 2026", "Hiburan"),

            // April
            Transaction("Gaji April", "Pemasukan", "8.500.000", "01 April 2026", "Gaji"),
            Transaction("Side Hustle", "Pemasukan", "2.000.000", "07 April 2026", "Bonus"),
            Transaction("Kebutuhan Kost", "Pengeluaran", "1.500.000", "02 April 2026", "Rumah"),
            Transaction("Makan & Harian", "Pengeluaran", "1.200.000", "15 April 2026", "Makanan"),
            Transaction("Tagihan", "Pengeluaran", "700.000", "20 April 2026", "Tagihan"),
            Transaction("Nabung Kecil", "Pengeluaran", "1.000.000", "28 April 2026", "Investasi")
        )
    }
    var currentScreen by remember { mutableStateOf("home") }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1E1E1E)) {
                NavigationBarItem(
                    selected = currentScreen == "home",
                    onClick = { currentScreen = "home" },
                    label = { Text("Home", color = Color.White) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null, tint = Color.White) }
                )
                NavigationBarItem(
                    selected = currentScreen == "stats",
                    onClick = { currentScreen = "stats" },
                    label = { Text("Stats", color = Color.White) },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White) }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "home" -> MoneyTrackerHomeScreen(transactions)
                "stats" -> StatisticsScreen(transactions)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(transactions: List<Transaction>) {
    var selectedStatType by remember { mutableStateOf("Pengeluaran") }
    var filterMonth by remember { mutableStateOf("Semua Bulan") }

    val availableMonths = remember(transactions) {
        listOf("Semua Bulan") + transactions.mapNotNull {
            val parts = it.date.split(" ")
            if (parts.size >= 3) "${parts[1]} ${parts[2]}" else null
        }.distinct()
    }

    Column(modifier = Modifier.fillMaxSize().background(BgColor).padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("Analisis Keuangan", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                // --- SECTION: TREND (Paling Atas) ---
                Text("Trend Keuangan", color = TextMuted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Saldo Bersih", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("Perkembangan pemasukan vs pengeluaran", color = TextMuted, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(24.dp))

                        val monthlyTrend = transactions.groupBy {
                            val p = it.date.split(" "); if (p.size >= 3) "${p[1]} ${p[2]}" else "Lainnya"
                        }.mapValues { (_, txs) ->
                            val inc = txs.filter { it.type == "Pemasukan" }.sumOf { it.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 }
                            val exp = txs.filter { it.type == "Pengeluaran" }.sumOf { it.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 }
                            (inc - exp).toFloat()
                        }.toList().sortedBy { (m, _) ->
                            try { SimpleDateFormat("MMMM yyyy", Locale.getDefault()).parse(m) } catch(e:Exception) { Date(0) }
                        }

                        if (monthlyTrend.size >= 2) {
                            LineChart(monthlyTrend, modifier = Modifier.fillMaxWidth().height(200.dp))
                        } else {
                            Text("Butuh minimal 2 bulan transaksi untuk melihat trend.", color = TextMuted, fontSize = 12.sp, modifier = Modifier.padding(vertical = 20.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text("Analisis Per Kategori", color = TextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Switcher Tipe (Pemasukan / Pengeluaran)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BgColor, RoundedCornerShape(12.dp))
                                .padding(4.dp)
                        ) {
                            listOf("Pengeluaran", "Pemasukan").forEach { type ->
                                val isSelected = selectedStatType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) CardSurface else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedStatType = type }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type,
                                        color = if (isSelected) Color.White else TextMuted,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Distribusi $selectedStatType",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Filter Bulan
                            var expandedMonth by remember { mutableStateOf(false) }
                            Box {
                                Surface(
                                    onClick = { expandedMonth = true },
                                    color = BgColor,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Text(filterMonth, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }
                                DropdownMenu(expanded = expandedMonth, onDismissRequest = { expandedMonth = false }) {
                                    availableMonths.forEach { month ->
                                        DropdownMenuItem(text = { Text(month) }, onClick = { filterMonth = month; expandedMonth = false })
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Donut Chart
                        val filteredData = transactions.filter { it.type == selectedStatType && (filterMonth == "Semua Bulan" || it.date.contains(filterMonth)) }
                        val categoryData = filteredData.groupBy { it.category }
                            .mapValues { it.value.sumOf { tx -> tx.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 }.toFloat() }

                        if (categoryData.isNotEmpty()) {
                            DonutChartWithLegend(categoryData, selectedStatType)
                        } else {
                            EmptyStateMessage("Tidak ada data $selectedStatType di bulan $filterMonth")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun DonutChartWithLegend(data: Map<String, Float>, type: String) {
    val total = data.values.sum()
    val colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF06B6D4))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            Canvas(modifier = Modifier.size(150.dp)) {
                var startAngle = -90f
                data.values.forEachIndexed { i, v ->
                    val sweep = (v / total) * 360f
                    drawArc(colors[i % colors.size], startAngle, sweep, false, style = androidx.compose.ui.graphics.drawscope.Stroke(40f))
                    startAngle += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total", color = TextMuted, fontSize = 12.sp)
                Text("Rp${total.toInt().format()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Legend Grid
        data.keys.forEachIndexed { i, cat ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(colors[i % colors.size], RoundedCornerShape(2.dp)))
                Text(cat, color = TextMuted, fontSize = 14.sp, modifier = Modifier.padding(start = 12.dp).weight(1f))
                Text("Rp${data[cat]?.toInt()?.format()}", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun LineChart(data: List<Pair<String, Float>>, modifier: Modifier = Modifier) {
    val netValues = data.map { it.second }
    val maxVal = netValues.maxOrNull() ?: 0f
    val minVal = netValues.minOrNull() ?: 0f
    val range = (maxVal - minVal).coerceAtLeast(1f)

    Column(modifier = modifier) {
        Row(modifier = Modifier.weight(1f)) {
            // Y-axis Labels
            Column(
                modifier = Modifier.fillMaxHeight().padding(end = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.End
            ) {
                Text(text = "Rp${maxVal.toInt().format()}", color = TextMuted, fontSize = 9.sp)
                Text(text = "Rp${((maxVal + minVal) / 2).toInt().format()}", color = TextMuted, fontSize = 9.sp)
                Text(text = "Rp${minVal.toInt().format()}", color = TextMuted, fontSize = 9.sp)
            }

            Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
                val width = size.width
                val height = size.height
                val spacing = width / (data.size - 1).coerceAtLeast(1)

                // Grid lines
                drawLine(Color.DarkGray.copy(0.2f), Offset(0f, 0f), Offset(width, 0f), strokeWidth = 1f)
                drawLine(Color.DarkGray.copy(0.2f), Offset(0f, height / 2), Offset(width, height / 2), strokeWidth = 1f)
                drawLine(Color.DarkGray.copy(0.2f), Offset(0f, height), Offset(width, height), strokeWidth = 1f)

                val zeroY = height - ((0f - minVal) / range * height)
                if (zeroY in 0f..height) {
                    drawLine(Color.White.copy(0.15f), Offset(0f, zeroY), Offset(width, zeroY), strokeWidth = 2f)
                }

                val points = data.indices.map { i ->
                    Offset(i * spacing, height - ((netValues[i] - minVal) / range * height))
                }

                // Draw segments with green for increase and red for decrease
                for (i in 0 until points.size - 1) {
                    val color = if (netValues[i + 1] >= netValues[i]) IncomeGreen else ExpenseRed
                    drawLine(
                        color = color,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 8f,
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }

                points.forEachIndexed { i, pt ->
                    val color = if (i > 0) {
                        if (netValues[i] >= netValues[i - 1]) IncomeGreen else ExpenseRed
                    } else if (points.size > 1) {
                        if (netValues[1] >= netValues[0]) IncomeGreen else ExpenseRed
                    } else Color.White
                    drawCircle(Color.White, radius = 10f, center = pt)
                    drawCircle(color, radius = 6f, center = pt)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Month Labels at the bottom
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 60.dp), // Approximate offset to align with Canvas
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { (month, _) ->
                val monthShort = month.split(" ").first().take(3)
                Text(monthShort, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
