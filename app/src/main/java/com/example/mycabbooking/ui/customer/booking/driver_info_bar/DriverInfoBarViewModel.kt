package com.example.mycabbooking.ui.customer.booking.driver_info_bar

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mycabbooking.model.User

/**
 * View model for DriveInforBarFragment
 */
class DriverInfoBarViewModel : ViewModel() {
    val driver: MutableLiveData<User> =
        MutableLiveData()

    fun setDriver(driver: User) {
        this.driver.value = driver
    }
}