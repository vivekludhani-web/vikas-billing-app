// app/src/main/java/com/vikas/billing/repository/FirebaseRepository.kt

package com.vikas.billing.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.Timestamp
import com.vikas.billing.models.*
import com.vikas.billing.utils.Constants
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val db: FirebaseFirestore) {

    private val TAG = "FirebaseRepository"

    // ==================== USER OPERATIONS ====================

    suspend fun createUser(user: User): Boolean = try {
        db.collection(Constants.USERS_COLLECTION)
            .document(user.userId)
            .set(user)
            .await()
        Log.d(TAG, "User created: ${user.userId}")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error creating user", e)
        false
    }

    suspend fun getUser(userId: String): User? = try {
        db.collection(Constants.USERS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching user", e)
        null
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>): Boolean = try {
        db.collection(Constants.USERS_COLLECTION)
            .document(userId)
            .update(updates)
            .await()
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error updating user", e)
        false
    }

    // ==================== BILL OPERATIONS ====================

    suspend fun createBill(userId: String, bill: Bill): Boolean = try {
        val billRef = db.collection(Constants.BILLS_COLLECTION)
            .document(userId)
            .collection("userBills")
            .add(bill)
            .await()

        // Update user's lastBillNumber
        val billNumber = bill.billNumber
        updateUser(userId, mapOf("lastBillNumber" to billNumber))
        
        Log.d(TAG, "Bill created: ${billRef.id}")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error creating bill", e)
        false
    }

    suspend fun getBill(userId: String, billId: String): Bill? = try {
        db.collection(Constants.BILLS_COLLECTION)
            .document(userId)
            .collection("userBills")
            .document(billId)
            .get()
            .await()
            .toObject(Bill::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching bill", e)
        null
    }

    suspend fun getAllBills(userId: String): List<Bill> = try {
        db.collection(Constants.BILLS_COLLECTION)
            .document(userId)
            .collection("userBills")
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(100)
            .get()
            .await()
            .toObjects(Bill::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching bills", e)
        emptyList()
    }

    suspend fun getBillsByDate(userId: String, date: String): List<Bill> = try {
        db.collection(Constants.BILLS_COLLECTION)
            .document(userId)
            .collection("userBills")
            .whereEqualTo("date", date)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(Bill::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching bills by date", e)
        emptyList()
    }

    suspend fun deleteBill(userId: String, billId: String): Boolean = try {
        db.collection(Constants.BILLS_COLLECTION)
            .document(userId)
            .collection("userBills")
            .document(billId)
            .delete()
            .await()
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error deleting bill", e)
        false
    }

    // ==================== INVENTORY OPERATIONS ====================

    suspend fun addInventoryItem(userId: String, item: InventoryItem): Boolean = try {
        db.collection(Constants.INVENTORY_COLLECTION)
            .document(userId)
            .collection("items")
            .add(item)
            .await()
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error adding inventory item", e)
        false
    }

    suspend fun getInventoryItem(userId: String, itemId: String): InventoryItem? = try {
        db.collection(Constants.INVENTORY_COLLECTION)
            .document(userId)
            .collection("items")
            .document(itemId)
            .get()
            .await()
            .toObject(InventoryItem::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching inventory item", e)
        null
    }

    suspend fun getAllInventory(userId: String): List<InventoryItem> = try {
        db.collection(Constants.INVENTORY_COLLECTION)
            .document(userId)
            .collection("items")
            .orderBy("itemName")
            .get()
            .await()
            .toObjects(InventoryItem::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching inventory", e)
        emptyList()
    }

    suspend fun updateInventoryQuantity(userId: String, itemId: String, newQuantity: Int): Boolean = try {
        db.collection(Constants.INVENTORY_COLLECTION)
            .document(userId)
            .collection("items")
            .document(itemId)
            .update("quantity", newQuantity, "updatedAt", Timestamp.now())
            .await()
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error updating inventory quantity", e)
        false
    }

    suspend fun getLowStockItems(userId: String): List<InventoryItem> = try {
        db.collection(Constants.INVENTORY_COLLECTION)
            .document(userId)
            .collection("items")
            .get()
            .await()
            .toObjects(InventoryItem::class.java)
            .filter { it.quantity <= it.reorderPoint }
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching low stock items", e)
        emptyList()
    }

    suspend fun deleteInventoryItem(userId: String, itemId: String): Boolean = try {
        db.collection(Constants.INVENTORY_COLLECTION)
            .document(userId)
            .collection("items")
            .document(itemId)
            .delete()
            .await()
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error deleting inventory item", e)
        false
    }

    // ==================== SUBSCRIPTION OPERATIONS ====================

    suspend fun createSubscription(userId: String, subscription: Subscription): Boolean = try {
        db.collection(Constants.SUBSCRIPTIONS_COLLECTION)
            .document(userId)
            .set(subscription)
            .await()
        
        // Update user as premium
        updateUser(userId, mapOf(
            "isPremium" to true,
            "subscriptionPlan" to subscription.planName,
            "premiumExpiry" to subscription.endDate
        ))
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error creating subscription", e)
        false
    }

    suspend fun getSubscription(userId: String): Subscription? = try {
        db.collection(Constants.SUBSCRIPTIONS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(Subscription::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching subscription", e)
        null
    }

    suspend fun cancelSubscription(userId: String): Boolean = try {
        db.collection(Constants.SUBSCRIPTIONS_COLLECTION)
            .document(userId)
            .update("status", Constants.SUBSCRIPTION_CANCELLED)
            .await()
        
        updateUser(userId, mapOf("isPremium" to false))
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error cancelling subscription", e)
        false
    }

    // ==================== ANALYTICS OPERATIONS ====================

    suspend fun updateAnalytics(userId: String, analytics: Analytics): Boolean = try {
        db.collection(Constants.ANALYTICS_COLLECTION)
            .document(userId)
            .set(analytics)
            .await()
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error updating analytics", e)
        false
    }

    suspend fun getAnalytics(userId: String): Analytics? = try {
        db.collection(Constants.ANALYTICS_COLLECTION)
            .document(userId)
            .get()
            .await()
            .toObject(Analytics::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching analytics", e)
        null
    }

    // ==================== DAILY REPORT OPERATIONS ====================

    suspend fun createDailyReport(userId: String, report: DailyReport): Boolean = try {
        db.collection(Constants.DAILY_REPORTS_COLLECTION)
            .document(userId)
            .collection("reports")
            .add(report)
            .await()
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error creating daily report", e)
        false
    }

    suspend fun getDailyReport(userId: String, date: String): DailyReport? = try {
        db.collection(Constants.DAILY_REPORTS_COLLECTION)
            .document(userId)
            .collection("reports")
            .whereEqualTo("date", date)
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.toObject(DailyReport::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching daily report", e)
        null
    }

    suspend fun getMonthlyReports(userId: String, month: String): List<DailyReport> = try {
        db.collection(Constants.DAILY_REPORTS_COLLECTION)
            .document(userId)
            .collection("reports")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .await()
            .toObjects(DailyReport::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching monthly reports", e)
        emptyList()
    }

    // ==================== PAYMENT OPERATIONS ====================

    suspend fun recordPayment(userId: String, payment: Payment): Boolean = try {
        db.collection(Constants.PAYMENTS_COLLECTION)
            .document(userId)
            .collection("payments")
            .add(payment)
            .await()
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error recording payment", e)
        false
    }

    suspend fun getPaymentsByBill(userId: String, billId: String): List<Payment> = try {
        db.collection(Constants.PAYMENTS_COLLECTION)
            .document(userId)
            .collection("payments")
            .whereEqualTo("billId", billId)
            .get()
            .await()
            .toObjects(Payment::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching payments", e)
        emptyList()
    }
}
