package com.app.core.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.core.data.USER_COLLECTION
import com.app.core.data.models.User
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import com.app.core.data.Result
import com.app.core.data.USER_IMAGE_STORAGE
import com.google.firebase.storage.FirebaseStorage

class UserRepository {
    private val userRef = FirebaseFirestore.getInstance().collection(USER_COLLECTION)
    private val authRef = FirebaseAuth.getInstance()

    // METHOD FOR LOGIN
    fun login(email: String, password: String): LiveData<Result<User>> {
        val state = MutableLiveData<Result<User>>()
        state.value = Result.Loading
        authRef.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loadUser(state, it)
            }
            .addOnFailureListener {
                Log.e(TAG, "login: $it")
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
        return state
    }

    private fun loadUser(loginState: MutableLiveData<Result<User>>, it: AuthResult) {
        val authResult = it.user
        authResult?.let {
            FirebaseFirestore.getInstance().collection(USER_COLLECTION)
                .whereEqualTo("id", authResult.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty) {
                        loginState.value = Result.Error("User not found")
                        return@addOnSuccessListener
                    }
                    for(document in documents) {
                        val user = document.toObject(User::class.java)
                        User.currentUser = user
                        loginState.value = Result.Success(user)
                        Log.d(TAG, "loadUser: $user")
                        return@addOnSuccessListener
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "loadUser: $it")
                    it.localizedMessage?.let { msg ->
                        loginState.value = Result.Error(msg)
                    }
                }
        }
    }

    //METHOD FOR REGISTER
    fun register(email: String, password: String, imageFile: File?, user: User): LiveData<Result<User>> {
        val registerState = MutableLiveData<Result<User>>()
        registerState.value = Result.Loading
        authRef.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result?.let {
                    user.id = it.user?.uid
                    createUser(registerState, imageFile, user)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "register: $it")
                registerState.value = it.localizedMessage?.let { it1 -> Result.Error(it1) }
            }
        return registerState
    }

    private fun createUser(registerState: MutableLiveData<Result<User>>, imageFile: File?, user: User) {
        if(imageFile != null) {
            val storageRef = FirebaseStorage.getInstance().getReference("$USER_IMAGE_STORAGE/${user.id}/${System.currentTimeMillis()}.jpg")
            val uploadTask = storageRef.putFile(Uri.fromFile(imageFile))
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener {
                user.imageUrl = it.toString()
                saveUser(registerState, user)
            }.addOnFailureListener {
                Log.e(TAG, "addOnFailureListener: $it")
                registerState.value = Result.Error(it.toString())
            }
        } else {
            saveUser(registerState, user)
        }
    }

    private fun saveUser(registerState: MutableLiveData<Result<User>>, user: User) {
        Log.d(TAG, "saveUser: $user")
        User.currentUser = user
        user.id?.let {
            userRef.document(it).set(user)
                .addOnSuccessListener {
                    registerState.value = Result.Success(user)
                }
                .addOnFailureListener { e ->
                    e.localizedMessage?.let { msg ->
                        registerState.value = Result.Error(msg)
                    }
                }
        }
    }

    companion object {
        const val TAG = "UserRepository"
    }

}