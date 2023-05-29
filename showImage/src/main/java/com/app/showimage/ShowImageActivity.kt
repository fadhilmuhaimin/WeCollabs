package com.app.showimage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.core.data.IMAGE
import com.app.showimage.databinding.ActivityShowImageBinding
import com.bumptech.glide.Glide

class ShowImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowImageBinding
    private var image: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        image = intent.getStringExtra(IMAGE)
        binding = ActivityShowImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        image?.let {
            Glide.with(this)
                .load(it)
                .placeholder(com.app.core.R.color.grey3)
                .error(com.app.core.R.color.grey3)
                .into(binding.image)
        }
        binding.backBtn.setOnClickListener {
            finish()
        }
    }
}