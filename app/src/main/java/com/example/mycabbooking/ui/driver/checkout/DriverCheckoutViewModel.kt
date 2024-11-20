package com.example.mycabbooking.ui.driver.checkout

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DriverCheckoutViewModel : ViewModel() {
    val priceInVNDString: MutableLiveData<String> = MutableLiveData()

    fun setPriceInVNDString(priceInVNDString: String) {
        this.priceInVNDString.value = priceInVNDString
    }
}
