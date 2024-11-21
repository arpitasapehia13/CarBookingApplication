package com.example.mycabbooking.ui.customer.booking.popup_driver_arrived

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycabbooking.model.User

/**
 * View model for PopupDriverArrivalFragment
 */
class PopupDriverArrivalViewModel : ViewModel() {
    val driver: MutableLiveData<User> =
        MutableLiveData()

    fun setDriver(driver: User) {
        this.driver.value = driver
    }
}