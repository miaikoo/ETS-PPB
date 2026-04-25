# Money Tracker App - MainActivity.kt Modules

## Overview

This Money Tracker application is built with Jetpack Compose and provides a comprehensive personal finance management system. The app allows users to track income and expenses, categorize transactions, visualize financial trends, and analyze spending patterns.

---

## Core Data Structures

### Transaction Data Class

```kotlin
data class Transaction(
    val name: String,           // Transaction name/description
    val type: String,           // "Pemasukan" (Income) or "Pengeluaran" (Expense)
    val amount: String,         // Transaction amount in Rupiah (formatted)
    val date: String,           // Date in format "dd MMMM yyyy"
    val category: String        // Transaction category
)
```

### Categories

- **Income Categories**: Gaji, Bonus, Investasi, Hibah, Penjualan, Lainnya
- **Expense Categories**: Makanan, Transportasi, Belanja, Hiburan, Pendidikan, Kesehatan, Tagihan, Rumah, Lainnya

### Color Palette

- `BgColor`: Dark background (#0F172A)
- `CardSurface`: Card background (#1E293B)
- `TextMuted`: Secondary text (#94A3B8)
- `IncomeGreen`: Income color (#10B981)
- `ExpenseRed`: Expense color (#F43F5E)

---

## Main Components

### 1. MainActivity

The entry point of the application. Extends `ComponentActivity` and initializes Compose UI with edge-to-edge display.

**Responsibilities:**

- Sets up the activity lifecycle
- Applies the Money Tracker theme
- Renders the main MoneyTrackerApp composable

---

### 2. MoneyTrackerApp

The main application container with bottom navigation between Home and Statistics screens.

**Features:**

- Navigation bar switching between "Home" and "Stats"
- Manages current screen state
- Provides sample transaction data for 4 months (January-April 2026)

---

### 3. MoneyTrackerHomeScreen

The primary home screen displaying user's financial dashboard and transaction list.

**Key Features:**

- **Wallet Card**: Shows total balance, income, and expenses
- **Transaction List**: Scrollable list of transactions with filtering
- **Filters**:
  - Transaction type (All, Income, Expense)
  - Month selection dropdown
- **Floating Action Button**: Opens dialog to add new transactions
- **Real-time Calculations**: Sums income/expense based on selected filters

**State Management:**

- `showAddDialog` / `showEditDialog`: Controls dialog visibility
- `selectedTransactionType`: Currently selected transaction type
- `filterType` / `filterMonth`: Active filters
- `selectedTransactionIndex` / `editingTransaction`: Editing context

---

### 4. AddTransactionDialog

Modal dialog for creating new transactions.

**Components:**

- Transaction type switcher (Income/Expense)
- Transaction name input field
- Amount input (numeric only)
- Date picker with calendar interface
- Category dropdown (dynamically changes based on type)
- Save/Cancel buttons

**Features:**

- Auto-formats amount with thousand separators
- Updates category options when type changes
- Validates input before saving

---

### 5. EditTransactionDialog

Modal dialog for modifying existing transactions.

**Additional Features vs AddDialog:**

- Pre-fills all fields with existing transaction data
- Delete button with confirmation warning
- Updates transaction in place
- Handles category validation when type changes

---

### 6. DatePickerDialog

Custom Material3 DatePicker wrapped in a dark-themed dialog.

**Features:**

- Dark theme matching app design
- Customizable accent colors
- Integrates with Transaction date selection
- Returns selected date as `java.util.Date`

---

### 7. TransactionItem

Individual transaction list item showing transaction details in a row.

**Displays:**

- **Icon**: Down arrow (income) or up arrow (expense) with appropriate color
- **Details**: Transaction name, category, and date
- **Amount**: Formatted with currency symbol and +/- indicator
- **Color Coding**: Green for income, red for expense
- **Interaction**: Clickable to open edit dialog

---

### 8. InfoColumn

Small composable displaying a labeled amount with icon in the wallet card.

**Parameters:**

- `label`: Display label (e.g., "Pemasukan")
- `amount`: Formatted amount string
- `color`: Color for text and icon
- `icon`: Material icon to display
- `modifier`: Layout modifier
- `align`: Horizontal alignment (Start or End)

---

### 9. StatisticsScreen

Comprehensive analytics and financial analysis screen.

**Sections:**

#### Financial Trend Section

- **Line Chart**: Shows net balance (income - expense) over months
- **Features**:
  - Color-coded segments (green for increases, red for decreases)
  - Y-axis labels with formatted amounts
  - Month labels on X-axis
  - Requires minimum 2 months of data

#### Category Analysis Section

- **Donut Chart**: Distribution of income/expense by category
- **Features**:
  - Transaction type switcher
  - Month filter dropdown
  - Interactive legend showing category amounts
  - Color-coded categories

---

### 10. DonutChartWithLegend

Displays category spending/income distribution as a donut chart.

**Features:**

- **Center Display**: Total amount in rupiah
- **Chart**: Animated donut segments with 6 different colors
- **Legend**: Category names with their respective amounts
- **Responsive**: Adjusts to data size

---

### 11. LineChart

Visualizes monthly financial trends as a line graph.

**Components:**

- **Y-Axis**: Shows min, mid, and max values
- **Grid Lines**: Horizontal reference lines for readability
- **Data Points**: Connected line segments with colored circles
- **Zero Line**: Highlighted line at balance = 0
- **X-Axis**: Month labels at bottom

**Color Logic:**

- Green lines/points for months with positive net balance
- Red lines/points for months with negative net balance

---

### 12. EmptyStateMessage

Simple centered text message displayed when no data is available.

**Usage:**

- Shown when no transactions exist for selected filters
- Shown when insufficient data for trend visualization

---

## Key Utility Functions

### `Int.format(): String`

Formats integer values with thousand separators.

- Example: `8000000.format()` → `"8,000,000"`

---

## Data Flow

1. **Transaction Creation**
   - User clicks FAB → AddTransactionDialog opens
   - Fills details and clicks Save → Transaction added to list
   - HomeScreen automatically updates with new transaction

2. **Transaction Editing**
   - User clicks TransactionItem → EditTransactionDialog opens
   - Modifies details and clicks Update → Transaction replaced in list
   - Or clicks Delete → Transaction removed after confirmation

3. **Filtering**
   - User selects type filter → displayedTransactions list updates
   - User selects month → displayedTransactions filtered by date range
   - Both filters work together (AND logic)

4. **Statistics**
   - App calculates monthly trends by grouping transactions by month
   - Categories grouped by their type and calculated total per category
   - Charts render based on filtered data

---

## Theme & Styling

The app uses a dark theme with custom colors for a modern finance app aesthetic:

- Dark blue/gray background for reduced eye strain
- Emerald green for positive (income) values
- Rose red for negative (expense) values
- Consistent rounded corners (12-28dp) throughout
- Semi-transparent overlays for better layering
