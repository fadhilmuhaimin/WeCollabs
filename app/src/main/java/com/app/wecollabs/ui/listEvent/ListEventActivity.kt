package com.app.wecollabs.ui.listEvent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.Result
import com.app.core.ui.VerticalEventAdapter
import com.app.wecollabs.databinding.ActivityListEventBinding
import com.app.wecollabs.ui.eventDetail.DetailEventActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListEventBinding
    private val viewModel: ListEventViewModel by viewModel()
    private lateinit var verticalEventAdapter: VerticalEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
    }

    private fun initVariable() = with(binding) {
        verticalEventAdapter = VerticalEventAdapter {
            Intent().apply {
                putExtra(DetailEventActivity.SELECTED_EVENT, it)
                setClass(this@ListEventActivity, DetailEventActivity::class.java)
                startActivity(this)
            }
        }
        rvEvents.setHasFixedSize(true)
        rvEvents.layoutManager = LinearLayoutManager(this@ListEventActivity)
        rvEvents.adapter = verticalEventAdapter
    }

    private fun initListener() {
        binding.toolbarEvents.setNavigationOnClickListener {
            finish()
        }
        viewModel.getEvents().observe(this) {
            when(it) {
                is Result.Loading -> {}
                is Result.Error -> {}
                is Result.Success -> {
                    verticalEventAdapter.submitList(it.data)
                }
            }
        }
    }

}