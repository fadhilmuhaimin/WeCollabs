package com.app.wecollabs.ui.listArticle

import androidx.lifecycle.ViewModel
import com.app.core.data.repository.ArticleRepository

class ListArticleViewModel(private val articleRepository: ArticleRepository): ViewModel() {
    fun gerArticles() = articleRepository.getArticles()
}