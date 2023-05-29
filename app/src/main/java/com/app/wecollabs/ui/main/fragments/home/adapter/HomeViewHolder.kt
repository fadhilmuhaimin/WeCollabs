package com.app.wecollabs.ui.main.fragments.home.adapter

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.wecollabs.ui.main.fragments.home.HomeViewModel

abstract class HomeViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    abstract fun bind(context: Context, viewModel: HomeViewModel, parent: Fragment)
}