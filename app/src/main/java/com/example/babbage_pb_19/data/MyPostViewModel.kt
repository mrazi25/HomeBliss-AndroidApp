package com.example.babbage_pb_19.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.babbage_pb_19.repository.MyPostRepository

class MyPostViewModel : ViewModel() {
    private val repository : MyPostRepository = MyPostRepository().getInstance()
    private val _allPost = MutableLiveData<List<Post>>()
    val allPost : LiveData<List<Post>> = _allPost


    init {
        repository.loadMyPosts(_allPost)
    }
}