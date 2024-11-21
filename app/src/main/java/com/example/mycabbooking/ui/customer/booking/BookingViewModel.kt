package com.example.mycabbooking.ui.customer.booking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycabbooking.model.User
import com.google.android.libraries.places.api.model.Place  // Correct import

/**
 * View model for BookingFragment
 */
class BookingViewModel : ViewModel() {
    private val currentUserObject: MutableLiveData<User>
    private val customerSelectedDropOffPlace: MutableLiveData<Place>
    private val customerSelectedPickupPlace: MutableLiveData<Place>
    private val transportationType: MutableLiveData<String>
    val bookBtnPressed: MutableLiveData<Boolean>
    val cancelBookingBtnPressed: MutableLiveData<Boolean>
    private val feedBackRating: MutableLiveData<Int>

    init {
        currentUserObject = MutableLiveData<User>()
        customerSelectedDropOffPlace = MutableLiveData<Place>()
        customerSelectedPickupPlace = MutableLiveData<Place>()
        transportationType = MutableLiveData<String>()
        bookBtnPressed = MutableLiveData<Boolean>()
        cancelBookingBtnPressed = MutableLiveData<Boolean>()
        feedBackRating = MutableLiveData<Int>()
    }

    fun getTransportationType(): MutableLiveData<String> {
        return transportationType
    }

    fun setTransportationType(transportationType: String?) {
        this.transportationType.setValue(transportationType)
    }

    fun setCurrentUserObject(currentUserObject: User?) {
        this.currentUserObject.setValue(currentUserObject)
    }

    fun setCustomerSelectedDropOffPlace(customerSelectedDropOffPlace: Place) {  // Corrected here
        this.customerSelectedDropOffPlace.setValue(customerSelectedDropOffPlace)
    }

    fun setCustomerSelectedPickupPlace(customerSelectedPickupPlace: Place?) {  // Corrected here
        this.customerSelectedPickupPlace.setValue(customerSelectedPickupPlace)
    }

    fun setBookBtnPressed(bookBtnPressed: Boolean?) {
        this.bookBtnPressed.setValue(bookBtnPressed)
    }

    fun setCancelBookingBtnPressed(cancelBookingBtnPressed: Boolean?) {
        this.cancelBookingBtnPressed.setValue(cancelBookingBtnPressed)
    }

    fun setFeedBackRating(feedBackRating: Int?) {
        this.feedBackRating.setValue(feedBackRating)
    }

    fun getCustomerSelectedPickupPlace(): MutableLiveData<Place> {
        return customerSelectedPickupPlace
    }

    fun getCurrentUserObject(): MutableLiveData<User> {
        return this.currentUserObject
    }

    fun getCustomerSelectedDropOffPlace(): MutableLiveData<Place> {
        return customerSelectedDropOffPlace
    }

    fun getBookBtnPressed(): MutableLiveData<Boolean> {
        return bookBtnPressed
    }

    fun getCancelBookingBtnPressed(): MutableLiveData<Boolean> {
        return cancelBookingBtnPressed
    }

    fun getFeedBackRating(): MutableLiveData<Int> {
        return feedBackRating
    }
}
