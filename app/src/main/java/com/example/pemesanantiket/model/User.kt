package com.example.pemesanantiket.model

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class User(
        var bio : String? = "",
        var email : String? = "",
        var name : String? = "",
        var password : String? = "",
        var urlPhoto : String? = "",
        var balance : Int? = 0,
        var username : String = ""
) : Parcelable