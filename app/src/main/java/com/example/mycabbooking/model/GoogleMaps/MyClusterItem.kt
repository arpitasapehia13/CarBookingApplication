package com.example.cabbooking.model.GoogleMaps

import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * ClusterItem class
 */
class MyClusterItem(lat: Double, lng: Double, iconBitMapDescriptor: BitmapDescriptor) :
    ClusterItem {
    private val position: LatLng
    val title: String
    val snippet: String
    private val iconBitMapDescriptor: BitmapDescriptor

    init {
        position = LatLng(lat, lng)
        this.title = "cc"
        this.snippet = "cl"
        //        this.title = site.getSiteName();
//        this.snippet = site.getSiteName();;
        this.iconBitMapDescriptor = iconBitMapDescriptor
    }

    override fun getPosition(): LatLng {
        return position
    }

    fun getIconBitMapDescriptor(): BitmapDescriptor {
        return iconBitMapDescriptor
    }
}
