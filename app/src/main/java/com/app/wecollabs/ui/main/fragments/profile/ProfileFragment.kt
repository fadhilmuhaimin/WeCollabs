package com.app.wecollabs.ui.main.fragments.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.app.core.data.models.User
import com.app.wecollabs.R
import com.app.wecollabs.databinding.FragmentProfileBinding
import com.app.wecollabs.ui.start.StartActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            User.currentUser?.let { user ->
                Glide.with(requireContext())
                    .load(user.imageUrl)
                    .placeholder(com.app.core.R.drawable.ic_user_default)
                    .error(com.app.core.R.drawable.ic_user_default)
                    .into(userImageIv)
                userNameTv.text = user.name
                userEmailTv.text = user.email
            }

            editProfileBtn.actionNameTv.text = getString(R.string.edit_profile)
            editProfileBtn.root.setOnClickListener {
                Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
            }

            logOutBtn.actionNameTv.text = getString(R.string.logout)
            logOutBtn.root.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(requireActivity(), StartActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}