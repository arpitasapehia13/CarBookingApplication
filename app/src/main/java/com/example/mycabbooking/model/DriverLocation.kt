package com.example.cabbooking.model

import com.google.firebase.firestore.DocumentId

/**
 * Data model for driver location
 */
class DriverLocation {
    @DocumentId
    var docId: String? = null
    var currentPositionLatitude: Double? = null
    var currentPositionLongitude: Double? = null

    constructor()

    constructor(
        docId: String?,
        currentPositionLatitude: Double?,
        currentPositionLongitude: Double?
    ) {
        this.docId = docId
        this.currentPositionLatitude = currentPositionLatitude
        this.currentPositionLongitude = currentPositionLongitude
    }
}
