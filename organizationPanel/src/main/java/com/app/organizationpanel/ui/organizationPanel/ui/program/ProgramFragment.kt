package com.app.organizationpanel.ui.organizationPanel.ui.program

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
import com.app.core.data.models.Organization
import com.app.core.data.models.Program
import com.app.core.ui.VerticalProgramAdapter
import com.app.organizationpanel.databinding.FragmentProgramBinding
import com.app.organizationpanel.ui.addProgram.AddProgramActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProgramFragment : Fragment() {

    private var _binding: FragmentProgramBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProgramViewModel by viewModel()
    private lateinit var verticalProgramAdapter: VerticalProgramAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProgramBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        verticalProgramAdapter = VerticalProgramAdapter {
            Intent().apply {
                putExtra(AddProgramActivity.SELECTED_PROGRAM, it)
                setClass(requireContext(), AddProgramActivity::class.java)
                startActivity(this)
            }
        }
        with(binding) {
            rvPrograms.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = verticalProgramAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Organization.currentOrganization?.let {
            it.id?.let { it1 ->
                viewModel.getPrograms(it1).observe(viewLifecycleOwner, observer)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Organization.currentOrganization?.let {
            it.id?.let { it1 ->
                viewModel.getPrograms(it1).removeObserver(observer)
            }
        }
    }

    private val observer = (Observer<Result<List<Program>>> {
        when(it) {
            is Result.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }
            is Result.Error -> {
                Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
            }
            is Result.Success -> {
                binding.progressBar.visibility = View.GONE
                if(it.data.isEmpty()) {
                    binding.emptyInfo.visibility = View.VISIBLE
                } else {
                    verticalProgramAdapter.submitList(it.data)
                }
            }
        }
    })

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}