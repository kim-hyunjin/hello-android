package com.github.kimhyunjin.chattingapp.chatdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kimhyunjin.chattingapp.Key
import com.github.kimhyunjin.chattingapp.databinding.ActivityChatBinding
import com.github.kimhyunjin.chattingapp.userlist.UserItem
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var chatRoomId = ""
    private var otherUserId = ""
    private var myUserId = ""
    private var myUsername = ""

    private val chatItemList = mutableListOf<ChatItem>()

    private lateinit var chatAdapter: ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chatRoomId = intent.getStringExtra(EXTRA_CHAT_ROOM_ID) ?: return
        otherUserId = intent.getStringExtra(EXTRA_OTHER_USER_ID) ?: return
        myUserId = Firebase.auth.currentUser?.uid ?: ""

        chatAdapter = ChatAdapter()
        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        getUserItemWithId(myUserId) {
            myUsername = it.username ?: ""
        }
        getUserItemWithId(otherUserId) { otherUserItem ->
            chatAdapter.otherUserItem = otherUserItem
        }
        observeChatAdded()

        binding.sendButton.setOnClickListener {
            val message = binding.messageEditText.text.toString()
            if (message.isEmpty()) {
                return@setOnClickListener
            }

            sendMessage(message)

            binding.messageEditText.text.clear()
        }
    }

    private fun getUserItemWithId(id: String, onSuccess: (userItem: UserItem) -> Unit) {
        Firebase.database.reference.child(Key.DB_USERS).child(id).get().addOnSuccessListener {
            val userItem = it.getValue(UserItem::class.java)
            if (userItem != null) {
                onSuccess(userItem)
            }
        }
    }

    private fun observeChatAdded() {
        Firebase.database.reference.child(Key.DB_CHATS).child(chatRoomId)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val chatItem = snapshot.getValue(ChatItem::class.java)
                    chatItem ?: return
                    chatItemList.add(chatItem)
                    chatAdapter.submitList(chatItemList)
                    chatAdapter.notifyItemInserted(chatItemList.lastIndex)
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    TODO("Not yet implemented")
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private fun sendMessage(message: String) {
        Firebase.database.reference.child(Key.DB_CHATS).child(chatRoomId).push().apply {
            val chatItem = ChatItem(
                message = message,
                userId = myUserId,
                chatId = key
            )
            setValue(chatItem)
        }

        val updates = hashMapOf<String, Any>(
            "${Key.DB_CHAT_ROOMS}/$myUserId/$otherUserId/lastMessage" to message,
            "${Key.DB_CHAT_ROOMS}/$otherUserId/$myUserId/lastMessage" to message,
            "${Key.DB_CHAT_ROOMS}/$otherUserId/$myUserId/chatRoomId" to chatRoomId,
            "${Key.DB_CHAT_ROOMS}/$otherUserId/$myUserId/otherUserId" to myUserId,
            "${Key.DB_CHAT_ROOMS}/$otherUserId/$myUserId/otherUserName" to myUsername,
        )
        Firebase.database.reference.updateChildren(updates)
    }

    companion object {
        const val EXTRA_CHAT_ROOM_ID = "chatRoomId"
        const val EXTRA_OTHER_USER_ID = "otherUserId"
    }
}