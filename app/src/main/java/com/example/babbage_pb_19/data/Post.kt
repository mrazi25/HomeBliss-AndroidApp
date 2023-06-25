package com.example.babbage_pb_19.data

import android.net.wifi.WifiManager.SubsystemRestartTrackingCallback
import android.os.Parcelable
import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import java.util.*

data class Post(val caption: String?= "",
                val judul: String?= "",
                val poster_img: String?= "",
                val poster_name: String?= "",
                val poster_uid: String?= "",
                val postid: String?= "",
                val postpict: String?= "") :java.io.Serializable {

}