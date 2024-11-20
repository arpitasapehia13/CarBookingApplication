package com.example.mycabbooking.ui.user_profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cabbooking.model.User

class UserProfileViewModel : ViewModel() {
    val currentUserObject: MutableLiveData<User> =
        MutableLiveData()

    fun setCurrentUserObject(currentUserObject: User) {
        this.currentUserObject.value = currentUserObject
    }
}