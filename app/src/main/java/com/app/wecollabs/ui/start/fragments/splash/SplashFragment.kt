package com.app.wecollabs.ui.start.fragments.splash

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.app.core.data.*
import com.app.wecollabs.R
import com.app.wecollabs.databinding.FragmentSplashBinding
import com.app.wecollabs.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            if(currentUser == null) {
                delay(1500L)
                findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
            } else {
                getUserLoginState()
            }
        }

    }

    private fun getUserLoginState() {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val loginType = pref.getString(LOGIN_AS, "")
        if(loginType != null) {
            Log.d(TAG, "getUserLoginState: login as = $loginType")
            if(loginType == USER) {
                loadUser()
            } else {
                loadOrganization()
            }
        } else {
            lifecycleScope.launch {
                delay(1500L)
                findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
            }
        }
    }

    private fun loadUser() {
        val uid = currentUser?.uid
        if(uid != null) {
            FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val user = doc.toObject(com.app.core.data.models.User::class.java)
                    com.app.core.data.models.User.currentUser = user
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finish()
                }
                .addOnFailureListener {
                    findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
                }
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        }
    }

    private fun loadOrganization() {
        val uid = currentUser?.uid
        if(uid != null) {
            FirebaseFirestore.getInstance().collection(ORGANIZATION_COLLECTION)
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    val organization = doc.toObject(com.app.core.data.models.Organization::class.java)
                    com.app.core.data.models.Organization.currentOrganization = organization
                    startActivity(Intent(requireContext(), Class.forName(ORGANIZATION_PANEL_ACTIVITY)))
                    requireActivity().finish()
                }
                .addOnFailureListener {
                    findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
                }
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "SplashFragment"
    }

}