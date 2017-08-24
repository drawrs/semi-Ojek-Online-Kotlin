package com.khilman.mapkotlinretrofit.Init

import com.google.gson.annotations.SerializedName

class Route {

    @SerializedName("copyrights")
    var copyrights: String? = null
    @SerializedName("legs")
    var legs: List<Leg>? = null
    @SerializedName("overview_polyline")
    var overviewPolyline: OverviewPolyline? = null
    @SerializedName("summary")
    var summary: String? = null
    @SerializedName("warnings")
    var warnings: List<Any>? = null
    @SerializedName("waypoint_order")
    var waypointOrder: List<Any>? = null

}
