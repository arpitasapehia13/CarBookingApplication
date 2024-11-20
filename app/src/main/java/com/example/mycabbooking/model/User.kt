package com.example.cabbooking.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

/**
 * Data model for user
 */
class User {
    @DocumentId
    var docId: String? = null
    var username: String? = null
    var phone: String? = null
    var birthDate: Date? = null
    var gender: String? = null
    var email: String? = null
    var role: String? = null
    var transportationType: String? = null
    var vehiclePlateNumber: String? = null
    var rating: List<Int>? = null
    var currentPositionLatitude: Double? = null
    var currentPositionLongitude: Double? = null


    constructor()

    constructor(
        docId: String?,
        username: String?,
        phone: String?,
        birthDate: Date?,
        gender: String?,
        email: String?,
        role: String?,
        transportationType: String?,
        vehiclePlateNumber: String?,
        rating: List<Int>?,
        currentPositionLatitude: Double?,
        currentPositionLongitude: Double?
    ) {
        this.docId = docId
        this.username = username
        this.phone = phone
        this.birthDate = birthDate
        this.gender = gender
        this.email = email
        this.role = role
        this.transportationType = transportationType
        this.vehiclePlateNumber = vehiclePlateNumber
        this.rating = rating
        this.currentPositionLatitude = currentPositionLatitude
        this.currentPositionLongitude = currentPositionLongitude
    }
}
