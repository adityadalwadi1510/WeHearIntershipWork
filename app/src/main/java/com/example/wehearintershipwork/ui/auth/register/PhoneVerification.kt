package com.example.wehearintershipwork.ui.auth.register

import com.google.firebase.auth.PhoneAuthCredential

data class PhoneVerification(
    val phone: String,
    val credential: PhoneAuthCredential?,
    val isAutoVerified: Boolean
)