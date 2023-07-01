package com.example.babbage_pb_19.repository

import androidx.lifecycle.MutableLiveData
import com.example.babbage_pb_19.data.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MyPostRepository {
    private val dbPostReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Post")
    private val firebaseUser = FirebaseAuth.getInstance().currentUser

    @Volatile private var instance : MyPostRepository ?= null

    fun getInstance() : MyPostRepository{
        return instance ?: synchronized(this){
            val instance = MyPostRepository()
            this.instance = instance
            instance
        }


    }
    fun loadMyPosts(postList : MutableLiveData<List<Post>>){
        dbPostReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val _postList: MutableList<Post> = mutableListOf()

                    snapshot.children.forEach { dataSnapshot ->
                        val post = dataSnapshot.getValue(Post::class.java)
                        post?.let {
                            if (it.poster_uid.equals(firebaseUser!!.uid)){
                                _postList.add(it)
                            }
                        }
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