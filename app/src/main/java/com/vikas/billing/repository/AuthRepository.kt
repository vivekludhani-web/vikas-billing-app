package com.vikas.billing.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.vikas.billing.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID_FROM_GOOGLE_SERVICES_JSON")
            .requestEmail()
            .build()
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> = withContext(Dispatchers.IO) {
        try {
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
            
            if (firebaseUser != null) {
                val user = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    createdAt = System.currentTimeMillis()
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.signOut()
            GoogleSignIn.getClient(context, getGoogleSignInOptions()).signOut().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun isLoggedIn() = firebaseAuth.currentUser != null
}
