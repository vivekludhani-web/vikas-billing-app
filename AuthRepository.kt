// app/src/main/java/com/vikas/billing/repository/AuthRepository.kt

package com.vikas.billing.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.vikas.billing.models.User
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDb: FirebaseFirestore
) {

    private val TAG = "AuthRepository"

    // Get current user ID
    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    // Get current user email
    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    // Google Sign-In
    suspend fun googleSignIn(idToken: String): Boolean = try {
        val auth = firebaseAuth
        
        // This would be handled by GoogleSignInClient in the Activity
        // Here we just verify if user is authenticated
        val user = auth.currentUser
        
        if (user != null) {
            Log.d(TAG, "Google Sign-In successful: ${user.uid}")
            
            // Check if user exists in Firestore
            val userDoc = firebaseDb.collection("users")
                .document(user.uid)
                .get()
                .await()
            
            if (!userDoc.exists()) {
                // Create new user document
                val newUser = User(
                    userId = user.uid,
                    email = user.email ?: "",
                    shopName = "VIKAS CLOTH HOUSE",
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now(),
                    isPremium = false,
                    subscriptionPlan = "FREE"
                )
                
                firebaseDb.collection("users")
                    .document(user.uid)
                    .set(newUser)
                    .await()
                
                Log.d(TAG, "New user created in Firestore")
            }
            true
        } else {
            Log.e(TAG, "Google Sign-In failed: No user found")
            false
        }
    } catch (e: Exception) {
        Log.e(TAG, "Google Sign-In error", e)
        false
    }

    // Sign Out
    suspend fun signOut(): Boolean = try {
        firebaseAuth.signOut()
        Log.d(TAG, "Sign out successful")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Sign out error", e)
        false
    }

    // Get User Details
    suspend fun getUserDetails(): User? = try {
        val userId = getCurrentUserId() ?: return null
        
        firebaseDb.collection("users")
            .document(userId)
            .get()
            .await()
            .toObject(User::class.java)
    } catch (e: Exception) {
        Log.e(TAG, "Error fetching user details", e)
        null
    }

    // Update User Profile
    suspend fun updateUserProfile(
        shopName: String,
        gstNumber: String,
        panNumber: String,
        phoneNumber: String,
        ownerName: String,
        shopAddress: String
    ): Boolean = try {
        val userId = getCurrentUserId() ?: return false
        
        firebaseDb.collection("users")
            .document(userId)
            .update(
                "shopName" to shopName,
                "gstNumber" to gstNumber,
                "panNumber" to panNumber,
                "phoneNumber" to phoneNumber,
                "ownerName" to ownerName,
                "shopAddress" to shopAddress,
                "updatedAt" to Timestamp.now()
            )
            .await()
        
        Log.d(TAG, "User profile updated")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error updating user profile", e)
        false
    }

    // Check if Premium
    suspend fun isPremiumUser(): Boolean = try {
        val userId = getCurrentUserId() ?: return false
        
        val userDoc = firebaseDb.collection("users")
            .document(userId)
            .get()
            .await()
        
        val isPremium = userDoc.getBoolean("isPremium") ?: false
        Log.d(TAG, "Premium check: $isPremium")
        isPremium
    } catch (e: Exception) {
        Log.e(TAG, "Error checking premium status", e)
        false
    }

    // Get Premium Expiry Date
    suspend fun getPremiumExpiryDate(): com.google.firebase.Timestamp? = try {
        val userId = getCurrentUserId() ?: return null
        
        val userDoc = firebaseDb.collection("users")
            .document(userId)
            .get()
            .await()
        
        userDoc.getTimestamp("premiumExpiry")
    } catch (e: Exception) {
        Log.e(TAG, "Error getting premium expiry", e)
        null
    }

    // Get Last Bill Number
    suspend fun getLastBillNumber(): Int = try {
        val userId = getCurrentUserId() ?: return 0
        
        val userDoc = firebaseDb.collection("users")
            .document(userId)
            .get()
            .await()
        
        userDoc.getLong("lastBillNumber")?.toInt() ?: 0
    } catch (e: Exception) {
        Log.e(TAG, "Error getting last bill number", e)
        0
    }

    // Update Last Login
    suspend fun updateLastLogin(): Boolean = try {
        val userId = getCurrentUserId() ?: return false
        
        firebaseDb.collection("users")
            .document(userId)
            .update("lastLogin" to Timestamp.now())
            .await()
        
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error updating last login", e)
        false
    }

    // Delete Account
    suspend fun deleteAccount(): Boolean = try {
        val userId = getCurrentUserId() ?: return false
        val user = firebaseAuth.currentUser ?: return false
        
        // Delete user data from Firestore
        firebaseDb.collection("users")
            .document(userId)
            .delete()
            .await()
        
        // Delete Firebase Auth user
        user.delete().await()
        
        Log.d(TAG, "Account deleted successfully")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error deleting account", e)
        false
    }

    // Verify Email
    suspend fun sendEmailVerification(): Boolean = try {
        val user = firebaseAuth.currentUser ?: return false
        user.sendEmailVerification().await()
        Log.d(TAG, "Verification email sent")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Error sending verification email", e)
        false
    }

    // Check if Email Verified
    fun isEmailVerified(): Boolean {
        return firebaseAuth.currentUser?.isEmailVerified ?: false
    }

    // Get Shop Name
    suspend fun getShopName(): String = try {
        val userId = getCurrentUserId() ?: return "VIKAS CLOTH HOUSE"
        
        val userDoc = firebaseDb.collection("users")
            .document(userId)
            .get()
            .await()
        
        userDoc.getString("shopName") ?: "VIKAS CLOTH HOUSE"
    } catch (e: Exception) {
        Log.e(TAG, "Error getting shop name", e)
        "VIKAS CLOTH HOUSE"
    }

    // Get User Full Name
    suspend fun getUserFullName(): String = try {
        val userId = getCurrentUserId() ?: return "User"
        
        val userDoc = firebaseDb.collection("users")
            .document(userId)
            .get()
            .await()
        
        userDoc.getString("ownerName") ?: firebaseAuth.currentUser?.displayName ?: "User"
    } catch (e: Exception) {
        Log.e(TAG, "Error getting user name", e)
        "User"
    }
}
