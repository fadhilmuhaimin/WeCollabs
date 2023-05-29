package com.app.organizationpanel.ui.organizationPanel.ui.event

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.Result
import com.app.core.data.models.Event
import com.app.core.data.models.Organization
import com.app.core.ui.VerticalEventAdapter
import com.app.organizationpanel.databinding.FragmentEventBinding
import com.app.organizationpanel.ui.addEvent.AddEventActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class EventFragment : Fragment() {

    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!
    private val viewModel: EventViewModel by viewModel()
    private lateinit var verticalEventAdapter: VerticalEventAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verticalEventAdapter = VerticalEventAdapter {
            Intent().apply {
                putExtra(AddEventActivity.SELECTED_EVENT, it)
                setClass(requireContext(), AddEventActivity::class.java)
                startActivity(this)
            }
        }
        with(binding) {
            rvEvents.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = verticalEventAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Organization.currentOrganization?.let {
            it.id?.let { it1 ->
                viewModel.getEvents(it1).observe(viewLifecycleOwner, observer)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Organization.currentOrganization?.let {
            it.id?.let { it1 ->
                viewModel.getEvents(it1).removeObserver(observer)
            }
        }
    }

    private val observer = (Observer<Result<List<Event>>> { result ->
        when(result) {
            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is Result.Error -> {
                Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
            }
            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                if(result.data.isEmpty()) {
                    binding.emptyInfo.visibility = View.VISIBLE
                } else {
                    verticalEventAdapter.submitList(result.data)
                }
            }
        }
    })

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}