package com.app.wecollabs.ui.listOrganization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.data.EN
import com.app.core.data.Result
import com.app.core.data.models.Goal
import com.app.core.ui.GridOrganizationAdapter
import com.app.wecollabs.databinding.ActivityListOrganizationBinding
import com.app.wecollabs.ui.organizationDetail.OrganizationDetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListOrganizationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListOrganizationBinding
    private var selectedGoal: Goal? = null
    private lateinit var gridOrganizationAdapter: GridOrganizationAdapter
    private val viewModel: ListOrganizationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOrganizationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
    }

    private fun initVariable() = with(binding) {
        selectedGoal = intent.getParcelableExtra(SELECTED_GOAL)
        Log.d("ListOrganizationActivity", "selectedGoal: $selectedGoal")
        toolbarTitle.text = selectedGoal?.name?.get(EN)
        gridOrganizationAdapter = GridOrganizationAdapter {
            Intent().apply {
                putExtra(OrganizationDetailActivity.SELECTED_ORGANIZATION, it)
                setClass(this@ListOrganizationActivity, OrganizationDetailActivity::class.java)
                startActivity(this)
            }
        }
        rvOrganizations.setHasFixedSize(true)
        rvOrganizations.layoutManager = GridLayoutManager(this@ListOrganizationActivity, 2)
        rvOrganizations.adapter = gridOrganizationAdapter
    }

    private fun initListener() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        selectedGoal?.let { goal ->
            goal.id?.let { id ->
                viewModel.getOrganizations(id).observe(this) { result ->
                    when(result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is Result.Error -> {
                            Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            if(result.data.isEmpty()) {
                                binding.emptyInfo.visibility = View.VISIBLE
                            } else {
                                gridOrganizationAdapter.submitList(result.data)
                            }
                        }
                    }
                }
            }
        }
    }



    companion object {
        const val SELECTED_GOAL = "goal"
    }

}