package com.vikas.billing.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.vikas.billing.MainActivity
import com.vikas.billing.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val googleSignInBtn = findViewById<Button>(R.id.google_sign_in)

        googleSignInBtn.setOnClickListener {
            // TODO: Implement Google Sign-In
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
