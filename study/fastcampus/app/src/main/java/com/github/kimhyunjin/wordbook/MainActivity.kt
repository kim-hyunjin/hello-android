package com.github.kimhyunjin.wordbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.kimhyunjin.wordbook.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), WordAdapter.ItemWordClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var wordAdapter: WordAdapter
    private val updateAddWordResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.d("updateAddWordResult", result.data.toString())
            val isUpdated = result.data?.getBooleanExtra("isUpdated", false) ?: false
            if (result.resultCode == RESULT_OK && isUpdated) {
                updateAddWord()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        binding.addButton.setOnClickListener {
            updateAddWordResult.launch(Intent(this, AddActivity::class.java))
        }
    }

    private fun initRecyclerView() {

        wordAdapter = WordAdapter(mutableListOf(), this)
        binding.wordRecyclerView.apply {
            adapter = wordAdapter
            layoutManager =
                LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, true)
            addItemDecoration(
                DividerItemDecoration(
                    applicationContext,
                    LinearLayoutManager.VERTICAL
                )
            )
        }

        Thread {
            val words = AppDatabase.getInstance(this).wordDao().getAll() ?: emptyList()
            wordAdapter.list.addAll(words)
            runOnUiThread {
                wordAdapter.notifyDataSetChanged()
            }
        }.start()
    }

    private fun updateAddWord() {
        Thread {
            val word = AppDatabase.getInstance(this).wordDao().getLatestWord()
            Log.d("updateAddWord", word.text)
            wordAdapter.list.add(word)
            runOnUiThread {
                wordAdapter.notifyItemInserted(wordAdapter.list.lastIndex)
            }
        }.start()
    }

    override fun onClick(word: Word) {
        Toast.makeText(this, "${word.text} clicked1", Toast.LENGTH_SHORT).show()
    }
}