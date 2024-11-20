package com.example.cabbooking.model.GoogleMaps

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * A CustomClusterItemRender to apply custom icon and clustering rule.
 */
class CustomClusterItemRender(
    context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<MyClusterItem?>?
) :
    DefaultClusterRenderer<MyClusterItem?>(context, map, clusterManager) {
    protected override fun onBeforeClusterItemRendered(
        item: MyClusterItem,
        markerOptions: MarkerOptions
    ) {
        markerOptions.icon(item.getIconBitMapDescriptor())
        super.onBeforeClusterItemRendered(item, markerOptions)
    }

    protected override fun shouldRenderAsCluster(cluster: Cluster<MyClusterItem?>): Boolean {
        return cluster.getSize() > 1
    }
}
