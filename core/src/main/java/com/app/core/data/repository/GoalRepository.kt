package com.app.core.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.core.data.GOALS_COLLECTION
import com.app.core.data.Result
import com.app.core.data.models.Goal
import com.google.firebase.firestore.FirebaseFirestore

class GoalRepository {
    private val goalRef = FirebaseFirestore.getInstance().collection(GOALS_COLLECTION)

    fun getListGoalsWithLimit(limit: Long): LiveData<Result<List<Goal>>> {
        val state = MutableLiveData<Result<List<Goal>>>()
        state.value = Result.Loading
        goalRef
            .limit(limit)
            .get()
            .addOnSuccessListener {
                val list = mutableListOf<Goal>()
                for(doc in it) {
                    try {
                        val goal = doc.toObject(Goal::class.java)
                        list.add(goal)
                    } catch (e: Exception) {
                        Log.e("GoalRepository", "getListGoalsWithLimit: $e")
                    }
                }
                state.value = Result.Success(list)
            }
            .addOnFailureListener {
                Log.e("GoalRepository", "getListGoalsWithLimit: $it")
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
        return state
    }

    fun getAllGoals(): LiveData<Result<List<Goal>>> {
        val state = MutableLiveData<Result<List<Goal>>>()
        state.value = Result.Loading
        goalRef
            .get()
            .addOnSuccessListener {
                val list = mutableListOf<Goal>()
                for(doc in it) {
                    try {
                        val goal = doc.toObject(Goal::class.java)
                        list.add(goal)
                    } catch (e: Exception) {
                        Log.e("GoalRepository", "getAllGoals: $e")
                    }
                }
                state.value = Result.Success(list)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
        return state
    }
}