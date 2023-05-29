package com.app.wecollabs.ui.start.fragments.register.forUser

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.app.core.data.Result
import com.app.core.data.models.User
import com.app.core.utils.FileHelper
import com.app.core.utils.Validator
import com.app.wecollabs.R
import com.app.wecollabs.databinding.FragmentRegisterUserBinding
import com.app.wecollabs.ui.main.MainActivity
import com.google.android.material.datepicker.MaterialDatePicker
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterUserViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            userImageIv.setOnClickListener { startGallery() }
            chooseImageTv.setOnClickListener { startGallery() }
            birthDateInput.setOnClickListener { openDatePicker() }
            loginBtn.setOnClickListener { findNavController().popBackStack() }
            genderInput.setOnItemClickListener { _, _, i, _ ->
                viewModel.selectedGender = i
            }
            registerBtn.setOnClickListener { register() }
            val genders = resources.getStringArray(com.app.core.R.array.gender)
            val genderAdapter = ArrayAdapter(requireContext(), com.app.core.R.layout.dropdown_item, genders)
            genderInput.setAdapter(genderAdapter)
        }
    }

    private fun register() {
        if(isInputValid()) {
            val user = User(
                name = viewModel.username,
                gender = viewModel.selectedGender,
                dateOfBirth = viewModel.selectedDateOfBirth,
                phoneNumber = viewModel.phoneNumber,
                email = viewModel.email
            )
            viewModel.register(user).observe(viewLifecycleOwner) {
                when(it) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                    }
                    is Result.Success -> {
                        showLoading(false)
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    private fun isInputValid() : Boolean = with(binding) {
        var isValid = true
        clearError()
        viewModel.username = nameInput.text.toString()
        viewModel.phoneNumber = numberInput.text.toString()
        viewModel.email = emailInput.text.toString()
        viewModel.password = passwordInput.text.toString()
        viewModel.confirmPassword = confirmPasswordInput.text.toString()

        if(viewModel.username.isEmpty()) {
            nameLayout.error = getString(R.string.error_username_input)
            isValid = false
        }

        if(viewModel.selectedGender == null) {
            genderLayout.error = getString(R.string.error_gender_input)
            isValid = false
        }

        if(viewModel.selectedDateOfBirth == null) {
            birthDateLayout.error = getString(R.string.error_selectedDate_input)
            isValid = false
        }

        if(viewModel.phoneNumber.isEmpty()) {
            numberLayout.error = getString(R.string.error_phone_number_input)
            isValid = false
        }

        if(viewModel.email.isEmpty()) {
            emailLayout.error = getString(R.string.error_email_input)
            isValid = false
        }

        if(!Validator.isEmailValid(viewModel.email)) {
            emailLayout.error = getString(R.string.error_email_format)
            isValid = false
        }

        if(viewModel.password.isEmpty()) {
            passwordLayout.error = getString(R.string.error_password_input)
            isValid = false
        }

        if(viewModel.confirmPassword != viewModel.password) {
            confirmPasswordLayout.error = getString(R.string.error_confirm_password_input)
            isValid = false
        }

        return isValid
    }

    private fun clearError() = with(binding) {
        nameLayout.error = null
        genderLayout.error = null
        birthDateLayout.error = null
        numberLayout.error = null
        emailLayout.error = null
        passwordLayout.error = null
        confirmPasswordLayout.error = null
    }

    private fun showLoading(isLoading: Boolean) = with(binding) {
        if(isLoading) {
            registerBtn.visibility = View.GONE
            registerLoadingBtn.root.visibility = View.VISIBLE
        } else {
            registerBtn.visibility = View.VISIBLE
            registerLoadingBtn.root.visibility = View.GONE
        }
    }

    private fun openDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.select_date))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
        datePicker.show(requireActivity().supportFragmentManager, "DATE")
        datePicker.addOnPositiveButtonClickListener {
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val calendar: Calendar = Calendar.getInstance()
            calendar.timeInMillis = it
            val date = formatter.format(calendar.time)
            viewModel.selectedDateOfBirth = date.toString()
            binding.birthDateInput.setText(date.toString())
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_image))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            viewModel.selectedImageFile = FileHelper.reduceFileImage(FileHelper.uriToFile(selectedImg, requireContext()))
            binding.userImageIv.setImageURI(selectedImg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}