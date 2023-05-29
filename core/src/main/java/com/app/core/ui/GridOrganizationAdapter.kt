package com.app.core.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.core.R
import com.app.core.data.ORGANIZATION_ID
import com.app.core.data.PROGRAM_COLLECTION
import com.app.core.data.models.Organization
import com.app.core.databinding.GridOrganizationBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class GridOrganizationAdapter(private val onClick: (Organization) -> Unit): ListAdapter<Organization, GridOrganizationAdapter.Holder>(DIFF_CALLBACK) {

    inner class Holder(private val binding: GridOrganizationBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(organization: Organization) = with(binding) {
            Glide.with(this.root)
                .load(organization.imageUrl)
                .placeholder(R.color.grey3)
                .error(R.color.grey3)
                .into(logo)
            name.text = organization.name
            getEventLength(organization)
            this.root.setOnClickListener { onClick(organization) }
        }

        private fun getEventLength(organization: Organization) = with(binding) {
            programCount.text = StringBuilder("0 Program")
            FirebaseFirestore.getInstance().collection(PROGRAM_COLLECTION)
                .whereEqualTo(ORGANIZATION_ID, organization.id)
                .get()
                .addOnSuccessListener { docs ->
                    Log.d("GridOrganizationAdapter", "getEventLength: ${organization.name} [${organization.id}] = ${docs.size()} Program")
                    programCount.text = StringBuilder("${docs.size()} Program")
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = GridOrganizationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Organization> =
            object : DiffUtil.ItemCallback<Organization>() {
                override fun areItemsTheSame(old: Organization, new: Organization): Boolean {
                    return old.id == new.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(old: Organization, new: Organization): Boolean {
                    return old == new
                }
            }
    }

}