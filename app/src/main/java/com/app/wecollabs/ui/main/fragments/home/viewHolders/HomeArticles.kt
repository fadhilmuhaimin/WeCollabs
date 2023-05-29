package com.app.wecollabs.ui.main.fragments.home.viewHolders

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.Result
import com.app.core.ui.VerticalArticleAdapter
import com.app.wecollabs.databinding.HomeArticlesBinding
import com.app.wecollabs.ui.articleDetail.ArticleDetailActivity
import com.app.wecollabs.ui.listArticle.ListArticleActivity
import com.app.wecollabs.ui.main.fragments.home.HomeViewModel
import com.app.wecollabs.ui.main.fragments.home.adapter.HomeViewHolder

class HomeArticles(
    private val binding: HomeArticlesBinding
): HomeViewHolder(binding.root) {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var parent: Fragment
    private lateinit var verticalArticleAdapter: VerticalArticleAdapter

    override fun bind(context: Context, viewModel: HomeViewModel, parent: Fragment) {
        this.context = context
        this.viewModel = viewModel
        this.parent = parent
        setView()
    }

    private fun setView() = with(binding) {
        seeMoreBtn.setOnClickListener {
            context.startActivity(Intent(context, ListArticleActivity::class.java))
        }
        verticalArticleAdapter = VerticalArticleAdapter {
            Intent().apply {
                setClass(context, ArticleDetailActivity::class.java)
                putExtra(ArticleDetailActivity.SELECTED_ARTICLE, it)
                context.startActivity(this)
            }
        }

        rvArticles.layoutManager = LinearLayoutManager(context)
        rvArticles.adapter = verticalArticleAdapter
        viewModel.getArticlesWithLimit().observe(parent.viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {}
                is Result.Error -> {}
                is Result.Success -> {
                    verticalArticleAdapter.submitList(it.data)
                }
            }
        }
    }


}