package com.example.talkies.vm

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.talkies.data.model.PhoneAuthUser
import com.example.talkies.state.UiState
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import androidx.core.content.edit
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import java.lang.IllegalArgumentException

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {
    // State Management
    private val _authState = MutableStateFlow<UiState<PhoneAuthUser>>(UiState.Idle)
    val authState = _authState.asStateFlow()

    private val _autoRetrievedOtp = MutableStateFlow("")
    val autoRetrievedOtp = _autoRetrievedOtp.asStateFlow()

    private var _verificationId = ""
    val verificationId get() = _verificationId

    private var _resendToken: PhoneAuthProvider.ForceResendingToken? = null
    private val userRef = database.reference.child("users")

    // OTP Handling
    fun onOtpEntered(otp: String) {
        if (otp.all { it.isDigit() }) {  // Only allow numeric input
            _autoRetrievedOtp.value = otp.take(6)  // Limit to 6 digits
        }
    }

    // Phone Verification
    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        _authState.value = UiState.Loading

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    _autoRetrievedOtp.value = credential.smsCode ?: ""
                    signInWithCredentials(credential, activity)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    _authState.value = UiState.Failed(
                        when (e) {
                            is FirebaseAuthInvalidCredentialsException -> "Invalid phone number"
                            is FirebaseTooManyRequestsException -> "Quota exceeded"
                            else -> "Verification failed: ${e.message}"
                        }
                    )
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    _verificationId = verificationId
                    _resendToken = token
                    _authState.value = UiState.CodeSent
                }
            })
            // Force reCAPTCHA flow if Play Integrity fails
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // OTP Verification
    fun verifyCode(otp: String, context: Context) {
        if (_verificationId.isEmpty()) {
            _authState.value = UiState.Failed("Verification not started")
            return
        }

        if (otp.length != 6) {
            _authState.value = UiState.Failed("Please enter 6-digit OTP")
            return
        }

        _authState.value = UiState.Loading
        try {
            val credential = PhoneAuthProvider.getCredential(_verificationId, otp)
            signInWithCredentials(credential, context)
        } catch (e: IllegalArgumentException) {
            _authState.value = UiState.Failed("Invalid verification code")
        }
    }

    // Authentication
    private fun signInWithCredentials(credential: PhoneAuthCredential, context: Context) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                val phoneAuthUser = PhoneAuthUser(
                    userId = user?.uid ?: "",
                    phoneNumber = user?.phoneNumber ?: ""
                )
                markUserAsSignedIn(context)
                _authState.value = UiState.Success(phoneAuthUser)
                fetchUserProfile(user?.uid ?: "")
                Log.d("LoginVM", "Authentication successful for ${user?.phoneNumber}")
            } else {
                Log.e("LoginVM", "Authentication failed", task.exception)
                _authState.value = UiState.Failed(
                    task.exception?.message ?: "Authentication failed"
                )
            }
        }
    }

    // User Management
    private fun markUserAsSignedIn(context: Context) {
        context.getSharedPreferences("app_pref", Context.MODE_PRIVATE).edit {
            putBoolean("isSignedIn", true)
        }
    }

    private fun fetchUserProfile(userId: String) {
        userRef.child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                snapshot.getValue(PhoneAuthUser::class.java)?.let { user ->
                    _authState.value = UiState.Success(user)
                }
            } else {
                // New user - create profile
                val newUser = PhoneAuthUser(
                    userId = userId,
                    phoneNumber = firebaseAuth.currentUser?.phoneNumber ?: ""
                )
                saveUserProfile(newUser)
            }
        }.addOnFailureListener {
            _authState.value = UiState.Failed("Failed to fetch user profile")
        }
    }

    fun saveUserProfile(user: PhoneAuthUser) {
        userRef.child(user.userId).setValue(user)
            .addOnSuccessListener {
                _authState.value = UiState.Success(user)
            }
            .addOnFailureListener {
                _authState.value = UiState.Failed("Failed to save profile")
            }
    }

    // Utility Functions
    fun resetAuthState() {
        _authState.value = UiState.Idle
        _autoRetrievedOtp.value = ""
    }

    fun signOut(activity: Activity) {
        firebaseAuth.signOut()
        activity.getSharedPreferences("app_pref", Activity.MODE_PRIVATE).edit {
            putBoolean("isSignedIn", false)
        }
        resetAuthState()
    }

    fun getCurrentUser(): PhoneAuthUser? {
        val user = firebaseAuth.currentUser
        return if (user != null) {
            PhoneAuthUser(
                userId = user.uid,
                phoneNumber = user.phoneNumber ?: ""
            )
        } else {
            null
        }
    }
}