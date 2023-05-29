package com.app.wecollabs.ui.main.fragments.home.viewHolders

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.data.EN
import com.app.core.data.OTHER
import com.app.core.data.Result
import com.app.core.data.models.Goal
import com.app.core.ui.GoalAdapter
import com.app.wecollabs.R
import com.app.wecollabs.databinding.HomeGoalCategoryBinding
import com.app.wecollabs.ui.listGoal.ListGoalActivity
import com.app.wecollabs.ui.listOrganization.ListOrganizationActivity
import com.app.wecollabs.ui.main.fragments.home.HomeViewModel
import com.app.wecollabs.ui.main.fragments.home.adapter.HomeViewHolder

class HomeGoalCategory(
    private val binding: HomeGoalCategoryBinding,
): HomeViewHolder(binding.root) {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var parent: Fragment
    private lateinit var goalAdapter: GoalAdapter

    override fun bind(context: Context, viewModel: HomeViewModel, parent: Fragment) {
        this.context = context
        this.viewModel = viewModel
        this.parent = parent
        setView()
    }

    private fun setView() = with(binding) {
        goalAdapter = GoalAdapter {
            if(it.id != null && it.id != OTHER) {
                Intent().apply {
                    putExtra(ListOrganizationActivity.SELECTED_GOAL, it)
                    setClass(context, ListOrganizationActivity::class.java)
                    context.startActivity(this)
                }
            } else {
                context.startActivity(Intent(context, ListGoalActivity::class.java))
            }
        }
        rvGoals.layoutManager = GridLayoutManager(context, 3)
        rvGoals.adapter = goalAdapter
        viewModel.getListGoalsWithLimit().observe(parent.viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    Log.d("HomeGoalCategory", "getListGoalsWithLimit: loading")
                }
                is Result.Error -> {
                    Log.e("HomeGoalCategory", "getListGoalsWithLimit: ${it.error}")
                }
                is Result.Success -> {
                    Log.d("HomeGoalCategory", "getListGoalsWithLimit: success")
                    val list = it.data as MutableList<Goal>
                    list.add(Goal(
                        id = OTHER,
                        name = mapOf(
                            EN to context.getString(R.string.see_more)
                        )
                    ))
                    goalAdapter.submitList(list)
                }
            }
        }
    }

}