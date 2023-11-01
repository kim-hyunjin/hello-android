package com.github.kimhyunjin.chattingapp.userlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kimhyunjin.chattingapp.R
import com.github.kimhyunjin.chattingapp.databinding.FragmentUserlistBinding

class UserFragment: Fragment(R.layout.fragment_userlist) {

    private lateinit var binding: FragmentUserlistBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentUserlistBinding.bind(view)

        val userListAdapter = UserAdapter {

        }
        binding.userListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userListAdapter
        }
    }
}