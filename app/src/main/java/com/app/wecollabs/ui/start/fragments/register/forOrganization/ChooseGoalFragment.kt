package com.app.wecollabs.ui.start.fragments.register.forOrganization

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.app.core.data.Result
import com.app.core.ui.ChooseGoalAdapter
import com.app.wecollabs.databinding.FragmentChooseGoalBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseGoalFragment : Fragment() {

    private var _binding: FragmentChooseGoalBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChooseGoalViewModel by viewModel()
    private lateinit var chooseGoalAdapter: ChooseGoalAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChooseGoalBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.chooseGoalArgument = ChooseGoalFragmentArgs.fromBundle(arguments as Bundle).chooseGoalArgument
        chooseGoalAdapter = ChooseGoalAdapter {
            viewModel.selectedGoal.value = it
        }
        binding.rvGoals.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvGoals.adapter = chooseGoalAdapter

        viewModel.getAllGoals().observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                is Result.Error -> {
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_LONG).show()
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    chooseGoalAdapter.submitList(it.data)
                }
            }
        }

        viewModel.selectedGoal.observe(viewLifecycleOwner) {
            binding.submitBtn.isEnabled = it.isNotEmpty()
        }

        binding.submitBtn.setOnClickListener {
            submit()
        }
    }

    private fun submit() = try {
        val goalIds = viewModel.selectedGoal.value?.map { it.id!! }
        viewModel.chooseGoalArgument.organization.goalsId = goalIds?.sorted()
        with(viewModel.chooseGoalArgument) {
            viewModel.register(email, password, imageFile, organization).observe(viewLifecycleOwner) {
                when(it) {
                    is Result.Loading -> {
                        binding.submitBtn.visibility = View.GONE
                        binding.submitBtnLoading.root.visibility = View.VISIBLE
                    }
                    is Result.Error -> {
                        binding.submitBtn.visibility = View.VISIBLE
                        binding.submitBtnLoading.root.visibility = View.GONE
                        Toast.makeText(requireContext(), it.error, Toast.LENGTH_LONG).show()
                    }
                    is Result.Success -> {
                        startActivity(Intent(requireContext(), Class.forName("com.app.organizationpanel.ui.OrganizationPanelActivity")))
                        requireActivity().finish()
                    }
                }
            }
        }
    } catch (e: Exception) {
        Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}