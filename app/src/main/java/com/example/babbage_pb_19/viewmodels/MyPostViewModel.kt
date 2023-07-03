package com.example.babbage_pb_19.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.babbage_pb_19.datas.Post
import com.example.babbage_pb_19.repositories.MyPostRepository

class MyPostViewModel : ViewModel() {
    private val repository : MyPostRepository = MyPostRepository().getInstance()
    private val _allPost = MutableLiveData<List<Post>>()
    val allPost : LiveData<List<Post>> = _allPost


    init {
        repository.loadMyPosts(_allPost)
    }
}