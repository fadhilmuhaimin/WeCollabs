package com.app.wecollabs.ui.main.fragments.favorite

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.ui.VerticalEventAdapter
import com.app.core.utils.DataMapper
import com.app.wecollabs.databinding.FragmentFavoriteBinding
import com.app.wecollabs.ui.eventDetail.DetailEventActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavoriteViewModel by viewModel()
    private lateinit var verticalEventAdapter: VerticalEventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verticalEventAdapter = VerticalEventAdapter {
            Intent().apply {
                putExtra(DetailEventActivity.SELECTED_EVENT, it)
                setClass(requireContext(), DetailEventActivity::class.java)
                startActivity(this)
            }
        }
        with(binding) {
            rvEvents.setHasFixedSize(true)
            rvEvents.layoutManager = LinearLayoutManager(requireContext())
            rvEvents.adapter = verticalEventAdapter
        }
        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) {
            if(it.isEmpty()) {
                showEmptyInfo(true)
            } else {
                showEmptyInfo(false)
                verticalEventAdapter.submitList(DataMapper.mapEventEntityToEvent(it))
            }
        }
    }

    private fun showEmptyInfo(isEmpty: Boolean) = with(binding) {
        if(isEmpty) {
            emptyInfo.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
            rvEvents.visibility = View.GONE
        } else {
            emptyInfo.visibility = View.GONE
            rvEvents.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}