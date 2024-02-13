package com.github.kimhyunjin.walletcards

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityOptionsCompat
import com.github.kimhyunjin.walletcards.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.cardTitleTextView.text = intent.getStringExtra(CARD_NAME)
        binding.cardLayout.backgroundTintList = intent.getParcelableExtra(CARD_COLOR) as? ColorStateList
    }

    companion object {
        private const val CARD_NAME = "CARD_NAME"
        private const val CARD_COLOR = "CARD_COLOR"

        fun start(context: Context, cardName: String, cardColor: ColorStateList?, optionCompat: ActivityOptionsCompat) {
            Intent(context, DetailActivity::class.java).apply {
                putExtra(CARD_NAME, cardName)
                putExtra(CARD_COLOR,cardColor)
            }.run {
                context.startActivity(this, optionCompat.toBundle())
            }
        }
    }
}