package com.github.kimhyunjin.chattingapp.chatlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kimhyunjin.chattingapp.R
import com.github.kimhyunjin.chattingapp.databinding.FragmentUserlistBinding

class ChatListFragment: Fragment(R.layout.fragment_userlist) {

    private lateinit var binding: FragmentUserlistBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserlistBinding.bind(view)

        val chatListAdapter = ChatListAdapter {

        }
        binding.userListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatListAdapter
        }

        chatListAdapter.submitList(mutableListOf(ChatRoomItem("1", "hello!!", "hyunjin", "qwer")))
    }
}