package com.app.wecollabs.ui.eventDetail

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.app.core.data.IMAGE
import com.app.core.data.LATITUDE
import com.app.core.data.LONGITUDE
import com.app.core.data.SHOW_IMAGE_ACTIVITY
import com.app.core.data.models.Event
import com.app.core.utils.DataMapper
import com.app.wecollabs.R
import com.app.wecollabs.databinding.ActivityDetailEventBinding
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailEventActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityDetailEventBinding
    private var selectedEvent: Event? = null
    private val viewModel: DetailEventViewModel by viewModel()
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initVariable()
        initListener()
        setView()
    }

    private fun initVariable() {
        selectedEvent = intent.getParcelableExtra(SELECTED_EVENT)
        if(selectedEvent?.addressCoordinate != null) {
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
                setClass(this@DetailEventActivity, Class.forName(SHOW_IMAGE_ACTIVITY))
                putExtra(IMAGE, selectedEvent?.imageUrl)
                startActivity(this)
            }
        }
        toolbarDetail.setNavigationOnClickListener {
            finish()
        }
        detailContent.readMoreBtn.setOnClickListener {
            detailContent.eventDescription.maxLines = selectedEvent?.description.toString().length
            detailContent.readMoreBtn.visibility = View.GONE
        }
        detailContent.contactBtn.root.setOnClickListener {
            selectedEvent?.let { event ->
                val number = event.contactNumber
                if(number != null) {
                    val url = "https://api.whatsapp.com/send?phone=${number.replaceFirst("0", "+62")}"
                    try {
                        val pm: PackageManager = this@DetailEventActivity.packageManager
                        pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES)
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        startActivity(i)
                    } catch (e: PackageManager.NameNotFoundException) {
                        Toast.makeText(this@DetailEventActivity, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    } catch (e: Exception) {
                        Toast.makeText(this@DetailEventActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@DetailEventActivity, getString(com.app.core.R.string.not_available), Toast.LENGTH_SHORT).show()
                }
            }
        }
        checkFavorite()
    }

    private fun checkFavorite() = lifecycleScope.launch {
        selectedEvent?.let { event ->
            isFavorite = viewModel.isEventFavorite(event.id)
            if (isFavorite) binding.favBtn.setImageResource(com.app.core.R.drawable.ic_favorite)
            else binding.favBtn.setImageResource(com.app.core.R.drawable.ic_favorite_outline)
            binding.favBtn.setOnClickListener {
                setFavoriteTo(!isFavorite, event)
            }
        }
    }

    private fun setFavoriteTo(isFav: Boolean, event: Event) = lifecycleScope.launch {
        isFavorite = isFav
        if (isFav) {
            viewModel.insertFavoriteEvent(DataMapper.mapEventToEntity(event))
            binding.favBtn.setImageResource(com.app.core.R.drawable.ic_favorite)
        } else {
            viewModel.deleteFavoriteEvent(DataMapper.mapEventToEntity(event))
            binding.favBtn.setImageResource(com.app.core.R.drawable.ic_favorite_outline)
        }
    }

    private fun setView() = with(binding.detailContent) {
        selectedEvent?.let { event ->
            eventTitle.text = event.title
            Glide.with(this@DetailEventActivity)
                .load(event.imageUrl)
                .placeholder(com.app.core.R.color.grey3)
                .error(com.app.core.R.color.grey3)
                .into(eventImage)
            setOrganizationName()
            eventDescription.text = event.description
            if(event.eventDate != null) {
                eventTime.eventDate.text = event.eventDate
            } else {
                eventTime.root.visibility = View.GONE
            }
            if(event.address != null) {
                eventAddress.address.text = event.address
            } else {
                eventAddress.root.visibility = View.GONE
            }
            contactBtn.title.text = getString(R.string.contact_organization)
            contactBtn.icon.setImageResource(com.app.core.R.drawable.ic_call)
        }
    }

    private fun setOrganizationName() = selectedEvent?.let { event ->
        if (event.organizationId == null) {
            binding.detailContent.eventOrganizationName.text = event.organizationName
        } else {
            //TODO : Load organization from database
        }
    }

    // SET MAP
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        selectedEvent?.let { event ->
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
        const val SELECTED_EVENT = "event"
    }
}