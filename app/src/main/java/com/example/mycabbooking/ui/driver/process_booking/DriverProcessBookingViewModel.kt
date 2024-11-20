package com.example.mycabbooking.ui.driver.process_booking

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cabbooking.model.User
import com.google.firebase.firestore.DocumentReference

class DriverProcessBookingViewModel : ViewModel() {
    private val currentUserObject: MutableLiveData<com.example.cabbooking.model.User>
    private val currentBookingDocRef: MutableLiveData<DocumentReference>
    private val checkoutDone: MutableLiveData<Boolean>

    init {
        currentUserObject = MutableLiveData<User>()
        currentBookingDocRef = MutableLiveData<DocumentReference>()
        checkoutDone = MutableLiveData<Boolean>()
    }

    fun setCurrentUserObject(currentUserObject: com.example.cabbooking.model.User?) {
        this.currentUserObject.setValue(currentUserObject)
    }

    fun setCheckoutDone(checkoutDone: Boolean?) {
        this.checkoutDone.setValue(checkoutDone)
    }

    fun getCheckoutDone(): MutableLiveData<Boolean> {
        return checkoutDone
    }

    fun setCurrentBookingDocRef(currentBookingDocRef: DocumentReference?) {
        this.currentBookingDocRef.setValue(currentBookingDocRef)
    }

    fun getCurrentUserObject(): MutableLiveData<com.example.cabbooking.model.User> {
        return this.currentUserObject
    }

    fun getCurrentBookingDocRef(): MutableLiveData<DocumentReference> {
        return currentBookingDocRef
    }
}
