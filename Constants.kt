// app/src/main/java/com/vikas/billing/utils/Constants.kt

package com.vikas.billing.utils

object Constants {
    // Firebase Configuration
    const val FIREBASE_PROJECT_ID = "vikas-billing-app"
    
    // Collections
    const val USERS_COLLECTION = "users"
    const val BILLS_COLLECTION = "bills"
    const val INVENTORY_COLLECTION = "inventory"
    const val SUBSCRIPTIONS_COLLECTION = "subscriptions"
    const val PAYMENTS_COLLECTION = "payments"
    const val ANALYTICS_COLLECTION = "analytics"
    const val DAILY_REPORTS_COLLECTION = "daily_reports"

    // Google Sign-In
    const val GOOGLE_CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID.apps.googleusercontent.com"
    
    // AdMob Configuration
    const val ADMOB_APP_ID = "ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"
    const val ADMOB_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111" // Test ID
    const val ADMOB_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // Test ID
    const val ADMOB_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917" // Test ID

    // Razorpay Configuration
    const val RAZORPAY_KEY_ID = "YOUR_RAZORPAY_KEY_ID"
    const val RAZORPAY_KEY_SECRET = "YOUR_RAZORPAY_KEY_SECRET"

    // Subscription Plans
    const val PLAN_FREE = "FREE"
    const val PLAN_BASIC = "BASIC"
    const val PLAN_PRO = "PRO"
    const val PLAN_ENTERPRISE = "ENTERPRISE"

    // Subscription Prices (in INR)
    const val PRICE_BASIC = 99.0
    const val PRICE_PRO = 299.0
    const val PRICE_ENTERPRISE = 499.0

    // GST Rates (in percentage)
    const val GST_RATE_5 = 5.0
    const val GST_RATE_12 = 12.0
    const val GST_RATE_18 = 18.0

    // Payment Methods
    const val PAYMENT_CASH = "CASH"
    const val PAYMENT_UPI = "UPI"
    const val PAYMENT_CARD = "CARD"
    const val PAYMENT_RAZORPAY = "RAZORPAY"

    // Bill Status
    const val BILL_STATUS_COMPLETED = "COMPLETED"
    const val BILL_STATUS_PENDING = "PENDING"
    const val BILL_STATUS_CANCELLED = "CANCELLED"

    // Subscription Status
    const val SUBSCRIPTION_ACTIVE = "ACTIVE"
    const val SUBSCRIPTION_EXPIRED = "EXPIRED"
    const val SUBSCRIPTION_CANCELLED = "CANCELLED"

    // Shared Preferences
    const val SHARED_PREF_NAME = "vikas_billing_prefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
    const val PREF_USER_EMAIL = "user_email"
    const val PREF_SHOP_NAME = "shop_name"
    const val PREF_IS_PREMIUM = "is_premium"
    const val PREF_LAST_BILL_NUMBER = "last_bill_number"

    // Intent Extras
    const val EXTRA_BILL_ID = "bill_id"
    const val EXTRA_ITEM_ID = "item_id"
    const val EXTRA_MODE = "mode" // CREATE, EDIT, VIEW

    // API Timeouts
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Database
    const val DATABASE_NAME = "vikas_billing_db"

    // Date Format
    const val DATE_FORMAT_PATTERN = "dd/MM/yyyy"
    const val TIME_FORMAT_PATTERN = "HH:mm:ss"
    const val DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss"

    // Limits
    const val MAX_ITEMS_PER_BILL = 50
    const val MAX_BILLS_FETCH = 100
    const val MIN_BILL_AMOUNT = 1.0
    const val MAX_DISCOUNT_PERCENT = 100.0

    // Features
    const val FEATURE_INVENTORY_MANAGEMENT = "inventory_management"
    const val FEATURE_ADVANCED_REPORTS = "advanced_reports"
    const val FEATURE_MULTI_USER = "multi_user"
    const val FEATURE_BACKUP_RESTORE = "backup_restore"
    const val FEATURE_CUSTOMER_DATABASE = "customer_database"
    const val FEATURE_STOCK_ALERTS = "stock_alerts"

