package com.app.core.data.repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.core.data.*
import com.app.core.data.local.LocalDataSource
import com.app.core.data.local.entity.EventEntity
import com.app.core.data.models.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class EventRepository(private val localDataSource: LocalDataSource) {

    private val eventRef = FirebaseFirestore.getInstance().collection(EVENT_COLLECTION)

    fun getTrendingEvents(limit: Long): LiveData<Result<List<Event>>> {
        val state = MutableLiveData<Result<List<Event>>>()
        state.value = Result.Loading
        eventRef
            .whereEqualTo(PROPERTY_TRENDING, true)
            .limit(limit)
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Event>()
                for(doc in docs) {
                    try {
                        val event = doc.toObject(Event::class.java)
                        list.add(event)
                    } catch (e: Exception) {
                        Log.e(TAG, "getTrendingEvents: $e")
                    }
                }
                Log.d(TAG, "getTrendingEvents: ${list.size}")
                state.value = Result.Success(list)
            }
            .addOnFailureListener {
                Log.e(TAG, "getTrendingEvents: $it")
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }

        return state
    }

    fun getComingSoonEvents(limit: Long): LiveData<Result<List<Event>>> {
        val state = MutableLiveData<Result<List<Event>>>()
        state.value = Result.Loading
        eventRef
            .whereEqualTo(PROPERTY_COMING_SOON, true)
            .limit(limit)
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Event>()
                for(doc in docs) {
                    try {
                        val event = doc.toObject(Event::class.java)
                        list.add(event)
                    } catch (e: Exception) {
                        Log.e(TAG, "getComingSoonEvents: $e")
                    }
                }
                Log.d(TAG, "getComingSoonEvents: ${list.size}")
                state.value = Result.Success(list)
            }
            .addOnFailureListener {
                Log.e(TAG, "getComingSoonEvents: $it")
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }

        return state
    }

    fun getEvents(): LiveData<Result<List<Event>>> {
        val state = MutableLiveData<Result<List<Event>>>()
        state.value = Result.Loading
        eventRef
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Event>()
                for(doc in docs) {
                    try {
                        val event = doc.toObject(Event::class.java)
                        list.add(event)
                    } catch (e: Exception) {
                        Log.e(TAG, "getEvents: $e")
                    }
                }
                state.value = Result.Success(list)
            }
            .addOnFailureListener {
                Log.e(TAG, "getEvents: $it")
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }

        return state
    }

    fun getEvents(organizationId: String): LiveData<Result<List<Event>>> {
        val state = MutableLiveData<Result<List<Event>>>()
        state.value = Result.Loading
        eventRef
            .whereEqualTo(ORGANIZATION_ID, organizationId)
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Event>()
                for(doc in docs) {
                    Log.d("EventRepository", "getEvents: $doc")
                    try {
                        val event = doc.toObject(Event::class.java)
                        list.add(event)
                    } catch (e: Exception) {
                        Log.e(TAG, "getEvents: $e")
                    }
                }
                state.value = Result.Success(list)
            }
            .addOnFailureListener {
                Log.e(TAG, "getEvents: $it")
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }

        return state
    }

    fun searchEvents(keyword: String):LiveData<Result<List<Event>>> {
        val state = MutableLiveData<Result<List<Event>>>()
        state.value = Result.Loading
        eventRef
            .get()
            .addOnSuccessListener { docs ->
                val list = mutableListOf<Event>()
                for(doc in docs) {
                    try {
                        val event = doc.toObject(Event::class.java)
                        if(event.title?.contains(keyword) == true || event.description?.contains(keyword) == true) {
                            list.add(event)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "getEvents: $e")
                    }
                }
                state.value = Result.Success(list)
            }
            .addOnFailureListener {
                Log.e(TAG, "getEvents: $it")
                it.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }

        return state
    }

    fun addEvent(event: Event, image: File): LiveData<Result<Event>> {
        val state = MutableLiveData<Result<Event>>()
        state.value = Result.Loading
        val key = eventRef.document().id
        event.id = key

        val storageRef = FirebaseStorage.getInstance().getReference("$EVENT_COLLECTION/${event.organizationId}/${System.currentTimeMillis()}.jpg")
        val uploadTask = storageRef.putFile(Uri.fromFile(image))
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@continueWithTask storageRef.downloadUrl
        }.addOnSuccessListener {
            event.imageUrl = it.toString()

            //ADD TO DATABASE
            eventRef
                .document(event.id)
                .set(event)
                .addOnSuccessListener { state.value = Result.Success(event) }
                .addOnFailureListener { err -> state.value = Result.Error(err.toString()) }

        }.addOnFailureListener {
            Log.e("EventRepository", "addOnFailureListener: $it")
            state.value = it.localizedMessage?.let { it1 -> Result.Error(it1) }
        }

        return state
    }

    fun updateEvent(event: Event, image: File?): LiveData<Result<Event>> {
        val state = MutableLiveData<Result<Event>>()
        state.value = Result.Loading
        if(image != null) {
            val storageRef = FirebaseStorage.getInstance().getReference("$EVENT_COLLECTION/${event.organizationId}/${System.currentTimeMillis()}.jpg")
            val uploadTask = storageRef.putFile(Uri.fromFile(image))
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@continueWithTask storageRef.downloadUrl
            }.addOnSuccessListener {
                event.imageUrl = it.toString()
                updateEventOnDatabase(state, event)
            }.addOnFailureListener {
                Log.e("EventRepository", "addOnFailureListener: $it")
                state.value = it.localizedMessage?.let { it1 -> Result.Error(it1) }
            }
        } else {
            updateEventOnDatabase(state, event)
        }

        return state
    }

    private fun updateEventOnDatabase(state: MutableLiveData<Result<Event>>, event: Event) {
        eventRef
            .document(event.id)
            .update(mapOf(
                Event.PROPERTY_TITLE to event.title,
                Event.PROPERTY_IMAGE to event.imageUrl,
                Event.PROPERTY_DATE to event.eventDate,
                Event.PROPERTY_DESCRIPTION to event.description,
                Event.PROPERTY_ADDRESS to event.address,
                Event.PROPERTY_COORDINATE to event.addressCoordinate,
                Event.PROPERTY_CONTACT to event.contactNumber,
                PROPERTY_COMING_SOON to event.isComingSoon,
            ))
            .addOnSuccessListener {
                state.value = Result.Success(event)
            }
            .addOnFailureListener { err ->
                err.localizedMessage?.let { msg ->
                    state.value = Result.Error(msg)
                }
            }
    }

    fun deleteEvent(eventId: String): LiveData<Result<Boolean>> {
        val state = MutableLiveData<Result<Boolean>>()
        state.value = Result.Loading
        eventRef
            .document(eventId)
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

    //FAVORITE EVENTS
    fun getFavoriteEvents() = localDataSource.getAllEvent()
    suspend fun insertFavoriteEvent(eventEntity: EventEntity) = localDataSource.insertEvent(eventEntity)
    suspend fun deleteFavoriteEvent(eventEntity: EventEntity) = localDataSource.deleteEvent(eventEntity)
    suspend fun isEventFavorite(eventId: String) = localDataSource.isEventFavorite(eventId)

    companion object {
        const val TAG = "EventRepository"
    }
}