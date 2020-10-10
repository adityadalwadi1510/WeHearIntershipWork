package com.example.wehearintershipwork.ui.auth.register

import android.net.Uri
import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.wehearintershipwork.model.User
import com.example.wehearintershipwork.repository.AuthRepository
import com.example.wehearintershipwork.util.FirebaseAuthError
import com.example.wehearintershipwork.util.Resource
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
class RegisterViewModel
@ViewModelInject
constructor(
    private val authRepository: AuthRepository

) : ViewModel() {

    companion object {
        private const val AUTO_RETRIEVAL_TIMEOUT_SECONDS = 60L
        private const val VERIFICATION_ID_KEY = "verification_id"

        const val OTP_SENT_MSG = "OTP code sent successfully"
        private lateinit var mutableLiveData: MutableLiveData<String>
    }

    var shouldNavigateToOtp: Boolean = false

    var bithDateString: String = ""
    var ImagePath: String = ""

    // save the details entered by user in this object
    var user: User? = null

    // variable for phone authentication
    private lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private var verificationId: String? = null

    // live data for observing the process of phone authentication
    // from sending the otp to verify it all the process will observe in this liveData
    private val _phoneVerification = MutableLiveData<Resource<PhoneVerification>>()
    val phoneVerification: LiveData<Resource<PhoneVerification>>
        get() = _phoneVerification

    // jobs for managing network calls in coroutines
    private var signInWithPhoneAuthJob = Job()
    private var uploadImage = Job()


    fun sendVerificationCode(phone: String, force: Boolean) {
        val actualPhone = if (!phone.startsWith("+91")) "+91 $phone" else phone
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            actualPhone,
            AUTO_RETRIEVAL_TIMEOUT_SECONDS,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    // verification is automatically completed
                    // either by instant verification or code is automatically detected
                    _phoneVerification.value =
                        Resource.success(PhoneVerification(phone, phoneAuthCredential, true))
                }

                override fun onVerificationFailed(firebaseException: FirebaseException) {
                    when (firebaseException) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            // Invalid Request
                            val error = firebaseException.localizedMessage
                                ?: FirebaseAuthError.ERROR_INVALID_PHONE_NUMBER.errorMsg
                            _phoneVerification.value =
                                Resource.error<PhoneVerification>(error, null)
                        }
                        is FirebaseTooManyRequestsException -> {
                            // Too Many Request
                            // The SMS quota for the project has been exceeded
                            val error = firebaseException.localizedMessage
                                ?: FirebaseAuthError.ERROR_QUOTA_EXCEEDED.errorMsg
                            _phoneVerification.value =
                                Resource.error<PhoneVerification>(error, null)
                        }
                        else -> {
                            val error =
                                firebaseException.localizedMessage
                                    ?: FirebaseAuthError.ERROR_UNKNOWN.errorMsg
                            _phoneVerification.value =
                                Resource.error<PhoneVerification>(error, null)
                        }
                    }
                }

                override fun onCodeSent(vId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(vId, token)
                    verificationId = vId
                    resendToken = token
                    _phoneVerification.value = Resource.error(
                        OTP_SENT_MSG,
                        PhoneVerification(phone, null, false)
                    )
                }
            },
            if (force) resendToken else null
        )
    }


    fun submitVerificationCode(phone: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        _phoneVerification.value = Resource.success(PhoneVerification(phone, credential, false))
    }

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): LiveData<Resource<FirebaseUser>> {
        signInWithPhoneAuthJob.cancel()
        signInWithPhoneAuthJob = Job()
        // sign out the user if he/she cancel the registration process in midway
        signInWithPhoneAuthJob.invokeOnCompletion {
            // if it is not null that means some error occurs(like network, etc.)
            // or job is cancelled (when user lives from the fragment)
            it?.let {
                if (authRepository.isUserSignIn()) {
                    authRepository.signOut()
                }
            }
        }

        return authRepository.signInWithPhoneCredential(credential)
            .asLiveData(viewModelScope.coroutineContext + signInWithPhoneAuthJob)
    }

    //Upload Image
    fun uploadImage(number: String): LiveData<Resource<String>> {
        uploadImage.cancel()
        uploadImage = Job()
        uploadImage.invokeOnCompletion {
            it?.let {

            }
        }
        return authRepository.uploadImage(Uri.parse(getProfileImagePath()), number, user)
            .asLiveData(viewModelScope.coroutineContext + uploadImage)
    }

    /**
     * save the verification id to bundle
     */
    fun onSaveStateInstance(outState: Bundle) {
        outState.putString(VERIFICATION_ID_KEY, verificationId)
    }

    /**
     * get the verification id from bundle if present
     */
    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (verificationId == null && savedInstanceState != null) {
            verificationId = savedInstanceState.getString(VERIFICATION_ID_KEY).toString()
        }
    }

    fun getDateString(): String {
        return bithDateString
    }

    fun setDateString(birthDate: String) {
        this.bithDateString = birthDate
    }


    fun getProfileImagePath(): String {
        return ImagePath
    }

    fun setProfileImagePath(profileImagePath: String) {
        this.ImagePath = profileImagePath
    }

}