package com.app.core.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.core.R
import com.app.core.data.EN
import com.app.core.data.models.Goal
import com.app.core.databinding.ItemGoalBinding
import com.bumptech.glide.Glide

class GoalAdapter(private val onCLick: (Goal) -> Unit): ListAdapter<Goal, GoalAdapter.Holder>(DIFF_CALLBACK) {
    private lateinit var context: Context

    inner class Holder(private val binding: ItemGoalBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: Goal) = with(binding) {
            Glide.with(this.root)
                .load(goal.iconUrl)
                .error(R.drawable.ic_icon)
                .placeholder(R.drawable.ic_icon)
                .into(goalIcon)
            goalName.text = goal.name?.get(EN)
            this.root.setOnClickListener { onCLick(goal) }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalAdapter.Holder {
        this.context = parent.context
        val view = ItemGoalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Goal> =
            object : DiffUtil.ItemCallback<Goal>() {
                override fun areItemsTheSame(old: Goal, new: Goal): Boolean {
                    return old.id == new.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(old: Goal, new: Goal): Boolean {
                    return old == new
                }
            }
    }

}