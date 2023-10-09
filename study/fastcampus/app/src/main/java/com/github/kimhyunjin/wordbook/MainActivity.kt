package com.github.kimhyunjin.wordbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.github.kimhyunjin.wordbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), WordAdapter.ItemWordClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var wordAdapter: WordAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        binding.addButton.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    private fun initRecyclerView() {
        val words = mutableListOf<Word>(
            Word("weather", "날씨", "명사"),
            Word("weather", "날씨", "명사"),
            Word("weather", "날씨", "명사")
        )

        wordAdapter = WordAdapter(words, this)
        binding.wordRecyclerView.apply {
            adapter = wordAdapter
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    applicationContext,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
    }

    override fun onClick(word: Word) {
        Toast.makeText(this, "${word.text} clicked1", Toast.LENGTH_SHORT).show()
    }
}