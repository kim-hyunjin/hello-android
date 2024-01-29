package com.github.kimhyunjin.starbucks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.kimhyunjin.starbucks.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        val homeData = context?.readData("home", Home::class.java) ?: return

        binding.appBarTitleTextView.text =
            getString(R.string.appbar_title_text, homeData.user.nickname)
        binding.startCountTextView.text =
            getString(R.string.appbar_star_title, homeData.user.starCount, homeData.user.totalCount)
        binding.appBarProgressBar.max = homeData.user.totalCount
        binding.appBarProgressBar.progress = homeData.user.starCount
        Glide.with(binding.appBarImageView).load(homeData.appbarImage).into(binding.appBarImageView)

        val menuData = context?.readData("menu", Menu::class.java) ?: return
        binding.recommendMenuList.tvTitle.text =
            getString(R.string.title_menu_list, homeData.user.nickname)
        menuData.coffee.forEach { menuItem ->
            binding.recommendMenuList.menuLayout.addView(
                MenuView(context = requireContext()).apply {
                    setTitle(menuItem.name)
                    setImageUrl(menuItem.image)
                }
            )
        }

    }
}