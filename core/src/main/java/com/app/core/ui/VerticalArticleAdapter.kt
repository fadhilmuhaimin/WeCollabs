package com.app.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.core.R
import com.app.core.data.models.Article
import com.app.core.databinding.VerticalArticleBinding
import com.app.core.utils.convertToDate
import com.bumptech.glide.Glide

class VerticalArticleAdapter (private val onClick: (Article) -> Unit): ListAdapter<Article, VerticalArticleAdapter.Holder>(DIFF_CALLBACK) {

    inner class Holder(private val binding: VerticalArticleBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) = with(binding) {
            Glide.with(this.root)
                .load(article.imageUrl)
                .placeholder(R.color.grey3)
                .error(R.color.grey3)
                .into(articleImage)
            articleTitle.text = article.title
            articleDate.text = article.publishedAt?.convertToDate()
            this.root.setOnClickListener { onClick(article) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = VerticalArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Article> =
            object : DiffUtil.ItemCallback<Article>() {
                override fun areItemsTheSame(old: Article, new: Article): Boolean {
                    return old.id == new.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(old: Article, new: Article): Boolean {
                    return old == new
                }
            }
    }

}