package com.app.core.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.core.data.*
import com.app.core.data.models.Program
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ProgramRepository {
    private val programRef = FirebaseFirestore.getInstance().collection(PROGRAM_COLLECTION)

    fun getPrograms(organizationId: String): LiveData<Result<List<Program>>> {
        val state = MutableLiveData<Result<List<Program>>>()
        state.value = Result.Loading
        programRef
            .whereEqualTo(ORGANIZATION_ID, organizationId)
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Program>()
                for(doc in docs) {
                    try {
                        val program = doc.toObject(Program::class.java)
                        list.add(program)
                    } catch (e: Exception) {
                        Log.e("ProgramRepository", "getPrograms: $e")
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

    fun addProgram(program: Program, image: File): LiveData<Result<Program>> {
        val state = MutableLiveData<Result<Program>>()
        state.value = Result.Loading
        val key = programRef.document().id
        program.id = key

        val storageRef = FirebaseStorage.getInstance().getReference("$PROGRAM_COLLECTION/${program.organizationId}/${System.currentTimeMillis()}.jpg")
        val uploadTask = storageRef.putFile(Uri.fromFile(image))
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@continueWithTask storageRef.downloadUrl
        }.addOnSuccessListener {
            program.imageUrl = it.toString()

            //ADD TO DATABASE
            programRef
                .document(program.id)
                .set(program)
                .addOnSuccessListener { state.value = Result.Success(program) }
                .addOnFailureListener { err -> state.value = Result.Error(err.toString()) }

        }.addOnFailureListener {
            Log.e("ProgramRepository", "addOnFailureListener: $it")
            state.value = it.localizedMessage?.let { it1 -> Result.Error(it1) }
        }

        return state
    }

    fun updateProgram(program: Program, image: File?): LiveData<Result<Program>> {
        val state = MutableLiveData<Result<Program>>()
        state.value = Result.Loading
        if(image != null) {
            val storageRef = FirebaseStorage.getInstance().getReference("$PROGRAM_COLLECTION/${program.organizationId}/${System.currentTimeMillis()}.jpg")
            val uploadTask = storageRef.putFile(Uri.fromFile(image))
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener {
                program.imageUrl = it.toString()
                updateProgramOnDatabase(state, program)
            }.addOnFailureListener {
                Log.e("ProgramRepository", "addOnFailureListener: $it")
                state.value = it.localizedMessage?.let { it1 -> Result.Error(it1) }
            }
        } else {
            updateProgramOnDatabase(state, program)
        }

        return state
    }

    private fun updateProgramOnDatabase(state: MutableLiveData<Result<Program>>, program: Program) {
        programRef
            .document(program.id)
            .update(mapOf(
                Program.PROPERTY_TITLE to program.title,
                Program.PROPERTY_IMAGE to program.imageUrl,
                Program.PROPERTY_DATE to program.date,
                Program.PROPERTY_DESCRIPTION to program.description,
                Program.PROPERTY_ADDRESS to program.address,
                Program.PROPERTY_COORDINATE to program.addressCoordinate,
                Program.PROPERTY_CONTACT to program.contactNumber,
            ))
            .addOnSuccessListener {
                state.value = Result.Success(program)
            }
            .addOnFailureListener { err ->
                err.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
    }

    fun deleteProgram(programId: String): LiveData<Result<Boolean>> {
        val state = MutableLiveData<Result<Boolean>>()
        state.value = Result.Loading
        programRef
            .document(programId)
            .delete()
            .addOnSuccessListener {
                state.value = Result.Success(true)
            }
            .addOnFailureListener {
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
        return state
    }
}