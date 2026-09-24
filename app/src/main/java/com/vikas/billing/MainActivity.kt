package com.vikas.billing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vikas.billing.repository.FirebaseRepository
import com.vikas.billing.repository.AuthRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var authRepository: AuthRepository
    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authRepository = AuthRepository(this)
        firebaseRepository = FirebaseRepository()

        lifecycleScope.launch {
            val currentUser = authRepository.getCurrentUser()
            if (currentUser == null) {
                finish()
            }
        }
    }
}
