package com.example.mycabbooking.ui.customer.booking.pickup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cabbooking.model.User

/**
 * View model for PickUpFragment
 */
class PickupViewModel : ViewModel() {
    val currentUserObject: MutableLiveData<User> =
        MutableLiveData()

    fun setCurrentUserObject(currentUserObject: User) {
        this.currentUserObject.value = currentUserObject
    }
}
