package com.example.mycabbooking.ui.driver.alert

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cabbooking.model.Booking

class DriverAlertViewModel : ViewModel() {
    val booking: MutableLiveData<Booking> = MutableLiveData()

    fun setBooking(booking: Booking) {
        this.booking.value = booking
    }
}
