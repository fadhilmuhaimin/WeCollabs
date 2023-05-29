package com.app.wecollabs.ui.listArticle

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.Result
import com.app.core.ui.VerticalArticleAdapter
import com.app.wecollabs.databinding.ActivityListArticleBinding
import com.app.wecollabs.ui.articleDetail.ArticleDetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListArticleBinding
    private val viewModel: ListArticleViewModel by viewModel()
    private lateinit var verticalArticleAdapter: VerticalArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
    }

    private fun initVariable() = with(binding) {
        verticalArticleAdapter = VerticalArticleAdapter {
            Intent().apply {
                putExtra(ArticleDetailActivity.SELECTED_ARTICLE, it)
                setClass(this@ListArticleActivity, ArticleDetailActivity::class.java)
                startActivity(this)
            }
        }
        rvArticles.setHasFixedSize(true)
        rvArticles.layoutManager = LinearLayoutManager(this@ListArticleActivity)
        rvArticles.adapter = verticalArticleAdapter
    }

    private fun initListener() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        viewModel.gerArticles().observe(this) {
            when(it) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    if(it.data.isEmpty()) {
                        binding.emptyInfo.visibility = View.VISIBLE
                    } else {
                        verticalArticleAdapter.submitList(it.data)
                    }
                }
            }
        }
    }
}