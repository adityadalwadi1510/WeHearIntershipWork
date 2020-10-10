package com.example.wehearintershipwork.repository

import android.net.Uri
import com.example.wehearintershipwork.firebase.FirebaseAuthSource
import com.example.wehearintershipwork.firebase.FirestoreUserSource
import com.example.wehearintershipwork.model.User
import com.example.wehearintershipwork.util.FirebaseAuthError
import com.example.wehearintershipwork.util.Resource
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.lang.Exception


@ExperimentalCoroutinesApi
class AuthRepository
constructor(
    private val firebaseAuthSource: FirebaseAuthSource,
    private val firestoreUserSource: FirestoreUserSource
) {

    fun isUserSignIn() = firebaseAuthSource.isUserSignIn()

    fun signOut() = firebaseAuthSource.signOut()

    fun signInWithPhoneCredential(credential: PhoneAuthCredential): Flow<Resource<FirebaseUser>> =
        flow {
            emit(Resource.loading(null))

            try {
                // sign in the user
                val user = firebaseAuthSource.signInWithPhoneAuthCredential(credential)
                emit(Resource.success(user))
            } catch (e: FirebaseAuthException) {
                when (e.errorCode) {
                    // invalid otp code
                    // this is handle here because, after user submit the code
                    // exceptions is thrown here as phone credential is check at time of sign in
                    FirebaseAuthError.ERROR_INVALID_VERIFICATION_CODE.name -> {
                        emit(
                            Resource.error(
                                FirebaseAuthError.ERROR_INVALID_VERIFICATION_CODE.errorMsg,
                                null
                            )
                        )
                    }
                    // too many times requested to sign in by same phone number
                    FirebaseAuthError.ERROR_TOO_MANY_REQUESTS.name -> {
                        emit(
                            Resource.error(
                                FirebaseAuthError.ERROR_TOO_MANY_REQUESTS.errorMsg,
                                null
                            )
                        )
                    }
                    // otp code is expired or too old
                    FirebaseAuthError.ERROR_SESSION_EXPIRED.name -> {
                        emit(
                            Resource.error(
                                FirebaseAuthError.ERROR_SESSION_EXPIRED.errorMsg,
                                null
                            )
                        )
                    }
                    else -> {
                        emit(Resource.error(e.localizedMessage!!, null))
                    }
                }
            }
        }.flowOn(Dispatchers.IO)

    fun uploadImage(uri:Uri,name:String,user: User?):Flow<Resource<String>> =
        flow {
            emit(Resource.loading(null))
            try {
                val url=firestoreUserSource.uploadImageToStore(uri,name)
                if (user != null) {
                    firestoreUserSource.saveUser(user)
                }
                emit(Resource.success(url))
            }catch (e:Exception){
                emit(Resource.error(e.localizedMessage!!, null))
            }

        }.flowOn(Dispatchers.IO)



}