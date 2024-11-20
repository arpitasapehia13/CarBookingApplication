package com.example.mycabbooking.ui.driver.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cabbooking.model.User

class DriverHomeViewModel : ViewModel() {
    val currentUserObject: MutableLiveData<User> =
        MutableLiveData()
    val acceptBookingBtnPressed: MutableLiveData<Boolean> = MutableLiveData()

    fun setCurrentUserObject(currentUserObject: User) {
        this.currentUserObject.value = currentUserObject
    }

    fun setAcceptBookingBtnPressed(acceptBookingBtnPressed: Boolean) {
        this.acceptBookingBtnPressed.value = acceptBookingBtnPressed
    }
}