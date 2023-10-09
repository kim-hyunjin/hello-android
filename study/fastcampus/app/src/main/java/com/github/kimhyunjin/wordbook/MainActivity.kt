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

    private var selectedWord: Word? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        binding.addButton.setOnClickListener {
            updateAddWordResult.launch(Intent(this, AddActivity::class.java))
        }

        binding.deleteImageView.setOnClickListener {
            delete()
        }
    }

    private fun initRecyclerView() {

        wordAdapter = WordAdapter(mutableListOf(), this)
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
            wordAdapter.list.add(0, word)
            runOnUiThread {
                wordAdapter.notifyDataSetChanged()
            }
        }.start()
    }

    private fun delete() {
        if (selectedWord == null) {
            return
        }

        Thread {
            selectedWord?.let {
                AppDatabase.getInstance(this).wordDao().delete(it)
                runOnUiThread {
                    val index = wordAdapter.list.indexOf(it)
                    wordAdapter.list.removeAt(index)
                    wordAdapter.notifyItemRemoved(index)
                    binding.textTextView.text = ""
                    binding.meanTextView.text = ""
                    Toast.makeText(this, "단어가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                selectedWord = null
            }
        }.start()
    }

    override fun onClick(word: Word) {
        selectedWord = word
        binding.textTextView.text = word.text
        binding.meanTextView.text = word.mean
    }
}