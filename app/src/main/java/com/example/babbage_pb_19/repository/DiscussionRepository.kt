package com.example.babbage_pb_19.repository

import androidx.lifecycle.MutableLiveData
import com.example.babbage_pb_19.data.Discussion
import com.example.babbage_pb_19.data.Post
import com.google.firebase.database.*

import com.google.firebase.database.*

class DiscussionRepository {
    private val dbDiscussionReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Discussion")

    @Volatile
    private var INSTANCE: DiscussionRepository? = null

    fun getInstance(): DiscussionRepository {
        return INSTANCE ?: synchronized(this) {
            val instance = DiscussionRepository()
            INSTANCE = instance
            instance
        }
    }


    fun loadDiscussions(postid: String, discussionList: MutableLiveData<List<Discussion>>) {
        println(postid)
        dbDiscussionReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val discussions: MutableList<Discussion> = mutableListOf()
                    if (snapshot.exists()) {
                        for (commentSnapshot in snapshot.child(postid).children) {
                            val commenter = commentSnapshot.child("commenter").getValue(String::class.java)
                            val discussion = commentSnapshot.child("discussion").getValue(String::class.java)
                            val img = commentSnapshot.child("img").getValue(String::class.java)
                            if (commenter != null && discussion != null && img != null) {
                                val discussionObj = Discussion(commenter, discussion, img)
                                discussions.add(discussionObj)
                            }
                        }
                    }
                    discussionList.postValue(discussions)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}
