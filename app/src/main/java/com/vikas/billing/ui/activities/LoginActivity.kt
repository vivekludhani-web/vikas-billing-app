package com.vikas.billing.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.vikas.billing.R
import com.vikas.billing.repository.AuthRepository
import com.vikas.billing.repository.FirebaseRepository
import com.vikas.billing.utils.Constants
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var authRepository: AuthRepository
    private lateinit var firebaseRepository: FirebaseRepository
    
    private lateinit var googleSignInBtn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView

    private val RC_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authRepository = AuthRepository(this)
        firebaseRepository = FirebaseRepository()

        googleSignInBtn = findViewById(R.id.google_sign_in_btn)
        progressBar = findViewById(R.id.progress_bar)
        statusText = findViewById(R.id.status_text)

        googleSignInClient = GoogleSignIn.getClient(this, authRepository.getGoogleSignInOptions())

        googleSignInBtn.setOnClickListener {
            signInWithGoogle()
        }

        if (authRepository.isLoggedIn()) {
            navigateToMain()
        }
    }

    private fun signInWithGoogle() {
        googleSignInBtn.isEnabled = false
        progressBar.visibility = android.view.View.VISIBLE
        statusText.text = "Signing in..."

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
                    lifecycleScope.launch {
                        val result = authRepository.signInWithGoogle(account)
                        result.onSuccess { user ->
                            firebaseRepository.createUser(user.uid, user).also { userResult ->
                                userResult.onSuccess {
                                    Log.d(Constants.LOG_TAG, "User created in Firestore")
                                    navigateToMain()
                                }
                                userResult.onFailure { error ->
                                    Log.e(Constants.LOG_TAG, "Error saving user: ${error.message}")
                                    statusText.text = "Error: ${error.message}"
                                }
                            }
                        }
                        result.onFailure { error ->
                            Log.e(Constants.LOG_TAG, "Sign in failed: ${error.message}")
                            statusText.text = "Sign in failed: ${error.message}"
                            googleSignInBtn.isEnabled = true
                            progressBar.visibility = android.view.View.GONE
                        }
                    }
                }
            } catch (e: ApiException) {
                Log.e(Constants.LOG_TAG, "signInResult:failed code= " + e.statusCode)
                statusText.text = "Sign in failed: ${e.message}"
                googleSignInBtn.isEnabled = true
                progressBar.visibility = android.view.View.GONE
            }
        }
    }

    private fun navigateToMain() {
        Toast.makeText(this, "Welcome to VIKAS Billing!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
