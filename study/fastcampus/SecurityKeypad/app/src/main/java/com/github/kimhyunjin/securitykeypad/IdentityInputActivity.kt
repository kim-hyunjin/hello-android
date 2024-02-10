package com.github.kimhyunjin.securitykeypad

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.github.kimhyunjin.securitykeypad.databinding.ActivityIdentityInputBinding
import com.github.kimhyunjin.securitykeypad.util.ViewUtil.hideKeyboard
import com.github.kimhyunjin.securitykeypad.util.ViewUtil.setEditorActionListener
import com.github.kimhyunjin.securitykeypad.util.ViewUtil.showKeyboard
import com.github.kimhyunjin.securitykeypad.util.ViewUtil.showKeyboardDelay

class IdentityInputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIdentityInputBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdentityInputBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        binding.nameEdit.showKeyboardDelay()
    }

    private fun initView() {
        binding.view = this

        with(binding) {
            nameEdit.setEditorActionListener(EditorInfo.IME_ACTION_NEXT) {
                birthdayLayout.isVisible = true
                birthdayEdit.showKeyboard()
            }

            birthdayEdit.doAfterTextChanged {
                if (birthdayEdit.length() > 7) {
                    genderLayout.isVisible = true
                    birthdayEdit.hideKeyboard()
                }
            }

            genderChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                if (!telecomLayout.isVisible) {
                    telecomLayout.isVisible = true
                }
            }

            telecomChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                if (!phoneLayout.isVisible) {
                    phoneLayout.isVisible = true
                    phoneEdit.showKeyboard()
                }
            }

            phoneEdit.doAfterTextChanged {
                if (phoneEdit.length() > 10) {
                    confirmButton.isVisible = true
                    phoneEdit.hideKeyboard()
                }
            }

            phoneEdit.setEditorActionListener(EditorInfo.IME_ACTION_DONE) {
                if (phoneEdit.length() > 9) {
                    confirmButton.isVisible = true
                    phoneEdit.hideKeyboard()
                }
            }
        }
    }
}