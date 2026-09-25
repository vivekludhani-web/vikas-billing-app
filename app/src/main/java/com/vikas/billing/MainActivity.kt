package com.vikas.billing

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val googleLoginBtn = findViewById<Button>(R.id.google_login_btn)
        val createBillBtn = findViewById<Button>(R.id.create_bill_btn)
        val logoutBtn = findViewById<Button>(R.id.logout_btn)
        val loginStatus = findViewById<TextView>(R.id.login_status)

        googleLoginBtn.setOnClickListener {
            loginStatus.text = "Google Login - Coming Soon"
        }

        createBillBtn.setOnClickListener {
            loginStatus.text = "Create Bill - Coming Soon"
        }

        logoutBtn.setOnClickListener {
            loginStatus.text = "Logged Out"
        }
    }
}
