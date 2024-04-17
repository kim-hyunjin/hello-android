package com.github.kimhyunjin.mycontentprovider

import android.annotation.SuppressLint
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.kimhyunjin.mycontentprovider.databinding.ActivityMainBinding
import com.github.kimhyunjin.mycontentprovider.db.PersonContract

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.insertBtn.setOnClickListener {
            insertData()
        }
        binding.queryBtn.setOnClickListener {
            queryData()
        }
        binding.updateBtn.setOnClickListener {
            updateDate()
        }
        binding.deleteBtn.setOnClickListener {
            deleteData()
        }
    }

    private fun insertData() {
        println("insertData가 호출됨")
        var uri = Uri.parse("content://com.github.kimhyunjin.myapplication.provider/person")
        val values = ContentValues().apply {
            put(PersonContract.PersonEntry.PERSON_NAME, "ows")
            put(PersonContract.PersonEntry.PERSON_AGE, 28)
            put(PersonContract.PersonEntry.PERSON_MOBILE, "010-0000-0000")
        }

        uri = contentResolver.insert(uri, values)
        println("insertDatat 결과 : $uri")
    }

    @SuppressLint("Range")
    private fun queryData() {
        val uri = Uri.parse("content://com.github.kimhyunjin.myapplication.provider/person")
        val columns = arrayOf(
            PersonContract.PersonEntry.PERSON_NAME,
            PersonContract.PersonEntry.PERSON_AGE,
            PersonContract.PersonEntry.PERSON_MOBILE
        )
        val cursor = contentResolver.query(uri, columns, null, null, "name ASC")
        println("queryData 결과 ${cursor?.count}")

        cursor?.let { cursor ->
            var index = 0
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndex(columns.get(0)))
                val age = cursor.getInt(cursor.getColumnIndex(columns.get(1)))
                val mobile = cursor.getString(cursor.getColumnIndex(columns.get(2)))

                println("#${index} -> ${name}, ${age}, ${mobile}")
                index++
            }
        }
    }

    private fun updateDate() {
        val uri = Uri.parse("content://com.github.kimhyunjin.myapplication.provider/person")
        val selection = "mobile = ?"
        val selectionArgs = arrayOf("010-0000-0000")

        val values = ContentValues().apply {
            put("mobile", "010-1000-1000")
        }

        val count = contentResolver.update(uri, values, selection, selectionArgs)
        println("updateData 결과 ${count}")
    }

    private fun deleteData() {
        val uri = Uri.parse("content://com.github.kimhyunjin.myapplication.provider/person")
        val selection = "name = ?"
        val selectionArgs = arrayOf("ows")

        val count = contentResolver.delete(uri, selection, selectionArgs)
        println("deleteData 결과 ${count}")
    }

    private fun println(str: String) = with(binding) {
        binding.resultTextView.append("$str\n")
    }
}