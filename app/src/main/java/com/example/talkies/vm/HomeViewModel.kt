package com.example.talkies.vm

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.talkies.data.model.ChatList
import com.example.talkies.data.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okio.IOException
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val database: FirebaseDatabase
) : ViewModel() {
    private val _chatList = MutableStateFlow<List<ChatList>>(emptyList())
    val chatList = _chatList.asStateFlow()
    private val databaseReference = FirebaseDatabase.getInstance().reference

    init {
        loadChatData()
    }

    fun loadChatList(
        currentUserPhoneNumber: String,
        onChatLoaded:(List<ChatList>)->Unit
    ){
        val chatList = mutableListOf<ChatList>()
        val chatRef = databaseReference.child("chats").child(currentUserPhoneNumber)
        chatRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    snapshot.children.forEach {child->
                        val phoneNumber = child.key?:return@forEach
                        val name = child.child("name").value as? String?:"Unknown"
                        val image = child.child("image").value as? String
                        val profileImageBitmap = image?.let {decodeBase64toBitmap(it)}
                        fetchLastMessageForChat (currentUserPhoneNumber,phoneNumber){lastMessage,time->
                                chatList.add(
                                    ChatList(
                                        name = name,
                                        image = profileImageBitmap as Int?,
                                        message = lastMessage,
                                        time = time
                                    )
                                )
                            if(chatList.size == snapshot.childrenCount.toInt()){
                                onChatLoaded(chatList)
                            }
                        }
                    }
                }else{
                    onChatLoaded(emptyList())
                }
            }
            override fun onCancelled(p0: DatabaseError) {
                onChatLoaded(emptyList())
            }
        })
    }

    private fun decodeBase64toBitmap(base64Image: String) : Bitmap?{
        return try{
            val decodeByte = Base64.decode(base64Image, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodeByte,0,decodeByte.size)
        }catch (e: IOException){
            null
        }
    }
    fun base64ToBitmap(base64String: String): Bitmap?{
        return try {
            val decodeByte = Base64.decode(base64String, Base64.DEFAULT)
            val inputStream : InputStream = ByteArrayInputStream(decodeByte)
            BitmapFactory.decodeStream(inputStream)
        }catch (e: IOException){
            null
        }
    }

    fun sendMessage(senderPhoneNumber: String, receiverPhoneNumber: String, messageText: String) {
        val messageId = databaseReference.push().key ?: return
        val message = Message(
            sendersPhoneNumber = senderPhoneNumber,
            message = messageText,
            timeStamp = System.currentTimeMillis()
        )
        //Senders database reference
        databaseReference.child("messages").child(senderPhoneNumber).child(receiverPhoneNumber)
            .child(messageId).setValue(message)

        //Receivers database reference
        databaseReference.child("messages").child(receiverPhoneNumber).child(senderPhoneNumber)
            .child(messageId).setValue(message)
    }

    fun getMessage(
        senderPhoneNumber: String,
        receiverPhoneNumber: String,
        OnNewMessage: (Message) -> Unit
    ) {
        val messageRef = databaseReference.child("messages")
            .child(senderPhoneNumber)
            .child(receiverPhoneNumber)

        messageRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)

                if (message != null) {
                    OnNewMessage(message)
                }
            }

            override fun onChildChanged(
                p0: DataSnapshot,
                p1: String?
            ) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onChildMoved(
                p0: DataSnapshot,
                p1: String?
            ) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun fetchLastMessageForChat(
        senderPhoneNumber: String,
        receiverPhoneNumber: String,
        onLastMessageFetched: (String, String) -> Unit
    ) {
        val chatRef =
            databaseReference.child("messages")
                .child(senderPhoneNumber)
                .child(receiverPhoneNumber)
        chatRef.orderByChild("timestamp").limitToLast(1)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val lastMessage =
                            snapshot.children.firstOrNull()?.child("message")?.value as? String
                        val timeStamp =
                            snapshot.children.firstOrNull()?.child("timeStamp")?.value as? String
                        onLastMessageFetched(lastMessage?:"No message",timeStamp?:"--:--")
                    }else{
                        onLastMessageFetched("No message","--:--")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onLastMessageFetched("No message","--:--")
                }

            })
    }


    fun searchUserByPhoneNumber(phoneNumber: String, callBack: (ChatList?) -> Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Log.e("HomeViewModel", "User is not authenticated")
            callBack(null)
            return
        }
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        databaseReference.orderByChild("phoneNumber").equalTo(phoneNumber)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val user = snapshot.children.first().getValue(ChatList::class.java)
                        callBack(user)
                    } else {
                        callBack(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeViewModel", "Error fetching user: ${error.message}")
                    callBack(null)
                }
            })
    }

    fun getChatForUser(userId: String, callBack: (List<ChatList>) -> Unit) {
        val chatRef = FirebaseDatabase.getInstance().getReference("users/${userId}/chats")
        chatRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = mutableListOf<ChatList>()
                for (childSnapshot in snapshot.children) {
                    val chat = childSnapshot.getValue(ChatList::class.java)
                    if (chat != null) {
                        chatList.add(chat)
                    }
                }
                callBack(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeViewModel", "Error fetching user chat: ${error.message}")
                callBack(emptyList())
            }
        })
    }

    private fun loadChatData() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val chatRef = FirebaseDatabase.getInstance().getReference("chats")
            chatRef.orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val chatList = mutableListOf<ChatList>()
                        for (childSnapshot in snapshot.children) {
                            val chat = childSnapshot.getValue(ChatList::class.java)
                            if (chat != null) {
                                chatList.add(chat)
                            }
                        }
                        _chatList.value = chatList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("HomeViewModel", "Error fetching user chat: ${error.message}")
                    }
                })
        }
    }

    fun addChat(newChat: ChatList) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            val newChatRef = FirebaseDatabase.getInstance().getReference("chats").push()
            val chatWithUser = newChat.copy(currentUserId)
            newChatRef.setValue(chatWithUser).addOnSuccessListener {
                Log.d("HomeViewModel", "chat added successfully to firebase")
            }.addOnFailureListener { exception ->
                Log.e("HomeViewModel", "Failed to add chat: ${exception.message}")

            }
        } else {
            Log.e("HomeViewModel", "User is not authenticate")
        }
    }
}