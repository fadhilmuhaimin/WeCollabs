package com.app.organizationpanel.ui.addProgram

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.Observer
import com.app.core.data.CHOOSE_LOCATION_ACTIVITY
import com.app.core.data.LATITUDE
import com.app.core.data.LONGITUDE
import com.app.core.data.Result
import com.app.core.data.models.Organization
import com.app.core.data.models.Program
import com.app.core.utils.DateHelper
import com.app.core.utils.FileHelper
import com.app.organizationpanel.R
import com.app.organizationpanel.databinding.ActivityAddProgramBinding
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.StringBuilder

class AddProgramActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProgramBinding
    private val viewModel: AddProgramViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProgramBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
    }

    private fun initVariable() {
        binding.titleToolbar.text = getString(R.string.add_program)
        binding.addContent.comingSoonLayout.visibility = View.GONE
        binding.addContent.actionBtn.text = getString(R.string.add_program)
        binding.addContent.deleteBtn.text = getString(R.string.delete_program)
        viewModel.selectedProgram = intent.getParcelableExtra(SELECTED_PROGRAM)
        if (viewModel.selectedProgram != null) {
            Log.d("AddProgramActivity", "selectedProgram: ${viewModel.selectedProgram}")
            setFieldValueWithSelectedProgram()
        }
    }

    private fun initListener() = with(binding) {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        with(addContent) {
            actionBtn.setOnClickListener {
                if (viewModel.selectedProgram == null) {
                    addProgram()
                } else {
                    updateProgram()
                }
            }
            deleteBtn.setOnClickListener {
                viewModel.selectedProgram?.id?.let { deleteProgram(it) }
            }
            chooseImageBtn.setOnClickListener {
                startGallery()
            }
            coordinateInput.setOnClickListener {
                openChooseLocationActivity()
            }
        }
    }

    private fun deleteProgram(programId: String) {
        viewModel.deleteProgram(programId).observe(this) {
            when(it) {
                is Result.Loading -> {}
                is Result.Error -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Success -> {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun setFieldValueWithSelectedProgram() = viewModel.selectedProgram?.let { program ->
        with(binding.addContent) {
            Glide.with(this@AddProgramActivity)
                .load(program.imageUrl)
                .placeholder(com.app.core.R.color.grey2)
                .error(com.app.core.R.color.grey2)
                .into(imageIv)
            titleInput.setText(program.title)
            descInput.setText(program.description)
            dateInput.setText(program.date)
            addressInput.setText(program.address)
            numberInput.setText(program.contactNumber)
            viewModel.addressCoordinate = program.addressCoordinate
            if(viewModel.addressCoordinate != null) coordinateInput.setText(StringBuilder("${viewModel.addressCoordinate?.get(LATITUDE)?.toFloat()}, ${viewModel.addressCoordinate?.get(LONGITUDE)?.toFloat()}"))
            actionBtn.text = getString(R.string.update_program)
            deleteBtn.visibility = View.VISIBLE
        }
    }

    private fun isInputValid(): Boolean = with(binding.addContent) {
        var isValid = true
        viewModel.apply {
            title = titleInput.text.toString()
            desc = descInput.text.toString()
            contactNumber = numberInput.text.toString()
            val date = dateInput.text.toString()
            val address = addressInput.text.toString()
            if(date.isNotEmpty()) viewModel.date = date
            if(address.isNotEmpty()) viewModel.address = address
        }

        if (viewModel.selectedProgram == null && viewModel.imageFile == null) {
            Toast.makeText(this@AddProgramActivity, "Please pick image", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (viewModel.title.isEmpty()) {
            titleLayout.error = getString(com.app.wecollabs.R.string.error_required_field)
            isValid = false
        }

        if (viewModel.desc.isEmpty()) {
            descLayout.error = getString(com.app.wecollabs.R.string.error_required_field)
            isValid = false
        }

        if (viewModel.contactNumber.isEmpty()) {
            numberLayout.error = getString(com.app.wecollabs.R.string.error_required_field)
            isValid = false
        }

        return isValid
    }

    private fun addProgram() {
        val image = viewModel.imageFile
        if (isInputValid() && image != null) {
            val program = Program(
                title = viewModel.title,
                date = viewModel.date,
                description = viewModel.desc,
                address = viewModel.address,
                addressCoordinate = viewModel.addressCoordinate,
                contactNumber = viewModel.contactNumber,
                organizationId = Organization.currentOrganization?.id,
                createdAt = DateHelper.getCurrentDate(),
            )
            viewModel.addProgram(program, image).observe(this, observer)
        }
    }

    private fun updateProgram() {
        if (isInputValid()) {
            val program = Program(
                id = viewModel.selectedProgram?.id.toString(),
                title = viewModel.title,
                imageUrl = viewModel.selectedProgram?.imageUrl,
                date = viewModel.date,
                description = viewModel.desc,
                address = viewModel.address,
                createdAt = viewModel.selectedProgram?.createdAt,
                addressCoordinate = viewModel.addressCoordinate,
                contactNumber = viewModel.contactNumber,
                organizationId = Organization.currentOrganization?.id,
                viewerCounter = viewModel.selectedProgram?.viewerCounter
            )
            viewModel.updateProgram(program, viewModel.imageFile).observe(this, observer)
        }
    }

    private val observer = (Observer<Result<Program>> {
        when(it) {
            is Result.Loading -> {
                showLoading(true)
            }
            is Result.Error -> {
                showLoading(false)
                Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
            }
            is Result.Success -> {
                showLoading(false)
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    })

    private fun openChooseLocationActivity() {
        val intent = Intent(this, Class.forName(CHOOSE_LOCATION_ACTIVITY))
        launcherChooseLocation.launch(intent)
    }

    private val launcherChooseLocation =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val latitude = data?.getDoubleExtra(LATITUDE, 0.0)
                val longitude = data?.getDoubleExtra(LONGITUDE, 0.0)
                if (latitude != null && longitude != null) {
                    viewModel.addressCoordinate = mapOf(
                        LATITUDE to latitude,
                        LONGITUDE to longitude
                    )
                    binding.addContent.coordinateInput.setText(StringBuilder("${latitude.toFloat()}, ${longitude.toFloat()}"))
                }
            }
        }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser =
            Intent.createChooser(intent, getString(com.app.wecollabs.R.string.choose_image))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImg: Uri = result.data?.data as Uri
                viewModel.imageFile =
                    FileHelper.reduceFileImage(FileHelper.uriToFile(selectedImg, this))
                binding.addContent.imageIv.setImageURI(selectedImg)
            }
        }

    private fun showLoading(isLoading: Boolean) = with(binding.addContent) {
        if(isLoading) {
            actionBtn.visibility = View.GONE
            loadingBtn.root.visibility = View.VISIBLE
        } else {
            actionBtn.visibility = View.VISIBLE
            loadingBtn.root.visibility = View.GONE
        }
    }

    companion object {
        const val SELECTED_PROGRAM = "program"
    }
}