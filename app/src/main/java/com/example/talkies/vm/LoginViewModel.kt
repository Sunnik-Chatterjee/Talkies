package com.example.talkies.vm

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import com.example.talkies.data.model.PhoneAuthUser
import com.example.talkies.state.UiState
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) : ViewModel() {
    private val _authState = MutableStateFlow<UiState<PhoneAuthUser>>(UiState.Idle)
    val authState = _authState.asStateFlow()

    private val userRef = database.reference.child("users")

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {
        _authState.value = UiState.Loading
        val option = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(id: String, token: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(id, token)
                Log.d("PhoneAuth", "onCodeSent triggered. verification ID : $id")
                _authState.value = UiState.CodeSent(id)
            }

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithCredentials(credential, context = activity)
            }

            override fun onVerificationFailed(exception: FirebaseException) {
                Log.e("PhoneAuth", "Verification Failed: ${exception.message}")
                _authState.value = UiState.Failed(exception.message ?: "Verification failed")
            }
        }
        val phoneAuthOptions = PhoneAuthOptions.newBuilder(firebaseAuth).setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS).setActivity(activity).setCallbacks(option).build()

        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions)
    }

    private fun signInWithCredentials(credential: PhoneAuthCredential, context: Context) {
        _authState.value = UiState.Loading
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
            } else {
                _authState.value = UiState.Failed(task.exception?.message ?: "Sign In Failed")
            }
        }
    }

    private fun markUserAsSignedIn(context: Context) {
        val sharedPreference = context.getSharedPreferences("app_pref", Context.MODE_PRIVATE)
        sharedPreference.edit().putBoolean("isSignedIn", true).apply()
    }

    private fun fetchUserProfile(userId: String) {
        val userRef =
            userRef.child(userId) //Getting the detail of the user from real time db to my user ref
        userRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val userProfile = snapshot.getValue(PhoneAuthUser::class.java)
                if (userProfile != null) {
                    _authState.value = UiState.Success(userProfile)
                }
            }
        }.addOnFailureListener {
            _authState.value = UiState.Failed("Fail to fetch user profile")
        }
    }

    fun verifyCode(otp: String, context: Context) {
        val currentAuthState = _authState.value

        if (currentAuthState !is UiState.CodeSent || currentAuthState.verificationId.isEmpty()) {

            Log.e("PhoneAuth", "Attempting to verify OTP without a valid verification ID")
            _authState.value = UiState.Failed("Verification not started or Invalid Id")
            return
        }
        val credential = PhoneAuthProvider.getCredential(currentAuthState.verificationId, otp)
        signInWithCredentials(credential, context)
    }

    fun saveUserProfile(userId: String, name: String, status: String, profileImage: Bitmap?) {
        val database = FirebaseDatabase.getInstance().reference
        val encodedImage = profileImage?.let { convertBitmapToBase64(it) }
        val userProfile = PhoneAuthUser(
            userId = userId,
            name = name,
            status = status,
            phoneNumber = Firebase.auth.currentUser?.phoneNumber ?: "",
            profileImage = encodedImage
        )
        database.child("users").child(userId).setValue(userProfile)
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun resetAuthState() {
        _authState.value = UiState.Idle
    }

    fun signOut(activity: Activity) {
        firebaseAuth.signOut()
        val sharedPreference = activity.getSharedPreferences("app_pref", Activity.MODE_PRIVATE)
        sharedPreference.edit { putBoolean("isSigned", false) }
    }

}