package com.github.kimhyunjin.securitykeypad.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.view.children
import com.github.kimhyunjin.securitykeypad.databinding.WidgetShuffleNumberKeypadBinding
import kotlin.random.Random

class ShuffleNumberKeypad @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attributeSet, defStyleAttr) {

    private var _binding: WidgetShuffleNumberKeypadBinding? = null

    private val binding get() = _binding!!

    init {
        _binding =
            WidgetShuffleNumberKeypadBinding.inflate(LayoutInflater.from(context), this, true)
        shuffle()
    }

    // View의 경우 databinding을 사용하는 경우, 화면에서 뷰가 사라졌을 때 명시적으로 databinding에 대한 참조를 끊어줘야한다.
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

    private fun shuffle() {
        val numberArr = ArrayList<String>()
        for (i in 0..9) {
            numberArr.add(i.toString())
        }

        binding.gridLayout.children.forEach { view ->
            if (view is TextView && view.tag != null) {
                val randIndex = Random.nextInt(numberArr.size)
                view.text = numberArr[randIndex]
                numberArr.removeAt(randIndex)
            }
        }
    }
}