package com.app.organizationpanel.ui.addEvent

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
import com.app.core.data.models.Event
import com.app.core.data.models.Organization
import com.app.core.utils.DateHelper
import com.app.core.utils.FileHelper
import com.app.organizationpanel.R
import com.app.organizationpanel.databinding.ActivityAddEventBinding
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.lang.StringBuilder

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    private val viewModel: AddEventViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
    }

    private fun initVariable() {
        binding.titleToolbar.text = getString(R.string.add_event)
        binding.addContent.deleteBtn.text = getString(R.string.delete_event)
        viewModel.selectedEvent = intent.getParcelableExtra(SELECTED_EVENT)
        if (viewModel.selectedEvent != null) {
            Log.d("AddEventActivity", "AddEventActivity: ${viewModel.selectedEvent}")
            setFieldValueWithSelectedEvent()
        }
    }

    private fun initListener() = with(binding) {
        toolbar.setNavigationOnClickListener {
            finish()
        }
        with(addContent) {
            actionBtn.setOnClickListener {
                if (viewModel.selectedEvent == null) {
                    addEvent()
                } else {
                    updateEvent()
                }
            }
            deleteBtn.setOnClickListener {
                viewModel.selectedEvent?.id?.let { it1 -> deleteEvent(it1) }
            }
            chooseImageBtn.setOnClickListener {
                startGallery()
            }
            coordinateInput.setOnClickListener {
                openChooseLocationActivity()
            }
        }
    }

    private fun deleteEvent(eventId: String) {
        viewModel.deleteEvent(eventId).observe(this) {
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

    private fun setFieldValueWithSelectedEvent() = viewModel.selectedEvent?.let { event ->
        with(binding.addContent) {
            Glide.with(this@AddEventActivity)
                .load(event.imageUrl)
                .placeholder(com.app.core.R.color.grey2)
                .error(com.app.core.R.color.grey2)
                .into(imageIv)
            titleInput.setText(event.title)
            descInput.setText(event.description)
            dateInput.setText(event.eventDate)
            addressInput.setText(event.address)
            numberInput.setText(event.contactNumber)
            viewModel.addressCoordinate = event.addressCoordinate
            if(viewModel.addressCoordinate != null) coordinateInput.setText(StringBuilder("${viewModel.addressCoordinate?.get(LATITUDE)?.toFloat()}, ${viewModel.addressCoordinate?.get(LONGITUDE)?.toFloat()}"))
            actionBtn.text = getString(R.string.update_event)
            deleteBtn.visibility = View.VISIBLE
            event.isComingSoon?.let { comingSoonSwithBtn.isChecked = it }
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
            isComingSoon = comingSoonSwithBtn.isChecked
        }

        if (viewModel.selectedEvent == null && viewModel.imageFile == null) {
            Toast.makeText(this@AddEventActivity, "Please pick image", Toast.LENGTH_SHORT).show()
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

    private fun addEvent() {
        val image = viewModel.imageFile
        if (isInputValid() && image != null) {
            val event = Event(
                title = viewModel.title,
                eventDate = viewModel.date,
                description = viewModel.desc,
                address = viewModel.address,
                addressCoordinate = viewModel.addressCoordinate,
                contactNumber = viewModel.contactNumber,
                organizationId = Organization.currentOrganization?.id,
                createdAt = DateHelper.getCurrentDate(),
                isComingSoon = viewModel.isComingSoon,
                isTrending = false
            )
            viewModel.addEvent(event, image).observe(this, observer)
        }
    }

    private fun updateEvent() {
        if (isInputValid()) {
            val event = Event(
                id = viewModel.selectedEvent?.id.toString(),
                title = viewModel.title,
                imageUrl = viewModel.selectedEvent?.imageUrl,
                eventDate = viewModel.date,
                description = viewModel.desc,
                address = viewModel.address,
                createdAt = viewModel.selectedEvent?.createdAt,
                addressCoordinate = viewModel.addressCoordinate,
                contactNumber = viewModel.contactNumber,
                organizationId = Organization.currentOrganization?.id,
                isComingSoon = viewModel.isComingSoon,
                isTrending = viewModel.selectedEvent?.isTrending,
                viewerCounter = viewModel.selectedEvent?.viewerCounter
            )
            viewModel.updateEvent(event, viewModel.imageFile).observe(this, observer)
        }
    }

    private val observer = (Observer<Result<Event>> {
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
        const val SELECTED_EVENT = "event"
    }
}