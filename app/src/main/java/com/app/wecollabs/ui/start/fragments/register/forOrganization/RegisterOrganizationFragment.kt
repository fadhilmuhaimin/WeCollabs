package com.app.wecollabs.ui.start.fragments.register.forOrganization

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.app.core.data.CHOOSE_LOCATION_ACTIVITY
import com.app.core.data.LATITUDE
import com.app.core.data.LONGITUDE
import com.app.core.data.models.Organization
import com.app.core.utils.FileHelper
import com.app.core.utils.Validator
import com.app.wecollabs.R
import com.app.wecollabs.databinding.FragmentRegisterOrganizationBinding
import java.io.File
import java.lang.StringBuilder

class RegisterOrganizationFragment : Fragment() {

    private var _binding: FragmentRegisterOrganizationBinding? = null
    private val binding get() = _binding!!
    private var selectedImage: File? = null
    private var name = ""
    private var phoneNumber = ""
    private var description = ""
    private var address = ""
    private var addressCoordinate = mapOf<String, Double>()
    private var email = ""
    private var password = ""
    private var confirmPassword = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterOrganizationBinding.inflate(LayoutInflater.from(requireContext()), container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            userImageIv.setOnClickListener { startGallery() }
            chooseImageTv.setOnClickListener { startGallery() }
            nextBtn.setOnClickListener { gotoNextStep() }
            coordinateInput.setOnClickListener { openChooseLocationActivity() }
            loginBtn.setOnClickListener { findNavController().popBackStack() }
        }
    }

    private fun gotoNextStep() {
        if(checkInput()) {
            val organization = Organization(
                name = name,
                phoneNumber = phoneNumber,
                description = description,
                address = address,
                addressCoordinate = addressCoordinate,
                email = email
            )
            val argument = ChooseGoalArgument(organization, selectedImage, email, password)
            val direction = RegisterOrganizationFragmentDirections.actionRegisterNGOFragmentToChooseGoalFragment(argument)
            findNavController().navigate(direction)
        }
    }

    private fun checkInput(): Boolean = with(binding) {
        var isValid = true
        clearError()
        name = nameInput.text.toString()
        phoneNumber = numberInput.text.toString()
        description = descInput.text.toString()
        address = addressInput.text.toString()
        email = emailInput.text.toString()
        password = passwordInput.text.toString()
        confirmPassword = confirmPasswordInput.text.toString()

        if(name.isEmpty()) {
            nameLayout.error = getString(R.string.error_username_input)
            isValid = false
        }

        if(phoneNumber.isEmpty()) {
            numberLayout.error = getString(R.string.error_phone_number_input)
            isValid = false
        }

        if(description.isEmpty()) {
            descLayout.error = "Please fill your description"
            isValid = false
        }

        if(address.isEmpty()) {
            addressLayout.error = getString(R.string.error_input_address)
            isValid = false
        }

        if(addressCoordinate.isEmpty()) {
            coordinateLayout.error = getString(R.string.error_select_coordinate)
            isValid = false
        }

        if(email.isEmpty()) {
            emailLayout.error = getString(R.string.error_email_input)
            isValid = false
        }

        if(!Validator.isEmailValid(email)) {
            emailLayout.error = getString(R.string.error_email_format)
            isValid = false
        }

        if(password.isEmpty()) {
            passwordLayout.error = getString(R.string.error_password_input)
            isValid = false
        }

        if(confirmPassword != password) {
            confirmPasswordLayout.error = getString(R.string.error_confirm_password_input)
            isValid = false
        }

        return isValid
    }

    private fun clearError() = with(binding) {
        nameLayout.error = null
        numberLayout.error = null
        descLayout.error = null
        addressLayout.error = null
        coordinateLayout.error = null
        emailLayout.error = null
        passwordLayout.error = null
        confirmPasswordLayout.error = null
    }

    private fun openChooseLocationActivity() {
        val intent = Intent(requireActivity(), Class.forName(CHOOSE_LOCATION_ACTIVITY))
        launcherChooseLocation.launch(intent)
    }

    private val launcherChooseLocation = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == AppCompatActivity.RESULT_OK) {
            val data = result.data
            val latitude = data?.getDoubleExtra(LATITUDE, 0.0)
            val longitude = data?.getDoubleExtra(LONGITUDE, 0.0)
            if(latitude != null && longitude != null) {
                addressCoordinate = mapOf(
                    LATITUDE to latitude,
                    LONGITUDE to longitude
                )
                binding.coordinateInput.setText(StringBuilder("${latitude.toFloat()}, ${longitude.toFloat()}"))
            }
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
            selectedImage = FileHelper.reduceFileImage(FileHelper.uriToFile(selectedImg, requireContext()))
            binding.userImageIv.setImageURI(selectedImg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}