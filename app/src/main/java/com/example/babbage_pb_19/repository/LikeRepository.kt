package com.example.babbage_pb_19.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*

class LikeRepository {
    private val dbLikesReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Likes")

    @Volatile private var instance : LikeRepository ?= null

    fun getInstance() : LikeRepository {
        return instance ?: synchronized(this) {
            val instance = LikeRepository()
            this.instance = instance
            instance
        }
    }
    fun loadLikes(user_id: String, likesList: MutableLiveData<Map<String, Long>>) {
        dbLikesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    var likeMap: MutableMap<String, Long> = mutableMapOf()
                    var sortedMap: MutableMap<String, Long> = mutableMapOf()
                    if (snapshot.exists()) {
                        for (likeSnapshot in snapshot.child(user_id).children) {
                            likeMap[likeSnapshot.key.toString()] = likeSnapshot.value as Long
                            println(likesList)
                        }
                        var sortedList = likeMap.toList().sortedByDescending { (_, value) -> value }
                        sortedMap = sortedList.toMap().toMutableMap()
                    }
                    likesList.postValue(sortedMap)
                    println("Sorted Map -> $sortedMap")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
