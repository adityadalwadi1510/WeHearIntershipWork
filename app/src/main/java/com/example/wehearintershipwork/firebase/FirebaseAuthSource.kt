package com.example.wehearintershipwork.firebase

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource {

    private val firebaseAuth:FirebaseAuth by lazy {
        Firebase.auth
    }


    fun currentUser()=firebaseAuth.currentUser


    fun isUserSignIn()=currentUser()!=null

    fun signOut()=firebaseAuth.signOut()

    private fun profileChangeRequest(name: String) = userProfileChangeRequest {
        displayName = name
    }

    suspend fun updateName(name: String) {
        currentUser()?.updateProfile(profileChangeRequest(name))?.await()
    }

    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): FirebaseUser? {
        return firebaseAuth.signInWithCredential(credential).await().user
    }

    suspend fun linkWithCredential(credential: AuthCredential): FirebaseUser? {
        return currentUser()?.linkWithCredential(credential)?.await()?.user
    }




}