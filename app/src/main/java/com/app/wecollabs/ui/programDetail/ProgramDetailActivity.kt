package com.app.wecollabs.ui.programDetail

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.app.core.data.*
import com.app.core.data.models.Program
import com.app.wecollabs.R
import com.app.wecollabs.databinding.ActivityProgramDetailBinding
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProgramDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityProgramDetailBinding
    private var selectedProgram: Program? = null
    private val viewModel: ProgramDetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProgramDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
        setView()
    }
    private fun initVariable() {
        selectedProgram = intent.getParcelableExtra(SELECTED_PROGRAM)
        if(selectedProgram?.addressCoordinate != null) {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        } else {
            binding.detailContent.mapText.visibility = View.GONE
            binding.detailContent.map.visibility = View.GONE
        }
    }

    private fun initListener() = with(binding) {
        detailContent.eventImage.setOnClickListener {
            Intent().apply {
                setClass(this@ProgramDetailActivity, Class.forName(SHOW_IMAGE_ACTIVITY))
                putExtra(IMAGE, selectedProgram?.imageUrl)
                startActivity(this)
            }
        }
        toolbarDetail.setNavigationOnClickListener {
            finish()
        }
        detailContent.readMoreBtn.setOnClickListener {
            detailContent.eventDescription.maxLines = selectedProgram?.description.toString().length
            detailContent.readMoreBtn.visibility = View.GONE
        }
        detailContent.contactBtn.root.setOnClickListener {
            selectedProgram?.let { program ->
                val number = program.contactNumber
                if(number != null) {
                    val url = "https://api.whatsapp.com/send?phone=${number.replaceFirst("0", "+62")}"
                    try {
                        val pm: PackageManager = this@ProgramDetailActivity.packageManager
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                    } catch (e: PackageManager.NameNotFoundException) {
                        Toast.makeText(this@ProgramDetailActivity, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    } catch (e: Exception) {
                        Toast.makeText(this@ProgramDetailActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@ProgramDetailActivity, getString(com.app.core.R.string.not_available), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setView() = with(binding.detailContent) {
        selectedProgram?.let { program ->
            eventTitle.text = program.title
            Glide.with(this@ProgramDetailActivity)
                .load(program.imageUrl)
                .placeholder(com.app.core.R.color.grey3)
                .error(com.app.core.R.color.grey3)
                .into(eventImage)
            setOrganizationName()
            eventDescription.text = program.description
            if(program.date != null) {
                eventTime.eventDate.text = program.date
            } else {
                eventTime.root.visibility = View.GONE
            }
            if(program.address != null) {
                eventAddress.address.text = program.address
            } else {
                eventAddress.root.visibility = View.GONE
            }

            contactBtn.title.text = getString(R.string.contact_organization)
            contactBtn.icon.setImageResource(com.app.core.R.drawable.ic_call)
        }
    }

    private fun setOrganizationName() = selectedProgram?.let { program ->
        program.organizationId?.let {
            viewModel.getOrganization(it).observe(this) { result ->
                when(result) {
                    is Result.Loading -> {}
                    is Result.Error -> {}
                    is Result.Success -> {
                        binding.detailContent.eventOrganizationName.text = result.data.name
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        selectedProgram?.let { event ->
            event.addressCoordinate?.let {
                val latitude = it[LATITUDE]
                val longitude = it[LONGITUDE]
                if (latitude != null && longitude != null) {
                    val eventLocation = LatLng(latitude, longitude)
                    mMap.addMarker(MarkerOptions().position(eventLocation)
                        .title(getString(R.string.location)))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15f))
                }
            }
        }
    }

    companion object {
        const val SELECTED_PROGRAM = "program"
    }
}