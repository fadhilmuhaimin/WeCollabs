package com.app.core.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.core.R
import com.app.core.data.ID
import com.app.core.data.ORGANIZATION_COLLECTION
import com.app.core.data.models.Organization
import com.app.core.data.models.Program
import com.app.core.databinding.VerticalEventBinding
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class VerticalProgramAdapter(private val onClick: (Program) -> Unit): ListAdapter<Program, VerticalProgramAdapter.Holder>(DIFF_CALLBACK) {

    inner class Holder(private val binding: VerticalEventBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(program: Program) = with(binding) {
            Glide.with(this.root)
                .load(program.imageUrl)
                .placeholder(R.color.grey3)
                .error(R.color.grey3)
                .into(eventImage)
            setOwner(program)
            eventTitle.text = program.title
            this.root.setOnClickListener { onClick(program) }
        }

        private fun setOwner(program: Program) {
            FirebaseFirestore.getInstance().collection(ORGANIZATION_COLLECTION)
                .whereEqualTo(ID, program.organizationId)
                .get()
                .addOnSuccessListener { docs ->
                    for(doc in docs) {
                        try {
                            val org = doc.toObject(Organization::class.java)
                            binding.eventOrganizationName.text = org.name
                        } catch (e: Exception) {}
                    }
                }
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
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Program> =
            object : DiffUtil.ItemCallback<Program>() {
                override fun areItemsTheSame(old: Program, new: Program): Boolean {
                    return old.id == new.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(old: Program, new: Program): Boolean {
                    return old == new
                }
            }
    }

}