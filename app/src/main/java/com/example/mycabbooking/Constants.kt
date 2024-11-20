package com.example.mycabbooking

class Constants {
    class Transportation {
        object Type {
            const val carType: String = "car"
            const val bikeType: String = "bike"
        }

        object UnitPrice {
            const val carType: Double = 20.0
            const val bikeType: Double = 10.0
        }
    }

    //Fields of FireStore 'users' collection
    object FSUser {
        const val userCollection: String = "users"
        const val usernameField: String = "username"
        const val phoneField: String = "phone"
        const val birthDateField: String = "birthDate"
        const val genderField: String = "gender"
        const val emailField: String = "email"
        const val roleField: String = "role"
        const val transportationType: String = "transportationType"
        const val vehiclePlateNumber: String = "vehiclePlateNumber"
        const val rating: String = "rating"
        const val currentPositionLatitude: String = "currentPositionLatitude"
        const val currentPositionLongitude: String = "currentPositionLongitude"


        const val roleCustomerVal: String = "Customer"
        const val roleDriverVal: String = "Driver"
    }

    object FSBooking {
        const val bookingCollection: String = "bookings"
        const val pickupPlaceAddress: String = "pickupPlaceAddress"
        const val dropOffPlaceAddress: String = "dropOffPlaceAddress"
        const val pickUpPlaceLatitude: String = "pickUpPlaceLatitude"
        const val pickUpPlaceLongitude: String = "pickUpPlaceLongitude"
        const val dropOffPlaceLatitude: String = "dropOffPlaceLatitude"
        const val dropOffPlaceLongitude: String = "dropOffPlaceLongitude"

        const val driver: String = "driver"
        const val distanceInKm: String = "distanceInKm"
        const val priceInVND: String = "priceInVND"
        const val transportationType: String = "transportationType"
        const val available: String = "available"
        const val arrived: String = "arrived"
        const val finished: String = "finished"
    }

    object FSDriverLocation {
        const val driverLocationCollection: String = "driverLocations"
        const val currentPositionLatitude: String = "currentPositionLatitude"
        const val currentPositionLongitude: String = "currentPositionLongitude"
    }


    //All Toast messages being used
    object ToastMessage {
        const val emptyInputError: String = "Please fill in your account authentication."
        const val signInSuccess: String = "Sign in successfully!"
        const val signInFailure: String = "Invalid email/password!"
        const val registerSuccess: String = "Successfully registered"
        const val registerFailure: String =
            "Authentication failed, email must be unique and has correct form!"
        const val retrieveUsersInfoFailure: String = "Error querying for all users' information!"
        const val emptyMessageInputError: String = "Please type your message to send!"

        //Create site validation message
        const val placeAutocompleteError: String = "Google PlaceAutocomplete error with code: "


        //Maps Error Handling
        const val currentLocationNotUpdatedYet: String =
            "Please wait for a few seconds for current location to be updated!"
        const val routeRenderingInProgress: String = "Please wait, the route is being rendered!"

        //Edit site Message
        const val editSiteSuccess: String = "Edit site successfully!"

        //Booking error
        const val addNewBookingToDbFail: String = "Fail to create new booking"
    }

    object PlaceAddressComponentTypes {
        const val premise: String = "premise"
        const val streetNumber: String = "street_number"
        const val route: String = "route"
        const val adminAreaLv1: String = "administrative_area_level_1"
        const val adminAreaLv2: String = "administrative_area_level_2"
        const val country: String = "country"
    }

    object MenuItemsIndex {
        const val myCreatedSitesItemIndex: Int = 0
        const val joinSitesItemIndex: Int = 1
        const val createSiteItemIndex: Int = 2
    }

    class GoogleMaps {
        object CameraZoomLevel {
            const val city: Int = 10
            const val streets: Int = 15
            const val buildings: Int = 20

            const val betweenCityAndStreets: Float = 12.5.toFloat()
            const val betweenStreetsAndBuildings: Float = 17.5.toFloat()
        }

        object DirectionApi {
            const val baseUrl: String = "https://maps.googleapis.com/maps/api/directions/"
            const val originParam: String = "origin"
            const val destinationParam: String = "destination"
            const val modeParam: String = "mode"
            const val outputParam: String = "json"
        }
    }

    object Notification {
        var CHANNEL_ID: String = "GAC"
        var CHANNEL_NAME: String = "GreenAndClean notification"
        var CHANNEL_DES: String = "GreenAndClean app notification"
        var title: String = "Green&Clean notification"
        var onSiteChangeTextContent: String =
            "There has been some changes made to one of your participating site, click to see..."
    }
}
