package com.example.money_tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.money_tracker.ui.theme.Money_TrackerTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.delay

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

// Global Config
val BgColor = Color(0xFF0F172A)
val CardSurface = Color(0xFF1E293B)
val TextMuted = Color(0xFF94A3B8)
val IncomeGreen = Color(0xFF10B981)
val ExpenseRed = Color(0xFFF43F5E)

val incomeCategories = listOf("Gaji", "Bonus", "Investasi", "Hibah", "Penjualan", "Lainnya")
val expenseCategories = listOf("Makanan", "Transportasi", "Belanja", "Hiburan", "Pendidikan", "Kesehatan", "Tagihan", "Rumah", "Lainnya")

data class Transaction(val name: String, val type: String, val amount: String, val date: String, val category: String)

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000)
        onTimeout()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(300.dp)
            )
            Spacer(modifier = Modifier.height(-8.dp))
            Text(
                text = "Smart Way to Manage Finance",
                color = TextMuted,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MoneyTrackerApp() {
    var showSplash by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }

//    "Makanan", "Transportasi", "Belanja", "Hiburan", "Pendidikan", "Kesehatan", "Tagihan", "Rumah", "Lainnya"
    val transactions = remember {
        mutableStateListOf(
            Transaction("Gaji Januari", "Pemasukan", "8.000.000", "01 January 2026", "Gaji"),
            Transaction("Kebutuhan Kost", "Pengeluaran", "1.500.000", "02 January 2026", "Rumah"),
            Transaction("Bensin", "Pengeluaran", "50.000", "10 January 2026", "Transportasi"),
            Transaction("Bensin", "Pengeluaran", "50.000", "20 January 2026", "Transportasi"),
            Transaction("Kuota Internet", "Pengeluaran", "100.000", "15 January 2026", "Tagihan"),
            Transaction("Langganan Spotify", "Pengeluaran", "80.000", "05 January 2026", "Tagihan"),
            Transaction("Makan Bulanan", "Pengeluaran", "600.000", "30 January 2026", "Makanan"),
            Transaction("Trip Bromo", "Pengeluaran", "1.500.000", "16 January 2026", "Hiburan"),

            Transaction("Gaji Februari", "Pemasukan", "8.000.000", "01 February 2026", "Gaji"),
            Transaction("Kebutuhan Kost", "Pengeluaran", "1.500.000", "02 February 2026", "Rumah"),
            Transaction("Bensin", "Pengeluaran", "50.000", "10 February 2026", "Transportasi"),
            Transaction("Bensin", "Pengeluaran", "50.000", "20 February 2026", "Transportasi"),
            Transaction("Kuota Internet", "Pengeluaran", "100.000", "15 February 2026", "Tagihan"),
            Transaction("Langganan Spotify", "Pengeluaran", "80.000", "05 February 2026", "Hiburan"),
            Transaction("Belanja Online", "Pengeluaran", "500.000", "08 February 2026", "Belanja"),
            Transaction("Makan Bulanan", "Pengeluaran", "800.000", "30 January 2026", "Makanan"),
            Transaction("Bonus Project", "Pemasukan", "1.500.000", "28 February 2026", "Bonus"),

            Transaction("Gaji Maret", "Pemasukan", "8.000.000", "01 March 2026", "Gaji"),
            Transaction("Beli HP Baru", "Pengeluaran", "6.000.000", "05 March 2026", "Belanja"),
            Transaction("Service Laptop", "Pengeluaran", "1.000.000", "10 March 2026", "Lainnya"),
            Transaction("Kebutuhan Kost", "Pengeluaran", "1.500.000", "02 March 2026", "Rumah"),
            Transaction("Bensin", "Pengeluaran", "50.000", "10 March 2026", "Transportasi"),
            Transaction("Bensin", "Pengeluaran", "50.000", "20 March 2026", "Transportasi"),
            Transaction("Kuota Internet", "Pengeluaran", "100.000", "15 March 2026", "Tagihan"),
            Transaction("Langganan Spotify", "Pengeluaran", "80.000", "05 March 2026", "Tagihan"),
            Transaction("Makan Bulanan", "Pengeluaran", "600.000", "30 March 2026", "Makanan"),
            Transaction("Checkup Kesehatan", "Pengeluaran", "600.000", "21 March 2026", "Kesehatan"),
        )
    }

    if (showSplash) {
        SplashScreen(onTimeout = { showSplash = false })
    } else {
        var currentScreen by remember { mutableStateOf("home") }
        Scaffold(
            floatingActionButton = {
                if (currentScreen == "home") {
                    ExtendedFloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = Color(0xFF6366F1),
                        contentColor = Color.White,
                        icon = { Icon(Icons.Default.Add, null) },
                        text = { Text("Transaksi", fontWeight = FontWeight.Bold) }
                    )
                }
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF1E1E1E)) {
                    NavigationBarItem(
                        selected = currentScreen == "home",
                        onClick = { currentScreen = "home" },
                        label = { Text("Home", color = Color.White) },
                        icon = { Icon(Icons.Default.Home, null, tint = Color.White) }
                    )
                    NavigationBarItem(
                        selected = currentScreen == "stats",
                        onClick = { currentScreen = "stats" },
                        label = { Text("Stats", color = Color.White) },
                        icon = { Icon(Icons.Default.DateRange, null, tint = Color.White) }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    "home" -> MoneyTrackerHomeScreen(
                        transactions = transactions,
                        showAddDialog = showAddDialog,
                        onDismissDialog = { showAddDialog = false },
                        onOpenDialog = { showAddDialog = true }
                    )
                    "stats" -> StatisticsScreen(transactions)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyTrackerHomeScreen(
    transactions: MutableList<Transaction>,
    showAddDialog: Boolean,
    onDismissDialog: () -> Unit,
    onOpenDialog: () -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTransactionType by remember { mutableStateOf("Pengeluaran") }
    var filterType by remember { mutableStateOf("All") }
    var filterMonth by remember { mutableStateOf("Semua Bulan") }
    var searchQuery by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH) }
    val availableMonths = remember(transactions) {
        listOf("Semua Bulan") + transactions.mapNotNull {
            val parts = it.date.split(" ")
            if (parts.size >= 3) "${parts[1]} ${parts[2]}" else null
        }.distinct()
    }

    var selectedTransactionIndex by remember { mutableStateOf(-1) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

    val displayedTransactions = remember(transactions, filterType, filterMonth, searchQuery) {
        transactions.filter { transaction ->
            val matchType = if (filterType == "All") true else transaction.type == filterType
            val parts = transaction.date.split(" ")
            val monthYear = if (parts.size >= 3) "${parts[1]} ${parts[2]}" else ""
            val matchMonth = if (filterMonth == "Semua Bulan") true else monthYear == filterMonth
            
            // SEARCH ONLY BY NAME
            val query = searchQuery.trim().lowercase()
            val matchSearch = query.isEmpty() || transaction.name.lowercase().contains(query)

            matchType && matchMonth && matchSearch
        }.sortedByDescending { try { dateFormat.parse(it.date) } catch (e: Exception) { Date(0) } }
    }

    val totalIncome = transactions.filter { it.type == "Pemasukan" }.sumOf { it.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 }
    val totalExpense = transactions.filter { it.type == "Pengeluaran" }.sumOf { it.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 }
    val balance = totalIncome - totalExpense

    if (showAddDialog) {
        AddTransactionDialog(
            transactionType = selectedTransactionType,
            onTypeChange = { selectedTransactionType = it },
            onAddTransaction = { name, amount, category, date ->
                val cleanAmount = amount.replace(Regex("[^0-9]"), "")
                transactions.add(Transaction(name, selectedTransactionType, cleanAmount.toIntOrNull()?.format() ?: "0", date, category))
                onDismissDialog()
            },
            onDismiss = onDismissDialog
        )
    }

    if (showEditDialog && editingTransaction != null) {
        EditTransactionDialog(
            transaction = editingTransaction!!,
            onUpdateTransaction = { name, type, amount, category, date ->
                val cleanAmount = amount.replace(Regex("[^0-9]"), "")
                val updated = Transaction(name, type, cleanAmount.toIntOrNull()?.format() ?: "0", date, category)
                if (selectedTransactionIndex in transactions.indices) transactions[selectedTransactionIndex] = updated
                showEditDialog = false
            },
            onDeleteTransaction = {
                if (selectedTransactionIndex in transactions.indices) transactions.removeAt(selectedTransactionIndex)
                showEditDialog = false
            },
            onDismiss = { showEditDialog = false }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Halo, Hikmia!", color = TextMuted, fontSize = 14.sp)
                    Text("Kelola uangmu hari ini", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                var expandedMonth by remember { mutableStateOf(false) }
                Box {
                    Surface(
                        onClick = { expandedMonth = true },
                        color = CardSurface,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(filterMonth, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = expandedMonth,
                        onDismissRequest = { expandedMonth = false }
                    ) {
                        availableMonths.forEach { month ->
                            DropdownMenuItem(
                                text = { Text(month) },
                                onClick = {
                                    filterMonth = month
                                    expandedMonth = false
                                }
                            )
                        }
                    }
                }
//                Spacer(modifier = Modifier.width(30.dp))
            }

            // Wallet Card
            var isBalanceVisible by remember { mutableStateOf(true) }
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), colors = CardDefaults.cardColors(containerColor = CardSurface), shape = RoundedCornerShape(28.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Total Saldo", color = TextMuted, fontSize = 14.sp)
                        Icon(imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp).clickable { isBalanceVisible = !isBalanceVisible })
                    }
                    Text(text = if (isBalanceVisible) "Rp${balance.format()}" else "Rp ******", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        InfoColumn("Pemasukan", if (isBalanceVisible) "Rp${totalIncome.format()}" else "Rp ******", IncomeGreen, Icons.Default.KeyboardArrowDown, Modifier.weight(1f))
                        InfoColumn("Pengeluaran", if (isBalanceVisible) "Rp${totalExpense.format()}" else "Rp ******", ExpenseRed, Icons.Default.KeyboardArrowUp, Modifier.weight(1f), Alignment.End)
                    }
                }
            }

            // Search Bar & Filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Cari nama...", fontSize = 13.sp) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6366F1),
                    unfocusedBorderColor = TextMuted.copy(0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = CardSurface.copy(0.5f),
                    unfocusedContainerColor = CardSurface.copy(0.5f)
                ),
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted, modifier = Modifier.size(18.dp)) }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All", "Pemasukan", "Pengeluaran").forEach { type ->
                    FilterChip(selected = filterType == type, onClick = { filterType = type }, label = { Text(type, fontSize = 11.sp) }, modifier = Modifier.height(34.dp), colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF6366F1).copy(0.2f), selectedLabelColor = Color(0xFF6366F1)))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        items(displayedTransactions) { transaction ->
            val globalIndex = transactions.indexOf(transaction)
            TransactionItem(transaction) {
                selectedTransactionIndex = globalIndex
                editingTransaction = transactions[globalIndex]
                showEditDialog = true
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
fun DatePickerDialog(onDismissRequest: () -> Unit, onDateSelected: (Date) -> Unit, initialDate: Date) {
    val calendar = Calendar.getInstance().apply { time = initialDate }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = calendar.timeInMillis)
    MaterialTheme(colorScheme = darkColorScheme(surface = CardSurface, onSurface = Color.White, primary = Color(0xFF6366F1), onPrimary = Color.White)) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = { TextButton(onClick = { datePickerState.selectedDateMillis?.let { onDateSelected(Date(it)) } }) { Text("Pilih", color = Color(0xFF6366F1), fontWeight = FontWeight.Bold) } },
            dismissButton = { TextButton(onClick = onDismissRequest) { Text("Batal", color = TextMuted) } },
            colors = DatePickerDefaults.colors(containerColor = CardSurface)
        ) {
            DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(containerColor = CardSurface, titleContentColor = Color.White, headlineContentColor = Color.White, weekdayContentColor = TextMuted, subheadContentColor = TextMuted, yearContentColor = Color.White, currentYearContentColor = Color(0xFF6366F1), selectedYearContentColor = Color.White, selectedYearContainerColor = Color(0xFF6366F1), dayContentColor = Color.White, selectedDayContainerColor = Color(0xFF6366F1), selectedDayContentColor = Color.White, todayContentColor = Color(0xFF6366F1), todayDateBorderColor = Color(0xFF6366F1)))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(transactionType: String, onTypeChange: (String) -> Unit, onAddTransaction: (String, String, String, String) -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val accentColor = if (transactionType == "Pemasukan") IncomeGreen else ExpenseRed
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    val categories = if (transactionType == "Pemasukan") incomeCategories else expenseCategories
    var selectedCategory by remember { mutableStateOf(categories.first()) }
    LaunchedEffect(transactionType) { selectedCategory = if (transactionType == "Pemasukan") incomeCategories.first() else expenseCategories.first() }
    if (showDatePicker) { DatePickerDialog(onDismissRequest = { showDatePicker = false }, onDateSelected = { selectedDate = it; showDatePicker = false }, initialDate = selectedDate) }
    AlertDialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false), modifier = Modifier.fillMaxWidth(0.95f), content = { Surface(color = CardSurface, shape = RoundedCornerShape(28.dp)) { Column(modifier = Modifier.padding(24.dp)) { Text("Tambah Transaksi", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(20.dp)); Row(modifier = Modifier.fillMaxWidth().background(BgColor, RoundedCornerShape(12.dp)).padding(4.dp)) { listOf("Pemasukan", "Pengeluaran").forEach { type -> val isSelected = transactionType == type; val chipColor = if (type == "Pemasukan") IncomeGreen else ExpenseRed; Box(modifier = Modifier.weight(1f).background(if (isSelected) chipColor.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(10.dp)).clickable { onTypeChange(type) }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) { Text(type, color = if (isSelected) chipColor else TextMuted, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) } } }; Spacer(modifier = Modifier.height(24.dp)); OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Transaksi") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = Color.White, focusedTextColor = Color.White), shape = RoundedCornerShape(12.dp)); Spacer(modifier = Modifier.height(12.dp)); OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Jumlah (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = Color.White, focusedTextColor = Color.White), shape = RoundedCornerShape(12.dp), prefix = { Text("Rp ", color = TextMuted) }); Spacer(modifier = Modifier.height(12.dp)); OutlinedTextField(value = dateFormat.format(selectedDate), onValueChange = {}, label = { Text("Tanggal") }, readOnly = true, modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }, trailingIcon = { Icon(Icons.Default.DateRange, null, tint = TextMuted, modifier = Modifier.clickable { showDatePicker = true }) }, colors = OutlinedTextFieldDefaults.colors(unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp)); Spacer(modifier = Modifier.height(12.dp)); var expanded by remember { mutableStateOf(false) }; ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) { OutlinedTextField(value = selectedCategory, onValueChange = {}, readOnly = true, label = { Text("Kategori") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = Color.White, focusedTextColor = Color.White), shape = RoundedCornerShape(12.dp)); ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(CardSurface)) { categories.forEach { category -> DropdownMenuItem(text = { Text(category, color = Color.White) }, onClick = { selectedCategory = category; expanded = false }, colors = MenuDefaults.itemColors(textColor = Color.White)) } } }; Spacer(modifier = Modifier.height(32.dp)); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) { TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal", color = TextMuted) }; Button(onClick = { if (name.isNotBlank() && amount.isNotBlank()) onAddTransaction(name, amount, selectedCategory, dateFormat.format(selectedDate)) }, modifier = Modifier.weight(1.5f), colors = ButtonDefaults.buttonColors(containerColor = accentColor), shape = RoundedCornerShape(12.dp), enabled = name.isNotBlank() && amount.isNotBlank()) { Text("Simpan Transaksi", fontWeight = FontWeight.Bold, color = Color.White) } } } } } )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionDialog(transaction: Transaction, onUpdateTransaction: (String, String, String, String, String) -> Unit, onDeleteTransaction: () -> Unit, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(transaction.name) }
    var type by remember { mutableStateOf(transaction.type) }
    var amount by remember { mutableStateOf(transaction.amount.replace(",", "")) }
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    var selectedDate by remember { mutableStateOf(try { dateFormat.parse(transaction.date) ?: Date() } catch (e: Exception) { Date() }) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteWarning by remember { mutableStateOf(false) }
    val categories = if (type == "Pemasukan") incomeCategories else expenseCategories
    var selectedCategory by remember { mutableStateOf(transaction.category) }
    val accentColor = if (type == "Pemasukan") IncomeGreen else ExpenseRed
    LaunchedEffect(type) { if (!categories.contains(selectedCategory)) { selectedCategory = categories.first() } }
    if (showDatePicker) { DatePickerDialog(onDismissRequest = { showDatePicker = false }, onDateSelected = { selectedDate = it; showDatePicker = false }, initialDate = selectedDate) }
    if (showDeleteWarning) { AlertDialog(onDismissRequest = { showDeleteWarning = false }, title = { Text("Hapus Transaksi", color = Color.White) }, text = { Text("Apakah Anda yakin ingin menghapus transaksi ini?", color = Color.White) }, containerColor = CardSurface, confirmButton = { TextButton(onClick = { onDeleteTransaction(); showDeleteWarning = false }) { Text("Hapus", color = ExpenseRed, fontWeight = FontWeight.Bold) } }, dismissButton = { TextButton(onClick = { showDeleteWarning = false }) { Text("Batal", color = TextMuted) } } ) }
    AlertDialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false), modifier = Modifier.fillMaxWidth(0.95f), content = { Surface(color = CardSurface, shape = RoundedCornerShape(28.dp)) { Column(modifier = Modifier.padding(24.dp)) { Text("Edit Transaksi", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(20.dp)); Row(modifier = Modifier.fillMaxWidth().background(BgColor, RoundedCornerShape(12.dp)).padding(4.dp)) { listOf("Pemasukan", "Pengeluaran").forEach { typeOption -> val isSelected = type == typeOption; val chipColor = if (typeOption == "Pemasukan") IncomeGreen else ExpenseRed; Box(modifier = Modifier.weight(1f).background(if (isSelected) chipColor.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(10.dp)).clickable { type = typeOption }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) { Text(typeOption, color = if (isSelected) chipColor else TextMuted, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) } } }; Spacer(modifier = Modifier.height(24.dp)); OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Transaksi") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = Color.White, focusedTextColor = Color.White), shape = RoundedCornerShape(12.dp)); Spacer(modifier = Modifier.height(12.dp)); OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Jumlah (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = Color.White, focusedTextColor = Color.White), shape = RoundedCornerShape(12.dp), prefix = { Text("Rp ", color = TextMuted) }); Spacer(modifier = Modifier.height(12.dp)); OutlinedTextField(value = dateFormat.format(selectedDate), onValueChange = {}, label = { Text("Tanggal") }, readOnly = true, modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }, trailingIcon = { Icon(Icons.Default.DateRange, null, tint = TextMuted, modifier = Modifier.clickable { showDatePicker = true }) }, colors = OutlinedTextFieldDefaults.colors(unfocusedTextColor = Color.White), shape = RoundedCornerShape(12.dp)); Spacer(modifier = Modifier.height(12.dp)); var expanded by remember { mutableStateOf(false) }; ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) { OutlinedTextField(value = selectedCategory, onValueChange = {}, readOnly = true, label = { Text("Kategori") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = accentColor, focusedLabelColor = accentColor, unfocusedTextColor = Color.White, focusedTextColor = Color.White), shape = RoundedCornerShape(12.dp)); ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(CardSurface)) { categories.forEach { category -> DropdownMenuItem(text = { Text(category, color = Color.White) }, onClick = { selectedCategory = category; expanded = false }, colors = MenuDefaults.itemColors(textColor = Color.White)) } } }; Spacer(modifier = Modifier.height(32.dp)); Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) { IconButton(onClick = { showDeleteWarning = true }, modifier = Modifier.background(ExpenseRed.copy(0.1f), RoundedCornerShape(12.dp))) { Icon(Icons.Default.Delete, null, tint = ExpenseRed) }; TextButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Batal", color = TextMuted) }; Button(onClick = { if (name.isNotBlank() && amount.isNotBlank()) onUpdateTransaction(name, type, amount, selectedCategory, dateFormat.format(selectedDate)) }, modifier = Modifier.weight(1.5f), colors = ButtonDefaults.buttonColors(containerColor = accentColor), shape = RoundedCornerShape(12.dp), enabled = name.isNotBlank() && amount.isNotBlank()) { Text("Update", fontWeight = FontWeight.Bold, color = Color.White) } } } } } )
}

