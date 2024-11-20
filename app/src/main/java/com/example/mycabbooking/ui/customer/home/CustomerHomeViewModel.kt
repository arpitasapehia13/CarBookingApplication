package com.example.mycabbooking.ui.customer.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cabbooking.model.User

class CustomerHomeViewModel : ViewModel() {
    val currentUserObject: MutableLiveData<User> =
        MutableLiveData()

    fun setCurrentUserObject(currentUserObject: User) {
        this.currentUserObject.value = currentUserObject
    }
}