package com.example.babbage_pb_19.repositories

import androidx.lifecycle.MutableLiveData
import com.example.babbage_pb_19.datas.Discussion
import com.google.firebase.database.*

class DiscussionRepository {
    private val dbDiscussionReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Discussion")
    @Volatile private var instance: DiscussionRepository? = null

    fun getInstance(): DiscussionRepository {
        return instance ?: synchronized(this) {
            val instance = DiscussionRepository()
            this.instance = instance
            instance
        }
    }
    fun loadDiscussions(postid: String, discussionList: MutableLiveData<List<Discussion>>) {
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
                                val discussionObj = Discussion(discussion, commenter, img)
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
                error.message
            }
        })
    }
}
