package com.app.organizationpanel.ui.organizationPanel.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.app.core.data.models.Organization
import com.app.organizationpanel.databinding.FragmentProfileBinding
import com.app.wecollabs.ui.start.StartActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAccountLayout()
    }

    private fun setAccountLayout() = with(binding.account) {
        Glide.with(requireContext())
            .load(Organization.currentOrganization?.imageUrl)
            .placeholder(com.app.core.R.color.grey3)
            .error(com.app.core.R.color.grey3)
            .into(logoIv)
        organizationName.text = Organization.currentOrganization?.name
        editProfileBtn.setOnClickListener {  }
        logOutBtn.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(requireActivity(), StartActivity::class.java))
        requireActivity().finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}