@Composable
fun TransactionItem(transaction: Transaction, onClick: () -> Unit) {
    val isIncome = transaction.type == "Pemasukan"
    val iconColor = if (isIncome) IncomeGreen else ExpenseRed
    val bgColor = if (isIncome) IncomeGreen.copy(alpha = 0.15f) else ExpenseRed.copy(alpha = 0.15f)
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).background(bgColor, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (isIncome) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp, contentDescription = null, tint = iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(transaction.name, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = transaction.category, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(" • ", color = TextMuted, fontSize = 12.sp)
                    Text(transaction.date, color = TextMuted, fontSize = 12.sp)
                }
            }
        }
        Text(text = if (isIncome) "+Rp${transaction.amount}" else "-Rp${transaction.amount}", color = if (isIncome) IncomeGreen else Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(transactions: List<Transaction>) {
    var selectedStatType by remember { mutableStateOf("Pengeluaran") }
    var filterMonth by remember { mutableStateOf("Semua Bulan") }
    var expandedCategoryName by remember { mutableStateOf<String?>(null) }
    val availableMonths = remember(transactions) { listOf("Semua Bulan") + transactions.mapNotNull { val parts = it.date.split(" "); if (parts.size >= 3) "${parts[1]} ${parts[2]}" else null }.distinct() }

    Column(modifier = Modifier.fillMaxSize().background(BgColor).padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("Analisis Keuangan", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)

        LazyColumn(modifier = Modifier.weight(1f)) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Trend Keuangan", color = TextMuted, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardSurface), shape = RoundedCornerShape(24.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Saldo Bersih",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Perkembangan akumulasi saldo", color = TextMuted, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(24.dp))

                        // --- GANTI DARI SINI ---
                        val trendDateFormat =
                            remember { SimpleDateFormat("MMMM yyyy", Locale.ENGLISH) }

                        // 1. Kelompokkan dan hitung selisih per bulan (Pemasukan - Pengeluaran)
                        val monthlyNet = transactions
                            .groupBy {
                                val p = it.date.split(" ")
                                if (p.size >= 3) "${p[1]} ${p[2]}" else "Other"
                            }
                            .mapValues { (_, txs) ->
                                val inc = txs.filter { it.type == "Pemasukan" }
                                    .sumOf {
                                        it.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                                    }
                                val exp = txs.filter { it.type == "Pengeluaran" }
                                    .sumOf {
                                        it.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
                                    }
                                inc - exp
                            }
                            .toList()
                            .sortedBy { (m, _) ->
                                try {
                                    trendDateFormat.parse(m)
                                } catch (e: Exception) {
                                    Date(0)
                                }
                            }

                        // 2. Hitung Akumulasi (Saldo bulan lalu + bulan ini)
                        var currentBalance = 0f
                        val monthlyTrend = monthlyNet.map { (month, net) ->
                            currentBalance += net
                            month to currentBalance
                        }

                        // 3. Tampilkan Chart
                        if (monthlyTrend.size >= 2) {
                            LineChart(
                                monthlyTrend,
                                modifier = Modifier.fillMaxWidth().height(200.dp)
                            )
                        } else {
                            Text(
                                "Butuh minimal 2 bulan transaksi untuk melihat trend.",
                                color = TextMuted,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 20.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("Analisis Per Kategori", color = TextMuted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardSurface), shape = RoundedCornerShape(24.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth().background(BgColor, RoundedCornerShape(12.dp)).padding(4.dp)) {
                            listOf("Pengeluaran", "Pemasukan").forEach { type -> val isSelected = selectedStatType == type; Box(modifier = Modifier.weight(1f).background(if (isSelected) CardSurface else Color.Transparent, RoundedCornerShape(10.dp)).clickable { selectedStatType = type; expandedCategoryName = null }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) { Text(text = type, color = if (isSelected) Color.White else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold) } }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Distribusi $selectedStatType", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            var expandedMonth by remember { mutableStateOf(false) }
                            Box {
                                Surface(onClick = { expandedMonth = true }, color = BgColor, shape = RoundedCornerShape(12.dp)) { Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) { Text(filterMonth, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold); Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.White, modifier = Modifier.size(14.dp)) } }
                                DropdownMenu(expanded = expandedMonth, onDismissRequest = { expandedMonth = false }) { availableMonths.forEach { month -> DropdownMenuItem(text = { Text(month) }, onClick = { filterMonth = month; expandedMonth = false; expandedCategoryName = null }) } }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        val filteredData = transactions.filter { it.type == selectedStatType && (filterMonth == "Semua Bulan" || it.date.contains(filterMonth)) }
                        val categoryData = filteredData.groupBy { it.category }.mapValues { it.value.sumOf { tx -> tx.amount.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0 }.toFloat() }
                        if (categoryData.isNotEmpty()) { DonutChartWithLegend(categoryData, filteredData, expandedCategoryName, onCategoryClick = { expandedCategoryName = if (expandedCategoryName == it) null else it }) } else { EmptyStateMessage("Tidak ada data $selectedStatType di bulan $filterMonth") }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
        Text(text = message, color = TextMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun DonutChartWithLegend(data: Map<String, Float>, filteredTransactions: List<Transaction>, expandedCategory: String?, onCategoryClick: (String) -> Unit) {
    val total = data.values.sum(); val colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFFEC4899), Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFF06B6D4))
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            Canvas(modifier = Modifier.size(150.dp)) { var startAngle = -90f; data.values.forEachIndexed { i, v -> val sweep = (v / total) * 360f; drawArc(colors[i % colors.size], startAngle, sweep, false, style = androidx.compose.ui.graphics.drawscope.Stroke(40f)); startAngle += sweep } }
            Column(horizontalAlignment = Alignment.CenterHorizontally) { Text("Total", color = TextMuted, fontSize = 12.sp); Text("Rp${total.toInt().format()}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        }
        Spacer(modifier = Modifier.height(24.dp))
        data.keys.forEachIndexed { i, cat ->
            val isSelected = cat == expandedCategory
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Row(modifier = Modifier.fillMaxWidth().background(if (isSelected) Color.White.copy(0.05f) else Color.Transparent, RoundedCornerShape(8.dp)).clickable { onCategoryClick(cat) }.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).background(colors[i % colors.size], RoundedCornerShape(2.dp))); Text(cat, color = if (isSelected) Color.White else TextMuted, fontSize = 14.sp, modifier = Modifier.padding(start = 12.dp).weight(1f), fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium); Text("Rp${data[cat]?.toInt()?.format()}", color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                if (isSelected) { filteredTransactions.filter { it.category == cat }.forEach { tx -> Row(modifier = Modifier.fillMaxWidth().padding(start = 30.dp, end = 8.dp, top = 4.dp, bottom = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text(tx.name, color = Color.White, fontSize = 12.sp); Text(tx.date, color = TextMuted, fontSize = 10.sp) }; Text("Rp${tx.amount}", color = if (tx.type == "Pemasukan") IncomeGreen else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold) } } }
            }
        }
    }
}

@Composable
fun LineChart(data: List<Pair<String, Float>>, modifier: Modifier = Modifier) {
    val netValues = data.map { it.second }; val maxVal = netValues.maxOrNull() ?: 0f; val minVal = netValues.minOrNull() ?: 0f; val range = (maxVal - minVal).coerceAtLeast(1f)
    Column(modifier = modifier) {
        Row(modifier = Modifier.weight(1f)) {
            Column(modifier = Modifier.fillMaxHeight().padding(end = 12.dp), verticalArrangement = Arrangement.SpaceBetween, horizontalAlignment = Alignment.End) { Text("Rp${maxVal.toInt().format()}", color = TextMuted, fontSize = 9.sp); Text("Rp${((maxVal + minVal) / 2).toInt().format()}", color = TextMuted, fontSize = 9.sp); Text("Rp${minVal.toInt().format()}", color = TextMuted, fontSize = 9.sp) }
            Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
                val width = size.width; val height = size.height; val spacing = width / (data.size - 1).coerceAtLeast(1)
                drawLine(Color.DarkGray.copy(0.2f), Offset(0f, 0f), Offset(width, 0f), 1f); drawLine(Color.DarkGray.copy(0.2f), Offset(0f, height / 2), Offset(width, height / 2), 1f); drawLine(Color.DarkGray.copy(0.2f), Offset(0f, height), Offset(width, height), 1f)
                val zeroY = height - ((0f - minVal) / range * height); if (zeroY in 0f..height) { drawLine(Color.White.copy(0.15f), Offset(0f, zeroY), Offset(width, zeroY), 2f) }
                val points = data.indices.map { i -> Offset(i * spacing, height - ((netValues[i] - minVal) / range * height)) }
                for (i in 0 until points.size - 1) { drawLine(if (netValues[i + 1] >= netValues[i]) IncomeGreen else ExpenseRed, points[i], points[i + 1], 8f, androidx.compose.ui.graphics.StrokeCap.Round) }
                points.forEachIndexed { i, pt -> val color = if (i > 0) { if (netValues[i] >= netValues[i - 1]) IncomeGreen else ExpenseRed } else if (points.size > 1) { if (netValues[1] >= netValues[0]) IncomeGreen else ExpenseRed } else Color.White; drawCircle(Color.White, 10f, pt); drawCircle(color, 6f, pt) }
            }
        }
        Spacer(modifier = Modifier.height(12.dp)); Row(modifier = Modifier.fillMaxWidth().padding(start = 60.dp), horizontalArrangement = Arrangement.SpaceBetween) { data.forEach { (month, _) -> Text(month.split(" ").first().take(3), color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold) } }
    }
}
fun Int.format(): String = "%,d".format(this)
