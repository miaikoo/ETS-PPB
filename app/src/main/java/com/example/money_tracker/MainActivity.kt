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
import com.example.money_tracker.ui.theme.Money_TrackerTheme
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Money_TrackerTheme {
                MoneyTrackerScreen()
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

// Modifikasi model Transaction dengan menambahkan kategori
data class Transaction(
    val name: String,
    val type: String,
    val amount: String,
    val date: String,
    val category: String // Menambahkan field kategori
)

@Composable
fun MoneyTrackerScreen() {
    val transactions = remember { mutableStateListOf<Transaction>() }

    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedTransactionType by remember { mutableStateOf("Pengeluaran") }
    var filterType by remember { mutableStateOf("All") }

    // Untuk proses edit transaksi
    var selectedTransactionIndex by remember { mutableStateOf(-1) }
    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

    val displayedTransactions = remember(transactions, filterType) {
        when (filterType) {
            "Pemasukan" -> transactions.filter { it.type == "Pemasukan" }
            "Pengeluaran" -> transactions.filter { it.type == "Pengeluaran" }
            else -> transactions
        }
    }

    val totalIncome = transactions.filter { it.type == "Pemasukan" }
        .sumOf { it.amount.replace(".", "").toInt() }
    val totalExpense = transactions.filter { it.type == "Pengeluaran" }
        .sumOf { it.amount.replace(".", "").toInt() }
    val balance = totalIncome - totalExpense

    // Dialog untuk tambah transaksi baru
    if (showAddDialog) {
        AddTransactionDialog(
            transactionType = selectedTransactionType,
            onTypeChange = { selectedTransactionType = it },
            onAddTransaction = { name, amount, category, date ->
                val formattedAmount = amount.replace(".", "").toInt().format()
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

    // Dialog untuk edit transaksi
    if (showEditDialog && editingTransaction != null) {
        EditTransactionDialog(
            transaction = editingTransaction!!,
            onUpdateTransaction = { name, type, amount, category, date ->
                val updatedTransaction = Transaction(
                    name = name,
                    type = type,
                    amount = amount.replace(".", "").toInt().format(),
                    date = date,
                    category = category
                )

                if (selectedTransactionIndex >= 0 && selectedTransactionIndex < transactions.size) {
                    transactions[selectedTransactionIndex] = updatedTransaction
                }

                showEditDialog = false
                editingTransaction = null
                selectedTransactionIndex = -1
            },
            onDeleteTransaction = {
                if (selectedTransactionIndex >= 0 && selectedTransactionIndex < transactions.size) {
                    transactions.removeAt(selectedTransactionIndex)
                }
                showEditDialog = false
                editingTransaction = null
                selectedTransactionIndex = -1
            },
            onDismiss = {
                showEditDialog = false
                editingTransaction = null
                selectedTransactionIndex = -1
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF121212)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Money Tracker",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rp${balance.format()}",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Pemasukan",
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Rp${totalIncome.format()}",
                        color = Color.Green,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Pengeluaran",
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Rp${totalExpense.format()}",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Filter buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { filterType = "All" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filterType == "All") MaterialTheme.colorScheme.primary else Color.Gray
                    )
                ) {
                    Text("All")
                }
                Button(
                    onClick = { filterType = "Pemasukan" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filterType == "Pemasukan") MaterialTheme.colorScheme.primary else Color.Gray
                    )
                ) {
                    Text("Pemasukan")
                }
                Button(
                    onClick = { filterType = "Pengeluaran" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (filterType == "Pengeluaran") MaterialTheme.colorScheme.primary else Color.Gray
                    )
                ) {
                    Text("Pengeluaran")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grafik Transaksi
            if (transactions.isNotEmpty()) {
                TransactionBarChart(
                    incomeAmount = totalIncome,
                    expenseAmount = totalExpense,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Daftar Transaksi
            if (displayedTransactions.isEmpty()) {
                EmptyStateMessage("Belum ada transaksi")
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(displayedTransactions.size) { index ->
                        val globalIndex = transactions.indexOf(displayedTransactions[index])
                        TransactionItem(
                            transaction = displayedTransactions[index],
                            onClick = {
                                selectedTransactionIndex = globalIndex
                                editingTransaction = transactions[globalIndex]
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TransactionBarChart(
    incomeAmount: Int,
    expenseAmount: Int,
    modifier: Modifier = Modifier
) {
    val maxValue = max(incomeAmount, expenseAmount).toFloat()
    val incomeRatio = if (maxValue > 0) incomeAmount / maxValue else 0f
    val expenseRatio = if (maxValue > 0) expenseAmount / maxValue else 0f

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
        ) {
            val barWidth = size.width * 0.3f
            val spacing = size.width * 0.4f

            // Draw Income Bar
            val incomeHeight = size.height * incomeRatio
            drawRect(
                color = Color.Green,
                topLeft = Offset(size.width / 4 - barWidth / 2, size.height - incomeHeight),
                size = Size(barWidth, incomeHeight)
            )

            // Draw Expense Bar
            val expenseHeight = size.height * expenseRatio
            drawRect(
                color = Color.Red,
                topLeft = Offset(3 * size.width / 4 - barWidth / 2, size.height - expenseHeight),
                size = Size(barWidth, expenseHeight)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chart Labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Pemasukan",
                    color = Color.Green,
                    fontSize = 12.sp
                )
                Text(
                    text = "Rp${incomeAmount.format()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Pengeluaran",
                    color = Color.Red,
                    fontSize = 12.sp
                )
                Text(
                    text = "Rp${expenseAmount.format()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Date) -> Unit,
    initialDate: Date
) {
    val calendar = Calendar.getInstance().apply {
        time = initialDate
    }

    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pilih Tanggal",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Gunakan DatePicker dari Material 3
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = calendar.timeInMillis
                )
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.padding(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("Batal")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val selectedCalendar = Calendar.getInstance().apply {
                                    timeInMillis = millis
                                }
                                onDateSelected(selectedCalendar.time)
                            } ?: onDateSelected(calendar.time)
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
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

    // State untuk tanggal
    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableStateOf(calendar.time) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

    // Menentukan daftar kategori berdasarkan jenis transaksi
    val categories = if (transactionType == "Pemasukan") incomeCategories else expenseCategories
    var selectedCategory by remember { mutableStateOf(categories.first()) }

    // Update kategori saat jenis transaksi berubah
    LaunchedEffect(transactionType) {
        val newCategories = if (transactionType == "Pemasukan") incomeCategories else expenseCategories
        selectedCategory = newCategories.first()
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Transaksi") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Transaksi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Jumlah (Rp)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Field pemilih tanggal
                OutlinedTextField(
                    value = dateFormat.format(selectedDate),
                    onValueChange = { },
                    label = { Text("Tanggal") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pilih Tanggal"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown kategori
                Text("Kategori", fontWeight = FontWeight.Medium)

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = transactionType == "Pemasukan",
                        onClick = { onTypeChange("Pemasukan") },
                        label = { Text("Pemasukan") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Pemasukan",
                                tint = Color.Green
                            )
                        }
                    )

                    FilterChip(
                        selected = transactionType == "Pengeluaran",
                        onClick = { onTypeChange("Pengeluaran") },
                        label = { Text("Pengeluaran") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Pengeluaran",
                                tint = Color.Red
                            )
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && amount.isNotBlank()) {
                        onAddTransaction(name, amount, selectedCategory, dateFormat.format(selectedDate))
                    }
                },
                enabled = name.isNotBlank() && amount.isNotBlank()
            ) {
                Text("Tambah")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
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

    // Menentukan daftar kategori berdasarkan jenis transaksi
    val categories = if (type == "Pemasukan") incomeCategories else expenseCategories
    var selectedCategory by remember { mutableStateOf(transaction.category) }

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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Transaksi") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Transaksi") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Jumlah (Rp)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Field pemilih tanggal
                OutlinedTextField(
                    value = dateFormat.format(selectedDate),
                    onValueChange = { },
                    label = { Text("Tanggal") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Pilih Tanggal"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dropdown kategori
                Text("Kategori", fontWeight = FontWeight.Medium)

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    OutlinedTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FilterChip(
                        selected = type == "Pemasukan",
                        onClick = { type = "Pemasukan" },
                        label = { Text("Pemasukan") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Pemasukan",
                                tint = Color.Green
                            )
                        }
                    )

                    FilterChip(
                        selected = type == "Pengeluaran",
                        onClick = { type = "Pengeluaran" },
                        label = { Text("Pengeluaran") },
                        leadingIcon = {
                            Icon(
                                Icons.Filled.KeyboardArrowUp,
                                contentDescription = "Pengeluaran",
                                tint = Color.Red
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Delete Button
                Button(
                    onClick = onDeleteTransaction,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Hapus Transaksi")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && amount.isNotBlank()) {
                        onUpdateTransaction(
                            name,
                            type,
                            amount,
                            selectedCategory,
                            dateFormat.format(selectedDate)
                        )
                    }
                },
                enabled = name.isNotBlank() && amount.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(transaction.name, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Menampilkan kategori dengan chip
                    SuggestionChip(
                        onClick = {},
                        label = { Text(transaction.category, fontSize = 10.sp) },
                        modifier = Modifier.padding(end = 8.dp),
                        enabled = false
                    )
                    Text(transaction.date, fontSize = 12.sp, color = Color.Gray)
                }
            }
            Text(
                text = if (transaction.type == "Pemasukan") "+Rp${transaction.amount}" else "-Rp${transaction.amount}",
                color = if (transaction.type == "Pemasukan") Color.Green else Color.Red,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun Int.format(): String = "%,d".format(this)

@Preview(showBackground = true)
@Composable
fun MoneyTrackerScreenPreview() {
    Money_TrackerTheme {
        MoneyTrackerScreen()
    }
}