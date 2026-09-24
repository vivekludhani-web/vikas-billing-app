package com.vikas.billing.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.vikas.billing.models.Bill
import com.vikas.billing.models.InventoryItem
import com.vikas.billing.models.Subscription
import com.vikas.billing.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun createUser(userId: String, user: User) = withContext(Dispatchers.IO) {
        try {
            db.collection("users").document(userId).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(userId: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val doc = db.collection("users").document(userId).get().await()
            val user = doc.toObject(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveBill(userId: String, bill: Bill) = withContext(Dispatchers.IO) {
        try {
            db.collection("bills").document(userId).collection("userBills")
                .add(bill).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBills(userId: String): Result<List<Bill>> = withContext(Dispatchers.IO) {
        try {
            val docs = db.collection("bills").document(userId).collection("userBills")
                .get().await()
            val bills = docs.toObjects(Bill::class.java)
            Result.success(bills)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveInventoryItem(userId: String, item: InventoryItem) = withContext(Dispatchers.IO) {
        try {
            db.collection("inventory").document(userId).collection("items")
                .add(item).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateSubscription(userId: String, subscription: Subscription) = withContext(Dispatchers.IO) {
        try {
            db.collection("subscriptions").document(userId).set(subscription).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSubscription(userId: String): Result<Subscription> = withContext(Dispatchers.IO) {
        try {
            val doc = db.collection("subscriptions").document(userId).get().await()
            val subscription = doc.toObject(Subscription::class.java)
            if (subscription != null) {
                Result.success(subscription)
            } else {
                Result.failure(Exception("Subscription not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
