package com.app.wecollabs.ui.organizationDetail

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.LATITUDE
import com.app.core.data.LONGITUDE
import com.app.core.data.Result
import com.app.core.data.models.Organization
import com.app.core.ui.VerticalProgramAdapter
import com.app.wecollabs.R
import com.app.wecollabs.databinding.ActivityOrganizationDetailBinding
import com.app.wecollabs.ui.programDetail.ProgramDetailActivity
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrganizationDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityOrganizationDetailBinding
    private var selectedOrganization: Organization? = null
    private val viewModel: OrganizationDetailViewModel by viewModel()
    private lateinit var verticalProgramAdapter: VerticalProgramAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrganizationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
        loadPrograms()
    }

    private fun initVariable() {
        selectedOrganization = intent.getParcelableExtra(SELECTED_ORGANIZATION)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.organizationMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
        selectedOrganization?.let { organization ->
            with(binding) {
                Glide.with(this@OrganizationDetailActivity)
                    .load(organization.imageUrl)
                    .into(profile.logo)
                profile.name.text = organization.name
                desc.description.text = organization.description
                address.address.text = organization.address
            }
        }
    }

    private fun initListener() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.profile.contactBtn.setOnClickListener {
            selectedOrganization?.let { organization ->
                val number = organization.phoneNumber
                if(number != null) {
                    val url = "https://api.whatsapp.com/send?phone=${number.replaceFirst("0", "+62")}"
                    try {
                        val pm: PackageManager = this.packageManager
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                    } catch (e: PackageManager.NameNotFoundException) {
                        Toast.makeText(this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    } catch (e: Exception) {
                        Toast.makeText(this@OrganizationDetailActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@OrganizationDetailActivity, getString(com.app.core.R.string.not_available), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadPrograms() = with(binding) {
        verticalProgramAdapter = VerticalProgramAdapter {
            Intent().apply {
                putExtra(ProgramDetailActivity.SELECTED_PROGRAM, it)
                setClass(this@OrganizationDetailActivity, ProgramDetailActivity::class.java)
                startActivity(this)
            }
        }
        selectedOrganization?.let { organization ->
            organization.id?.let {
                viewModel.getPrograms(it).observe(this@OrganizationDetailActivity) { result ->
                    when(result) {
                        is Result.Loading -> {}
                        is Result.Error -> {}
                        is Result.Success -> {
                            if (result.data.isNotEmpty()) {
                                with(binding) {
                                    separator.visibility = View.VISIBLE
                                    programText.visibility = View.VISIBLE
                                    rvPrograms.visibility = View.VISIBLE
                                    rvPrograms.setHasFixedSize(true)
                                    rvPrograms.layoutManager = LinearLayoutManager(this@OrganizationDetailActivity)
                                    rvPrograms.adapter = verticalProgramAdapter
                                    verticalProgramAdapter.submitList(result.data)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val SELECTED_ORGANIZATION = "organization"
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        selectedOrganization?.let { organization ->
            organization.addressCoordinate?.let {
                val latitude = it[LATITUDE]
                val longitude = it[LONGITUDE]
                if(latitude != null && longitude != null) {
                    val eventLocation = LatLng(latitude, longitude)
                    mMap.addMarker(MarkerOptions().position(eventLocation).title(getString(R.string.location)))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 15f))
                }
            }
        }
    }
}