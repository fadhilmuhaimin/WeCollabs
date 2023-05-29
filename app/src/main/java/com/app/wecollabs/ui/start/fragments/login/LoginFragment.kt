package com.app.wecollabs.ui.start.fragments.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.app.core.data.LOGIN_AS
import com.app.core.data.ORGANIZATION_PANEL_ACTIVITY
import com.app.core.data.Result
import com.app.core.data.USER
import com.app.core.data.models.Organization
import com.app.core.data.models.User
import com.app.core.utils.Validator
import com.app.wecollabs.R
import com.app.wecollabs.databinding.FragmentLoginBinding
import com.app.wecollabs.ui.main.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var loginType: String
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginType = LoginFragmentArgs.fromBundle(arguments as Bundle).type
        with(binding) {
            if (loginType == USER) {
                loginTypeTv.text = StringBuilder("( ${getString(R.string.user)} )")
                registerBtn.setOnClickListener {
                    findNavController().navigate(R.id.action_loginFragment_to_registerUserFragment)
                }
            } else {
                loginTypeTv.text = StringBuilder("( ${getString(R.string.organization)} )")
                registerBtn.setOnClickListener {
                    findNavController().navigate(R.id.action_loginFragment_to_registerNGOFragment)
                }
            }
            loginBtn.setOnClickListener {
                login()
            }
        }
    }

    private fun login() = with(binding) {
        if (isInputClear()) {
            if (loginType == USER) {
                viewModel.loginUser(emailInput.text.toString(), passwordInput.text.toString())
                    .observe(viewLifecycleOwner, loginUserObserver)
            } else {
                viewModel.loginOrganization(emailInput.text.toString(), passwordInput.text.toString())
                    .observe(viewLifecycleOwner, loginOrganizationObserver)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) = with(binding) {
        if (isLoading) {
            loginBtn.visibility = View.GONE
            loginLoadingBtn.root.visibility = View.VISIBLE
        } else {
            loginBtn.visibility = View.VISIBLE
            loginLoadingBtn.root.visibility = View.GONE
        }
    }

    private fun isInputClear(): Boolean = with(binding) {
        var valid = true
        clearError()

        if (emailInput.text.toString().isEmpty()) {
            emailLayout.error = resources.getString(R.string.error_email_input)
            valid = false
        } else if (!Validator.isEmailValid(emailInput.text.toString())) {
            emailLayout.error = resources.getString(R.string.error_email_format)
            valid = false
        }

        if (passwordInput.text.toString().length < 6) {
            passwordLayout.error = resources.getString(R.string.error_password_input)
            valid = false
        }

        return valid
    }

    private fun clearError() = with(binding) {
        emailLayout.apply {
            error = null
            clearFocus()
        }
        passwordLayout.apply {
            error = null
            clearFocus()
        }
    }

    private val loginUserObserver = Observer<Result<User>> {
        when (it) {
            is Result.Loading -> {
                showLoading(true)
            }
            is Result.Error -> {
                showLoading(false)
                Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
            }
            is Result.Success -> {
                saveUserLoginState(loginType)
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    private val loginOrganizationObserver = Observer<Result<Organization>> {
        when (it) {
            is Result.Loading -> {
                showLoading(true)
            }
            is Result.Error -> {
                showLoading(false)
                Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
            }
            is Result.Success -> {
                saveUserLoginState(loginType)
                startActivity(Intent(requireContext(), Class.forName(ORGANIZATION_PANEL_ACTIVITY)))
                requireActivity().finish()
            }
        }
    }

    private fun saveUserLoginState(loginType: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = pref.edit()
        editor.putString(LOGIN_AS, loginType)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


}