package com.github.kimhyunjin.tomorrowhouse.ui.article

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.github.kimhyunjin.tomorrowhouse.R
import com.github.kimhyunjin.tomorrowhouse.databinding.FragmentWriteArticleBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.util.UUID

class WriteArticleFragment : Fragment(R.layout.fragment_write_article) {
    private lateinit var binding: FragmentWriteArticleBinding
    private var selectedUri: Uri? = null
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            binding.photoImageView.setImageURI(uri)
            this.selectedUri = uri
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWriteArticleBinding.bind(view)

        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        binding.photoImageView.setOnClickListener {

        }
        binding.photoClearButton.setOnClickListener {

        }
        binding.submitButton.setOnClickListener {
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener
                val fileName = "${UUID.randomUUID()}.png"
                Firebase.storage.reference.child("articles/photo").child(fileName)
                    .putFile(photoUri).addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                        } else {
                            // error handler
                        }
                    }
            } else {
                Snackbar.make(view, "이미지가 선택되지 않았습니다.", Snackbar.LENGTH_SHORT).show()
            }
        }
        binding.backButton.setOnClickListener {
            findNavController().navigate(WriteArticleFragmentDirections.actionBack())
        }
    }
}