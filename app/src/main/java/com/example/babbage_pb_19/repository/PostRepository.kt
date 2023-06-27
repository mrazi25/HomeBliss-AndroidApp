package com.example.babbage_pb_19.repository

import androidx.lifecycle.MutableLiveData
import com.example.babbage_pb_19.data.Post
import com.google.firebase.database.*

class PostRepository {
    private val dbPostReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Post")

    @Volatile private var INSTANCE : PostRepository ?= null

    fun getInstance() : PostRepository{
        return INSTANCE ?: synchronized(this){
            val instance = PostRepository()
            INSTANCE = instance
            instance
        }


    }


    fun loadUsers(postList : MutableLiveData<List<Post>>){
        dbPostReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _postList : List<Post> = snapshot.children.map { dataSnapshot ->
                        dataSnapshot.getValue(Post::class.java)!!
                    }
                    postList.postValue(_postList)
                }catch (e : Exception){
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


    }
}