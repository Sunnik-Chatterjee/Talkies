package com.example.talkies.vm

import androidx.lifecycle.ViewModel
import com.example.talkies.data.local.UserPref
import com.example.talkies.data.model.Channel
import com.google.firebase.database.FirebaseDatabase
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
}