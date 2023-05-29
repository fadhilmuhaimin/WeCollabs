package com.app.wecollabs.ui.main.fragments.home.viewHolders

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.Result
import com.app.core.ui.HorizontalEventAdapter
import com.app.wecollabs.databinding.HomeOnTrendingBinding
import com.app.wecollabs.ui.eventDetail.DetailEventActivity
import com.app.wecollabs.ui.listEvent.ListEventActivity
import com.app.wecollabs.ui.main.fragments.home.HomeViewModel
import com.app.wecollabs.ui.main.fragments.home.adapter.HomeViewHolder

class HomeOnTrending(
    private val binding: HomeOnTrendingBinding,
): HomeViewHolder(binding.root) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var context: Context
    private lateinit var parent: Fragment
    private lateinit var horizontalEventAdapter: HorizontalEventAdapter

    override fun bind(context: Context, viewModel: HomeViewModel, parent: Fragment) {
        this.context = context
        this.viewModel = viewModel
        this.parent = parent
        setView()
    }

    private fun setView() = with(binding) {
        horizontalEventAdapter = HorizontalEventAdapter {
            Intent().apply {
                putExtra(DetailEventActivity.SELECTED_EVENT, it)
                setClass(context, DetailEventActivity::class.java)
                context.startActivity(this)
            }
        }
        rvEvents.setHasFixedSize(true)
        rvEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvEvents.adapter = horizontalEventAdapter

        viewModel.getTrendingEvents().observe(parent.viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {}
                is Result.Error -> {}
                is Result.Success -> {
                    horizontalEventAdapter.submitList(it.data)
                }
            }
        }

        seeMoreBtn.setOnClickListener {
            context.startActivity(Intent(context, ListEventActivity::class.java))
        }
    }

}