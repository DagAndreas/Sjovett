package com.in2000_project.BoatApp.maps

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager

/** Is supposed to help us manage the zoneClusters */
class ZoneClusterManager(
    context: Context,
    googleMap: GoogleMap,
) : ClusterManager<ZoneClusterItem>(context, googleMap, MarkerManager(googleMap))