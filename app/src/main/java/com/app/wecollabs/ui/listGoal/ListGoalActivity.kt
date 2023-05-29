package com.app.wecollabs.ui.listGoal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.data.Result
import com.app.core.ui.GoalAdapter
import com.app.wecollabs.databinding.ActivityListGoalBinding
import com.app.wecollabs.ui.listOrganization.ListOrganizationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListGoalBinding
    private val viewModel: ListGoalViewModel by viewModel()
    private lateinit var goalAdapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
    }

    private fun initVariable() = with(binding) {
        goalAdapter = GoalAdapter {
            Intent().apply {
                putExtra(ListOrganizationActivity.SELECTED_GOAL, it)
                setClass(this@ListGoalActivity, ListOrganizationActivity::class.java)
                startActivity(this)
            }
        }
        rvGoals.layoutManager = GridLayoutManager(this@ListGoalActivity, 3)
        rvGoals.adapter = goalAdapter
    }

    private fun initListener() = with(binding) {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        viewModel.getGoals().observe(this@ListGoalActivity) {
            when(it) {
                is Result.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    Toast.makeText(this@ListGoalActivity, it.error, Toast.LENGTH_LONG).show()
                }
                is Result.Success -> {
                    progressBar.visibility = View.GONE
                    goalAdapter.submitList(it.data)
                }
            }
        }
    }
}