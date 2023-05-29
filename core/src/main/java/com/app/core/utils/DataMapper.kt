package com.app.core.utils

import com.app.core.data.LATITUDE
import com.app.core.data.LONGITUDE
import com.app.core.data.local.entity.EventEntity
import com.app.core.data.models.Event

object DataMapper {
    fun mapEventEntityToEvent(list: List<EventEntity>): List<Event> {
        return list.map {
            Event(
                id = it.id,
                title = it.title,
                imageUrl = it.imageUrl,
                eventDate = it.date,
                description = it.description,
                address = it.address,
                addressCoordinate = mapOf(
                    LATITUDE to it.latitude,
                    LONGITUDE to it.longitude
                ),
                contactNumber = it.contactNumber,
                organizationName = it.organizationName,
                isTrending = it.isTrending,
                isComingSoon = it.isComingSoon
            )
        }
    }

    fun mapEventToEntity(event: Event): EventEntity {
        return EventEntity(
            id = event.id,
            title = event.title,
            imageUrl = event.imageUrl,
            date = event.eventDate,
            description = event.description,
            address = event.address,
            latitude = event.addressCoordinate?.get(LATITUDE),
            longitude = event.addressCoordinate?.get(LONGITUDE),
            contactNumber = event.contactNumber,
            organizationName = event.organizationName,
            organizationId = event.organizationId,
            isTrending = event.isTrending,
            isComingSoon = event.isComingSoon
        )
    }

    fun mapEntityToEvent(it: EventEntity): Event {
        return Event(
            id = it.id,
            title = it.title,
            imageUrl = it.imageUrl,
            eventDate = it.date,
            description = it.description,
            address = it.address,
            addressCoordinate = mapOf(
                LATITUDE to it.latitude,
                LONGITUDE to it.longitude
            ),
            contactNumber = it.contactNumber,
            organizationName = it.organizationName,
            isTrending = it.isTrending,
            isComingSoon = it.isComingSoon
        )
    }
}