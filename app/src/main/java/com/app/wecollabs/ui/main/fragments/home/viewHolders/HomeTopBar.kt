package com.app.wecollabs.ui.main.fragments.home.viewHolders

import android.content.Context
import android.content.Intent
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.app.core.data.models.User
import com.app.wecollabs.databinding.HomeTopBarBinding
import com.app.wecollabs.ui.main.fragments.home.HomeViewModel
import com.app.wecollabs.ui.main.fragments.home.adapter.HomeViewHolder
import com.app.wecollabs.ui.notification.NotificationActivity
import com.app.wecollabs.ui.searchEvent.SearchEventActivity

class HomeTopBar(private val binding: HomeTopBarBinding): HomeViewHolder(binding.root) {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel

    override fun bind(context: Context, viewModel: HomeViewModel, parent: Fragment) {
        this.context = context
        this.viewModel = viewModel
        binding.welcomeUserText.text = StringBuilder("Hi, ${User.currentUser?.name}")
        setView()
    }

    private fun setView() = with(binding) {
        notifBtn.setOnClickListener {
            context.startActivity(Intent(context, NotificationActivity::class.java))
        }
        searchBar.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                val keyword = searchBar.text.toString()
                if(keyword.isNotEmpty()) {
                    Intent().apply {
                        putExtra(SearchEventActivity.KEYWORD, keyword)
                        setClass(context, SearchEventActivity::class.java)
                        context.startActivity(this)
                    }
                }
                return@setOnEditorActionListener true
            }
            false
        }
    }

}