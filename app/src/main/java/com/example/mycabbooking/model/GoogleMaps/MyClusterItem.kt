package com.example.mycabbooking.model.GoogleMaps

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.maps.android.clustering.ClusterItem

class MyClusterItem(
    private val position: LatLng,
    private val title: String,
    private val snippet: String,
    private val icon: BitmapDescriptor
) : ClusterItem {

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    fun getIconBitMapDescriptor(): BitmapDescriptor {
        return icon
    }
}
