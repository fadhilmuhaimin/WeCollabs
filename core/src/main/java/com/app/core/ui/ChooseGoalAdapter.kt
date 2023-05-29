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

class ChooseGoalAdapter(private val selectHandler: (MutableList<Goal>) -> Unit): ListAdapter<Goal, ChooseGoalAdapter.Holder>(DIFF_CALLBACK) {
    private lateinit var context: Context
    var selectedGoal = mutableListOf<Goal>()

    inner class Holder(private val binding: ItemGoalBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(goal: Goal) = with(binding) {
            Glide.with(this.root)
                .load(goal.iconUrl)
                .error(R.drawable.ic_icon)
                .placeholder(R.drawable.ic_icon)
                .into(goalIcon)
            goalName.text = goal.name?.get(EN)
            if(selectedGoal.contains(goal)) {
                this.root.background.setTint(context.resources.getColor(R.color.primary_color_30, null))
            } else {
                this.root.background.setTint(context.resources.getColor(R.color.white, null))
            }
            this.root.setOnClickListener {
                if(selectedGoal.contains(goal)) {
                    selectedGoal.remove(goal)
                } else {
                    selectedGoal.add(goal)
                }
                selectHandler(selectedGoal)
                notifyItemChanged(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseGoalAdapter.Holder {
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