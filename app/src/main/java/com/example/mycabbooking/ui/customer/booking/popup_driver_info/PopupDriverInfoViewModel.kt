package com.example.mycabbooking.ui.customer.booking.popup_driver_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cabbooking.model.User

class PopupDriverInfoViewModel : ViewModel() {
    val driver: MutableLiveData<User> =
        MutableLiveData()

    fun setDriver(driver: User) {
        this.driver.value = driver
    }
}