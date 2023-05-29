package com.app.core.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.core.data.*
import com.app.core.data.models.Organization
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class OrganizationRepository {
    private val organizationRef = FirebaseFirestore.getInstance().collection(ORGANIZATION_COLLECTION)
    private val authRef = FirebaseAuth.getInstance()

    // METHODE FOR AUTHENTICATION
    // METHOD FOR LOGIN
    fun login(email: String, password: String): LiveData<Result<Organization>> {
        val state = MutableLiveData<Result<Organization>>()
        state.value = Result.Loading
        authRef.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                loadOrganization(state, it)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
        return state
    }

    private fun loadOrganization(loginState: MutableLiveData<Result<Organization>>, it: AuthResult) {
        val authResult = it.user
        authResult?.let {
            FirebaseFirestore.getInstance().collection(ORGANIZATION_COLLECTION)
                .whereEqualTo("id", authResult.uid)
                .get()
                .addOnSuccessListener { documents ->
                    if(documents.isEmpty) {
                        loginState.value = Result.Error("Organization not found")
                        return@addOnSuccessListener
                    }
                    for(document in documents) {
                        val organization = document.toObject(Organization::class.java)
                        Organization.currentOrganization = organization
                        loginState.value = Result.Success(organization)
                        return@addOnSuccessListener
                    }
                }
                .addOnFailureListener {
                    it.localizedMessage?.let { msg ->
                        loginState.value = Result.Error(msg)
                    }
                }
        }
    }

    //METHOD FOR REGISTER
    fun register(email: String, password: String, imageFile: File?, organization: Organization): LiveData<Result<Organization>> {
        val registerState = MutableLiveData<Result<Organization>>()
        registerState.value = Result.Loading
        authRef.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                result?.let {
                    organization.id = it.user?.uid
                    createOrganization(registerState, imageFile, organization)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "register: $it")
                registerState.value = it.localizedMessage?.let { it1 -> Result.Error(it1) }
            }
        return registerState
    }

    private fun createOrganization(registerState: MutableLiveData<Result<Organization>>, imageFile: File?, organization: Organization) {
        if(imageFile != null) {
            val storageRef = FirebaseStorage.getInstance().getReference("$ORGANIZATION_IMAGE_STORAGE/${organization.id}/${System.currentTimeMillis()}.jpg")
            val uploadTask = storageRef.putFile(Uri.fromFile(imageFile))
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener {
                organization.imageUrl = it.toString()
                saveOrganization(registerState, organization)
            }.addOnFailureListener {
                Log.e(TAG, "addOnFailureListener: $it")
                registerState.value = it.localizedMessage?.let { it1 -> Result.Error(it1) }
            }
        } else {
            saveOrganization(registerState, organization)
        }
    }

    private fun saveOrganization(registerState: MutableLiveData<Result<Organization>>, organization: Organization) {
        Log.d(TAG, "saveUser: $organization")
        Organization.currentOrganization = organization
        organization.id?.let {
            organizationRef.document(it).set(organization)
                .addOnSuccessListener {
                    registerState.value = Result.Success(organization)
                }
                .addOnFailureListener { e ->
                    e.localizedMessage?.let { msg ->
                        registerState.value = Result.Error(msg)
                    }
                }
        }
    }

    //OTHER METHOD
    fun getOrganizations(goalId: String): LiveData<Result<List<Organization>>> {
        val state = MutableLiveData<Result<List<Organization>>>()
        state.value = Result.Loading
        organizationRef
            .whereArrayContains(GOAL_ID, goalId)
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Organization>()
                for(doc in docs) {
                    try {
                        val organization = doc.toObject(Organization::class.java)
                        list.add(organization)
                    } catch (e: Exception) {
                        Log.e(TAG, "getOrganizations: $e")
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

    fun getOrganization(id: String) : LiveData<Result<Organization>> {
        val state = MutableLiveData<Result<Organization>>()
        state.value = Result.Loading
        organizationRef
            .whereEqualTo(ID, id)
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Organization>()
                for(doc in docs) {
                    try {
                        val organization = doc.toObject(Organization::class.java)
                        list.add(organization)
                    } catch (e: Exception) {
                        Log.e(TAG, "getOrganizations: $e")
                    }
                }
                state.value = Result.Success(list[0])
            }
            .addOnFailureListener {
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
        return state
    }

    companion object {
        const val TAG = "OrganizationRepository"
    }
}