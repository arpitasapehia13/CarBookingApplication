package com.example.mycabbooking.ui.driver.driver_info

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cabbooking.model.User

class DriverInfoViewModel : ViewModel() {
    val currentUserObject: MutableLiveData<User> =
        MutableLiveData()

    fun setCurrentUserObject(currentUserObject: User) {
        this.currentUserObject.value = currentUserObject
    }
}