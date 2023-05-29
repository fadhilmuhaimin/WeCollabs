package com.app.wecollabs.ui.main.fragments.home.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.app.wecollabs.ui.main.fragments.home.HomeViewModel

class HomeViewsAdapter(
    private val viewHolder : List<HomeViewHolder>,
    private val viewModel: HomeViewModel,
    private val parent: Fragment,
    ): RecyclerView.Adapter<HomeViewHolder>() {
    private lateinit var context: Context

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        context = parent.context
        return viewHolder[viewType]
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        viewHolder[position].bind(context, viewModel, parent)
    }

    override fun getItemCount(): Int = viewHolder.size

}