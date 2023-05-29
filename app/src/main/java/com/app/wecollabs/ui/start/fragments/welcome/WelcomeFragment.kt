package com.app.wecollabs.ui.start.fragments.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.core.data.ORGANIZATION
import com.app.core.data.USER
import com.app.wecollabs.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnUser.root.setOnClickListener {
                gotoLogin(USER)
            }
            btnNgo.root.setOnClickListener {
                gotoLogin(ORGANIZATION)
            }
        }
    }

    private fun gotoLogin(type: String) {
        val destination = WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment(type)
        findNavController().navigate(destination)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}