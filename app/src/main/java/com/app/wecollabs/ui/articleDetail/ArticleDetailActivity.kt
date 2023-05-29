package com.app.wecollabs.ui.articleDetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.core.data.IMAGE
import com.app.core.data.SHOW_IMAGE_ACTIVITY
import com.app.core.data.models.Article
import com.app.core.utils.convertToDate
import com.app.wecollabs.databinding.ActivityArticleDetailBinding
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding
    private var selectedArticle: Article? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
    }

    private fun initVariable() {
        selectedArticle = intent.getParcelableExtra(SELECTED_ARTICLE)
        selectedArticle?.let { article ->
            Glide.with(this)
                .load(article.imageUrl)
                .placeholder(com.app.core.R.color.grey3)
                .error(com.app.core.R.color.grey3)
                .into(binding.articleImage)
            binding.articleTitle.text = article.title
            binding.articlePublishDate.text = article.publishedAt?.convertToDate(SimpleDateFormat.MEDIUM)
            binding.articleContent.text = article.content
        }
    }

    private fun initListener() = with(binding) {
        backBtn.setOnClickListener { finish() }
        articleImage.setOnClickListener {
            Intent().apply {
                setClass(this@ArticleDetailActivity, Class.forName(SHOW_IMAGE_ACTIVITY))
                putExtra(IMAGE, selectedArticle?.imageUrl)
                startActivity(this)
            }
        }
    }

    companion object {
        const val SELECTED_ARTICLE = "article"
    }
}