package com.example.mycabbooking.ui.customer.booking.checkout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cabbooking.model.User

/**
 * View model for CheckoutFragment
 */
class CheckoutViewModel : ViewModel() {
    val currentUserObject: MutableLiveData<User> =
        MutableLiveData()
    val transportationType: MutableLiveData<String> = MutableLiveData()
    val distanceInKmString: MutableLiveData<String> = MutableLiveData()
    val priceInVNDString: MutableLiveData<String> = MutableLiveData()

    fun setCurrentUserObject(currentUserObject: User) {
        this.currentUserObject.value = currentUserObject
    }

    fun setTransportationType(transportationType: String) {
        this.transportationType.value = transportationType
    }

    fun setPriceInVNDString(priceInVNDString: String) {
        this.priceInVNDString.value = priceInVNDString
    }

    fun setDistanceInKmString(distanceInKmString: String) {
        this.distanceInKmString.value = distanceInKmString
    }
}