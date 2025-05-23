package com.example.talkies.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.talkies.data.local.UserPref
import com.example.talkies.data.model.Channel
import com.example.talkies.data.model.ChatList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: FirebaseDatabase
) : ViewModel() {
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels = _channels.asStateFlow()

    init {
        getChannels()
    }

    private fun getChannels() {
        //Fetch the data from FireStore
        database.getReference("channels").get().addOnSuccessListener {
            val list = mutableListOf<Channel>()
            it.children.forEach { data ->
                val channel = Channel(data.key!!, data.value.toString())
                list.add(channel)
            }
            _channels.value = list
        }
    }

    fun addChannel(name: String){
        val key= database.getReference("channel").push().key
        database.getReference("channel").child(key!!).setValue(name).addOnSuccessListener {
            getChannels()
        }
    }
    fun searchUserByPhoneNumber(phoneNumber:String,callBack:(ChatList?) -> Unit){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser == null){
            Log.e("HomeViewModel","User is not authenticated")
            callBack(null)
            return
        }
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user = snapshot.children.first().getValue(ChatList::class.java)
                        callBack(user)
                    }else{
                        callBack(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeViewModel","Error fetching user: ${error.message}")
                    callBack(null)
                }
            })
    }
}