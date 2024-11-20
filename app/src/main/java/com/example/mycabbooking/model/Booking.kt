package com.example.cabbooking.model

import com.google.firebase.firestore.DocumentId

/**
 * Data model for booking
 */
class Booking {
    @DocumentId
    var docId: String? = null
    var pickupPlaceAddress: String? = null
    var dropOffPlaceAddress: String? = null
    var pickUpPlaceLatitude: Double? = null
    var pickUpPlaceLongitude: Double? = null
    var dropOffPlaceLatitude: Double? = null
    var dropOffPlaceLongitude: Double? = null
    private var driver: com.example.cabbooking.model.User? = null
    var distanceInKm: String? = null
    var priceInVND: String? = null
    var transportationType: String? = null
    var available: Boolean? = null
    var arrived: Boolean? = null
    var finished: Boolean? = null

    constructor()

    constructor(
        docId: String?,
        pickupPlaceAddress: String?,
        dropOffPlaceAddress: String?,
        pickUpPlaceLatitude: Double?,
        pickUpPlaceLongitude: Double?,
        dropOffPlaceLatitude: Double?,
        dropOffPlaceLongitude: Double?,
        driver: com.example.cabbooking.model.User?,
        distanceInKm: String?,
        priceInVND: String?,
        transportationType: String?,
        available: Boolean?,
        arrived: Boolean?,
        finished: Boolean?
    ) {
        this.docId = docId
        this.pickupPlaceAddress = pickupPlaceAddress
        this.dropOffPlaceAddress = dropOffPlaceAddress
        this.pickUpPlaceLatitude = pickUpPlaceLatitude
        this.pickUpPlaceLongitude = pickUpPlaceLongitude
        this.dropOffPlaceLatitude = dropOffPlaceLatitude
        this.dropOffPlaceLongitude = dropOffPlaceLongitude
        this.driver = driver
        this.distanceInKm = distanceInKm
        this.priceInVND = priceInVND
        this.transportationType = transportationType
        this.available = available
        this.arrived = arrived
        this.finished = finished
    }

    fun getDriver(): com.example.cabbooking.model.User? {
        return driver
    }

    fun setDriver(driver: com.example.cabbooking.model.User?) {
        this.driver = driver
    }
}
