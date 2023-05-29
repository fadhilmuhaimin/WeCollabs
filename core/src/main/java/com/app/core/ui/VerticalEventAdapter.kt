package com.app.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.core.R
import com.app.core.data.models.Event
import com.app.core.data.models.Organization
import com.app.core.databinding.VerticalEventBinding
import com.bumptech.glide.Glide

class VerticalEventAdapter(private val onClick: (Event) -> Unit): ListAdapter<Event, VerticalEventAdapter.Holder>(DIFF_CALLBACK) {

    inner class Holder(private val binding: VerticalEventBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(event: Event) = with(binding) {
            Glide.with(this.root)
                .load(event.imageUrl)
                .placeholder(R.color.grey3)
                .error(R.color.grey3)
                .into(eventImage)
            eventIcon.setImageResource(R.drawable.ic_ngo)
            if(event.organizationId == null) {
                eventOrganizationName.text = event.organizationName
            } else {
                val org = Organization.currentOrganization
                if(org != null && org.id == event.organizationId) {
                    eventOrganizationName.text = org.name
                } else {
                    //load org name
                }
            }
            eventTitle.text = event.title
            this.root.setOnClickListener { onClick(event) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = VerticalEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Event> =
            object : DiffUtil.ItemCallback<Event>() {
                override fun areItemsTheSame(old: Event, new: Event): Boolean {
                    return old.id == new.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(old: Event, new: Event): Boolean {
                    return old == new
                }
            }
    }

}