package com.vikas.billing.utils

object Constants {
    const val FIREBASE_AUTH_COLLECTION = "users"
    const val FIREBASE_BILLS_COLLECTION = "bills"
    const val FIREBASE_INVENTORY_COLLECTION = "inventory"
    const val FIREBASE_SUBSCRIPTIONS_COLLECTION = "subscriptions"
    
    const val SHARED_PREF_NAME = "vikas_billing_prefs"
    const val USER_ID_KEY = "user_id"
    const val EMAIL_KEY = "email"
    const val SHOP_NAME_KEY = "shop_name"
    const val IS_PREMIUM_KEY = "is_premium"
    
    const val PLAN_FREE = "FREE"
    const val PLAN_BASIC = "BASIC"
    const val PLAN_PRO = "PRO"
    const val PLAN_ENTERPRISE = "ENTERPRISE"
    
    const val BASIC_PRICE = 99.0
    const val PRO_PRICE = 299.0
    const val ENTERPRISE_PRICE = 499.0
    
    const val PAYMENT_CASH = "CASH"
    const val PAYMENT_UPI = "UPI"
    const val PAYMENT_CARD = "CARD"
    const val PAYMENT_ONLINE = "ONLINE"
    
    const val DEFAULT_GST_RATE = 18.0
    
    const val LOG_TAG = "VIKAS_BILLING"
}
