package com.khilman.mapkotlinretrofit.Init

import com.google.gson.annotations.SerializedName


class Distance {

    @SerializedName("text")
    var text: String? = null
    @SerializedName("value")
    var value: Long? = null

}
