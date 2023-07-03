package com.example.babbage_pb_19.repositories

import androidx.lifecycle.MutableLiveData
import com.example.babbage_pb_19.datas.Post
import com.google.firebase.database.*

class PostRepository {
    private val dbPostReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Post")
    @Volatile private var instance : PostRepository ?= null

    fun getInstance() : PostRepository{
        return instance ?: synchronized(this){
            val instance = PostRepository()
            this.instance = instance
            instance
        }
    }
    fun loadPosts(postList : MutableLiveData<List<Post>>){
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
                error.message
            }
        })
    }
}