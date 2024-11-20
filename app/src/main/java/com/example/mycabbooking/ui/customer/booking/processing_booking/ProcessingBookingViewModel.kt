package com.example.mycabbooking.ui.customer.booking.processing_booking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * View model for ProcessingBookingFragment
 */
class ProcessingBookingViewModel : ViewModel() {
    val dropOffPlaceString: MutableLiveData<String> = MutableLiveData()
    val pickupPlaceString: MutableLiveData<String> = MutableLiveData()
    val priceInVNDString: MutableLiveData<String> = MutableLiveData()

    fun setPriceInVNDString(priceInVNDString: String) {
        this.priceInVNDString.value = priceInVNDString
    }

    fun setDropOffPlaceString(dropOffPlaceString: String) {
        this.dropOffPlaceString.value = dropOffPlaceString
    }

    fun setPickupPlaceString(pickupPlaceString: String) {
        this.pickupPlaceString.value = pickupPlaceString
    }
}