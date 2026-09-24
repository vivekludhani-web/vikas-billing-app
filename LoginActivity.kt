// app/src/main/java/com/vikas/billing/ui/activities/LoginActivity.kt

package com.vikas.billing.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.vikas.billing.R
import com.vikas.billing.models.User
import com.vikas.billing.utils.Constants
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDb: FirebaseFirestore

    private lateinit var btnGoogleLogin: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvAppName: TextView

    private val RC_SIGN_IN = 9001
    private val TAG = "LoginActivity"
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        initFirebase()
        initGoogleSignIn()

        // Check if user already logged in
        if (firebaseAuth.currentUser != null) {
            navigateToDashboard()
        }

        btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun initViews() {
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        progressBar = findViewById(R.id.progressBar)
        tvAppName = findViewById(R.id.tvAppName)

        progressBar.visibility = android.view.View.GONE
    }

    private fun initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDb = FirebaseFirestore.getInstance()
    }

    private fun initGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    Log.d(TAG, "Google Sign-In successful: ${account.id}")
                    firebaseAuthWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(this, "Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        showProgress(true)

        scope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential).await()

                val user = firebaseAuth.currentUser
                if (user != null) {
                    Log.d(TAG, "Firebase Auth successful: ${user.uid}")

                    // Check if user exists in Firestore
                    val userDoc = firebaseDb.collection(Constants.USERS_COLLECTION)
                        .document(user.uid)
                        .get()
                        .await()

                    if (!userDoc.exists()) {
                        // Create new user document
                        createNewUser(user.uid, user.email ?: "")
                    } else {
                        // Update last login
                        updateLastLogin(user.uid)
                    }

                    navigateToDashboard()
                } else {
                    showError("Authentication failed")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Auth error", e)
                showError("Login failed: ${e.message}")
            } finally {
                showProgress(false)
            }
        }
    }

    private suspend fun createNewUser(userId: String, email: String) {
        try {
            val newUser = User(
                userId = userId,
                email = email,
                shopName = "VIKAS CLOTH HOUSE",
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                isPremium = false,
                subscriptionPlan = Constants.PLAN_FREE,
                lastBillNumber = 0,
                totalBills = 0,
                totalRevenue = 0.0
            )

            firebaseDb.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .set(newUser)
                .await()

            Log.d(TAG, "New user created in Firestore")
            Toast.makeText(this, "Welcome to VIKAS Billing App!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user", e)
        }
    }

    private suspend fun updateLastLogin(userId: String) {
        try {
            firebaseDb.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .update("updatedAt" to Timestamp.now())
                .await()

            Log.d(TAG, "Last login updated")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating last login", e)
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showProgress(show: Boolean) {
        progressBar.visibility = if (show) android.view.View.VISIBLE else android.view.View.GONE
        btnGoogleLogin.isEnabled = !show
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.e(TAG, message)
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
