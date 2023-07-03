package com.example.babbage_pb_19.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R.id as ID
import com.example.babbage_pb_19.adapters.PostAdapter
import com.example.babbage_pb_19.viewmodels.PostViewModel
import com.example.babbage_pb_19.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding!!

    private lateinit var viewModel : PostViewModel
    private lateinit var postRecyclerView: RecyclerView
    lateinit var adapter: PostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postRecyclerView = view.findViewById(ID.recycler_view_home)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        postRecyclerView.layoutManager = linearLayoutManager
        postRecyclerView.setHasFixedSize(true)
        adapter = PostAdapter()
        postRecyclerView.adapter = adapter
        viewModel = ViewModelProvider(this)[PostViewModel::class.java]

        viewModel.allPosts.observe(viewLifecycleOwner, Observer {
            adapter.updatePostList(it)

        })

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding =  FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
}



