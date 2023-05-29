package com.app.wecollabs.ui.main.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.wecollabs.databinding.*
import com.app.wecollabs.ui.main.fragments.home.adapter.HomeViewHolder
import com.app.wecollabs.ui.main.fragments.home.adapter.HomeViewsAdapter
import com.app.wecollabs.ui.main.fragments.home.viewHolders.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModel()
    private var listHomeViews = mutableListOf<HomeViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            listHomeViews = mutableListOf(
                HomeTopBar(
                    HomeTopBarBinding.inflate(LayoutInflater.from(requireContext()), binding.root, false)
                ),
                HomeOnTrending(
                    HomeOnTrendingBinding.inflate(LayoutInflater.from(requireContext()), binding.root, false),
                ),
                HomeGoalCategory(
                    HomeGoalCategoryBinding.inflate(LayoutInflater.from(requireContext()), binding.root, false)
                ),
                HomeEvents(
                    HomeEventsBinding.inflate(LayoutInflater.from(requireContext()), binding.root, false)
                ),
                HomeArticles(
                    HomeArticlesBinding.inflate(LayoutInflater.from(requireContext()), binding.root, false)
                )
            )
            rvHome.setHasFixedSize(true)
            rvHome.layoutManager = LinearLayoutManager(requireContext())
            rvHome.adapter = HomeViewsAdapter(listHomeViews, viewModel, this@HomeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}