package com.github.kimhyunjin.chattingapp.userlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kimhyunjin.chattingapp.Key
import com.github.kimhyunjin.chattingapp.R
import com.github.kimhyunjin.chattingapp.chatdetail.ChatActivity
import com.github.kimhyunjin.chattingapp.chatlist.ChatRoomItem
import com.github.kimhyunjin.chattingapp.databinding.FragmentUserlistBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID

class UserFragment : Fragment(R.layout.fragment_userlist) {

    private lateinit var binding: FragmentUserlistBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserlistBinding.bind(view)

        val userListAdapter = UserAdapter { otherUser ->
            val myUserId = Firebase.auth.currentUser?.uid ?: return@UserAdapter
            otherUser.userId ?: return@UserAdapter
            val chatRoomDB = Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(myUserId)
                .child(otherUser.userId)

            var chatRoomId = ""
            chatRoomDB.get().addOnSuccessListener {
                if (it.exists()) {
                    val chatRoom = it.getValue(ChatRoomItem::class.java)
                    chatRoomId = chatRoom?.chatRoomId ?: ""
                } else {
                    chatRoomId = UUID.randomUUID().toString()
                    val newChatRoom = ChatRoomItem(
                        chatRoomId = chatRoomId,
                        otherUserId = otherUser.userId,
                        otherUserName = otherUser.username
                    )
                    chatRoomDB.setValue(newChatRoom)
                }

                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(ChatActivity.EXTRA_CHAT_ROOM_ID, chatRoomId)
                intent.putExtra(ChatActivity.EXTRA_OTHER_USER_ID, otherUser.userId)
                startActivity(intent)
            }
        }
        binding.userListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userListAdapter
        }

        Firebase.database.reference.child(Key.DB_USERS)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<UserItem>()

                    snapshot.children.forEach {
                        val user = it.getValue(UserItem::class.java)
                        if (user != null && user.userId != Firebase.auth.currentUser?.uid) {
                            list.add(user)
                        }
                    }

                    userListAdapter.submitList(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }
}