    // Error Messages
    const val ERROR_NETWORK = "Network error. Please check your internet connection."
    const val ERROR_AUTH = "Authentication failed. Please login again."
    const val ERROR_FIREBASE = "Database error. Please try again."
    const val ERROR_PAYMENT = "Payment failed. Please try again."
    const val ERROR_INVALID_INPUT = "Please fill all required fields."

    // Success Messages
    const val SUCCESS_BILL_CREATED = "Bill created successfully!"
    const val SUCCESS_ITEM_ADDED = "Item added successfully!"
    const val SUCCESS_SUBSCRIPTION = "Subscription activated!"
}

// GST Calculator
object GSTCalculator {
    
    fun calculateGST(amount: Double, gstPercent: Double): Double {
        return (amount * gstPercent) / 100
    }

    fun calculateTotal(amount: Double, gstPercent: Double): Double {
        val gst = calculateGST(amount, gstPercent)
        return amount + gst
    }

    fun calculateWithDiscount(
        amount: Double,
        discountPercent: Double,
        gstPercent: Double
    ): Pair<Double, Double> {
        val discountAmount = (amount * discountPercent) / 100
        val afterDiscount = amount - discountAmount
        val gstAmount = calculateGST(afterDiscount, gstPercent)
        val total = afterDiscount + gstAmount
        return Pair(gstAmount, total)
    }

    fun calculateBillTotal(
        subtotal: Double,
        gstPercent: Double,
        discountPercent: Double = 0.0
    ): Triple<Double, Double, Double> {
        // subtotal after discount
        val discount = (subtotal * discountPercent) / 100
        val afterDiscount = subtotal - discount
        
        // GST on discounted amount
        val gst = (afterDiscount * gstPercent) / 100
        
        // Total
        val total = afterDiscount + gst
        
        return Triple(discount, gst, total)
    }
}

// Premium Feature Checker
object PremiumFeatureChecker {
    
    fun isFeatureAvailable(isPremium: Boolean, feature: String): Boolean {
        return when {
            isPremium -> true
            feature == Constants.FEATURE_INVENTORY_MANAGEMENT -> false
            feature == Constants.FEATURE_ADVANCED_REPORTS -> false
            feature == Constants.FEATURE_MULTI_USER -> false
            feature == Constants.FEATURE_BACKUP_RESTORE -> false
            feature == Constants.FEATURE_CUSTOMER_DATABASE -> false
            feature == Constants.FEATURE_STOCK_ALERTS -> false
            else -> true
        }
    }

    fun getFeaturesForPlan(plan: String): List<String> {
        return when (plan) {
            Constants.PLAN_FREE -> listOf(
                "Basic Billing",
                "Simple Reports"
            )
            Constants.PLAN_BASIC -> listOf(
                "Basic Billing",
                "Simple Reports",
                Constants.FEATURE_INVENTORY_MANAGEMENT,
                Constants.FEATURE_STOCK_ALERTS
            )
            Constants.PLAN_PRO -> listOf(
                "Basic Billing",
                "Simple Reports",
                Constants.FEATURE_INVENTORY_MANAGEMENT,
                Constants.FEATURE_STOCK_ALERTS,
                Constants.FEATURE_ADVANCED_REPORTS,
                Constants.FEATURE_MULTI_USER,
                Constants.FEATURE_CUSTOMER_DATABASE
            )
            Constants.PLAN_ENTERPRISE -> listOf(
                "Basic Billing",
                "Simple Reports",
                Constants.FEATURE_INVENTORY_MANAGEMENT,
                Constants.FEATURE_STOCK_ALERTS,
                Constants.FEATURE_ADVANCED_REPORTS,
                Constants.FEATURE_MULTI_USER,
                Constants.FEATURE_CUSTOMER_DATABASE,
                Constants.FEATURE_BACKUP_RESTORE
            )
            else -> emptyList()
        }
    }
}
