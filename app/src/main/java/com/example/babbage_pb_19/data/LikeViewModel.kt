package com.example.babbage_pb_19.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.babbage_pb_19.repository.LikeRepository

class LikeViewModel : ViewModel() {
    private val repository : LikeRepository = LikeRepository().getInstance()
    private val _allLikes = MutableLiveData<Map<String, Long>>()
    val allLikes : LiveData<Map<String, Long>> = _allLikes
    var user_id : String =""

    fun loadLikes(user_id: String) {
        repository.loadLikes(user_id, allLikes as MutableLiveData<Map<String, Long>>)
    }
    init {
        repository.loadLikes(user_id, allLikes as MutableLiveData<Map<String, Long>>)
    }
}