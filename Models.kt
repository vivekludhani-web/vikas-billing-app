// app/src/main/java/com/vikas/billing/models/Models.kt

package com.vikas.billing.models

import com.google.firebase.Timestamp

// Bill Model
data class Bill(
    val billId: String = "",
    val billNumber: Int = 0,
    val userId: String = "",
    val date: Timestamp = Timestamp.now(),
    val items: List<BillItem> = emptyList(),
    val subtotal: Double = 0.0,
    val gstAmount: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "CASH", // CASH, UPI, CARD
    val notes: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val isPremium: Boolean = false,
    val status: String = "COMPLETED", // COMPLETED, PENDING, CANCELLED
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

// Bill Item Model
data class BillItem(
    val itemId: String = "",
    val itemCode: String = "",
    val itemName: String = "",
    val quantity: Int = 0,
    val unitPrice: Double = 0.0,
    val gstPercent: Double = 5.0, // 5%, 12%, 18%
    val discount: Double = 0.0, // percentage
    val amount: Double = 0.0, // quantity * unitPrice
    val gstAmount: Double = 0.0,
    val totalAmount: Double = 0.0
)

// Inventory Model
data class InventoryItem(
    val itemId: String = "",
    val itemCode: String = "",
    val itemName: String = "",
    val category: String = "", // SAREE, SUIT, FABRIC, etc.
    val quantity: Int = 0,
    val costPrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val reorderPoint: Int = 10,
    val supplier: String = "",
    val lastPurchaseDate: Timestamp = Timestamp.now(),
    val lastSaleDate: Timestamp? = null,
    val description: String = "",
    val imageUrl: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

// User/Shop Model
data class User(
    val userId: String = "",
    val email: String = "",
    val shopName: String = "VIKAS CLOTH HOUSE",
    val shopAddress: String = "",
    val gstNumber: String = "",
    val panNumber: String = "",
    val phoneNumber: String = "",
    val ownerName: String = "",
    val isPremium: Boolean = false,
    val premiumExpiry: Timestamp? = null,
    val subscriptionPlan: String = "FREE", // FREE, BASIC, PRO, ENTERPRISE
    val profilePhotoUrl: String = "",
    val lastBillNumber: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val totalBills: Int = 0,
    val totalRevenue: Double = 0.0
)

// Subscription Model
data class Subscription(
    val subscriptionId: String = "",
    val userId: String = "",
    val planName: String = "BASIC", // BASIC, PRO, ENTERPRISE
    val planPrice: Double = 99.0,
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    val autoRenew: Boolean = true,
    val paymentId: String = "", // Razorpay payment ID
    val status: String = "ACTIVE", // ACTIVE, EXPIRED, CANCELLED
    val paymentMethod: String = "RAZORPAY",
    val billingCycle: String = "MONTHLY" // MONTHLY, YEARLY
)

// Payment Model
data class Payment(
    val paymentId: String = "",
    val billId: String = "",
    val userId: String = "",
    val amount: Double = 0.0,
    val paymentMethod: String = "CASH", // CASH, UPI, CARD, RAZORPAY
    val transactionId: String = "",
    val status: String = "SUCCESS", // SUCCESS, FAILED, PENDING
    val date: Timestamp = Timestamp.now(),
    val notes: String = ""
)

// Report Model
data class DailyReport(
    val reportId: String = "",
    val userId: String = "",
    val date: String = "", // YYYY-MM-DD
    val totalBills: Int = 0,
    val totalItems: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalGST: Double = 0.0,
    val totalDiscount: Double = 0.0,
    val cashPayments: Double = 0.0,
    val upiPayments: Double = 0.0,
    val cardPayments: Double = 0.0,
    val netProfit: Double = 0.0,
    val createdAt: Timestamp = Timestamp.now()
)

// Analytics Model
data class Analytics(
    val analyticsId: String = "",
    val userId: String = "",
    val totalBills: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalGST: Double = 0.0,
    val averageBillAmount: Double = 0.0,
    val lastBillDate: Timestamp? = null,
    val lastLogin: Timestamp = Timestamp.now(),
    val totalItems: Int = 0,
    val lowStockItems: Int = 0,
    val topSellingItems: List<String> = emptyList(),
    val updatedAt: Timestamp = Timestamp.now()
)
