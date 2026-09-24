package com.vikas.billing.models

data class User(
    val uid: String = "",
    val email: String = "",
    val shopName: String = "",
    val gstNumber: String = "",
    val isPremium: Boolean = false,
    val subscriptionPlan: String = "FREE",
    val lastBillNumber: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class Bill(
    val billId: String = "",
    val billNumber: Int = 0,
    val date: Long = System.currentTimeMillis(),
    val items: List<BillItem> = emptyList(),
    val subtotal: Double = 0.0,
    val gstRate: Double = 18.0,
    val gstAmount: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "CASH",
    val customerName: String = "",
    val customerPhone: String = ""
)

data class BillItem(
    val itemId: String = "",
    val itemName: String = "",
    val itemCode: String = "",
    val quantity: Int = 1,
    val rate: Double = 0.0,
    val amount: Double = 0.0
)

data class InventoryItem(
    val itemId: String = "",
    val itemName: String = "",
    val itemCode: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val costPrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val reorderPoint: Int = 10,
    val unit: String = "PCS"
)

data class Subscription(
    val subscriptionId: String = "",
    val userId: String = "",
    val planName: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val paymentId: String = "",
    val status: String = "ACTIVE",
    val amount: Double = 0.0
)

data class DailyReport(
    val reportId: String = "",
    val date: Long = System.currentTimeMillis(),
    val totalBills: Int = 0,
    val totalAmount: Double = 0.0,
    val totalGST: Double = 0.0,
    val paymentMethods: Map<String, Double> = emptyMap()
)
