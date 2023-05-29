package com.app.wecollabs.ui.searchEvent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.Result
import com.app.core.ui.VerticalEventAdapter
import com.app.wecollabs.databinding.ActivitySearchEventBinding
import com.app.wecollabs.ui.eventDetail.DetailEventActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchEventActivity : AppCompatActivity() {

    private var keyword = ""
    private lateinit var binding: ActivitySearchEventBinding
    private val viewModel: SearchEventViewModel by viewModel()
    private lateinit var verticalEventAdapter: VerticalEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
    }

    private fun initVariable() = with(binding) {
        keyword = intent.getStringExtra(KEYWORD).toString()
        searchEvents(keyword)
        appbar.searchBar.setText(keyword)
        verticalEventAdapter = VerticalEventAdapter {
            Intent().apply {
                putExtra(DetailEventActivity.SELECTED_EVENT, it)
                setClass(this@SearchEventActivity, DetailEventActivity::class.java)
                startActivity(this)
            }
        }
        rvEvents.setHasFixedSize(true)
        rvEvents.layoutManager = LinearLayoutManager(this@SearchEventActivity)
        rvEvents.adapter = verticalEventAdapter
    }

    private fun initListener() = with(binding) {
        appbar.searchBar.addTextChangedListener { value ->
            if(value.toString().isNotEmpty()) {
                searchEvents(value.toString())
            }
        }
        appbar.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun searchEvents(keyword: String) = viewModel.searchEvents(keyword.trim()).observe(this) {
        when(it) {
            is Result.Loading -> {
                showLoading(true)
            }
            is Result.Error -> {
                Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
            }
            is Result.Success -> {
                showLoading(false)
                if(it.data.isEmpty()) {
                    showEmptyInfo(true)
                } else {
                    showEmptyInfo(false)
                    verticalEventAdapter.submitList(it.data)
                    binding.rvEvents.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) = with(binding) {
        if(isLoading) {
            rvEvents.visibility = View.GONE
            emptyInfo.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun showEmptyInfo(isEmpty: Boolean) = with(binding) {
        if(isEmpty) {
            rvEvents.visibility = View.GONE
            emptyInfo.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        } else {
            emptyInfo.visibility = View.GONE
        }
    }

    companion object {
        const val KEYWORD = "keyword"
    }
}