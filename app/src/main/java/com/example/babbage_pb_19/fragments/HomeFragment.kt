package com.example.babbage_pb_19.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.babbage_pb_19.R
import com.example.babbage_pb_19.data.Post
import com.example.babbage_pb_19.adapter.PostAdapter
import com.example.babbage_pb_19.data.PostViewModel
import com.google.firebase.database.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */


class HomeFragment : Fragment() {

    private lateinit var viewModel : PostViewModel
    private lateinit var postRecyclerView: RecyclerView
    lateinit var adapter: PostAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postRecyclerView = view.findViewById(R.id.recycler_view_home)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        postRecyclerView.layoutManager = linearLayoutManager
        postRecyclerView.setHasFixedSize(true)
        adapter = PostAdapter()
        postRecyclerView.adapter = adapter
        viewModel = ViewModelProvider(this)[PostViewModel::class.java]

        viewModel.allUsers.observe(viewLifecycleOwner, Observer {

            adapter.updatePostList(it)

        })

    }
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val dummyPostList = listOf(
//            Post(R.drawable.ic_image_teal.toString(), "User 1", R.drawable.ic_image_teal.toString(), 10, "Publisher 1", "Description 1", "Comments 1"),
//        )
//
//        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_home)
//        adapter = PostAdapter(context!!, dummyPostList)
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // Mengambil postingan dari Firebase Realtime Database dan mengatur data ke adapter
//    }
//
//    override fun onItemClick(post: Post) {
//        // Tindakan yang akan dilakukan saat item diklik
//        Toast.makeText(requireContext(), "Item clicked: ${post.userName}", Toast.LENGTH_SHORT).show()
//    }
//
//    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
//    private val postsRef: DatabaseReference = database.reference.child("Posts")
//
//    private fun getPosts(callback: (List<Post>) -> Unit) {
//        val postList = mutableListOf<Post>()
//
//        val valueEventListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (postSnapshot in dataSnapshot.children) {
//                    val post = postSnapshot.getValue(Post::class.java)
//                    post?.let { postList.add(it) }
//                }
//
//                callback(postList)
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Handle error jika terjadi kegagalan dalam mengambil data
//            }
//        }
//
//        postsRef.addValueEventListener(valueEventListener)
//    }
//    private lateinit var adapter: PostAdapter
//    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
//    private val postCollection = firestore.collection("post")
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val recyclerView: RecyclerView = view.findViewById(R.id.app_bar_layout)
//        adapter = PostAdapter(requireContext(), emptyList())
//        recyclerView.adapter = adapter
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        // Mengambil postingan dari Firebase Firestore dan mengatur data ke adapter
//        FirebaseManager.getPosts { postList ->
//            adapter.updateData(postList)
//        }
//    }
//
//    override fun onItemClick(post: Post) {
//        // Tindakan yang akan dilakukan saat item diklik
//        Toast.makeText(requireContext(), "Item clicked: ${post.userName}", Toast.LENGTH_SHORT).show()
//    }
//
//    fun getPosts(callback: (List<Post>) -> Unit) {
//        postCollection.get()
//            .addOnSuccessListener { result ->
//                val postList = mutableListOf<Post>()
//                for (document in result) {
//                    val userProfileImageUrl = document.getString("userProfileImageUrl") ?: ""
//                    val userName = document.getString("userName") ?: ""
//                    val postImageUrl = document.getString("postImageUrl") ?: ""
//                    val likes = document.getLong("likes")?.toInt() ?: 0
//                    val publisher = document.getString("publisher") ?: ""
//                    val description = document.getString("description") ?: ""
//                    val comments = document.getString("comments") ?: ""
//
//                    val post = Post(userProfileImageUrl, userName, postImageUrl, likes, publisher, description, comments)
//                    postList.add(post)
//                }
//
//                callback(postList)
//            }
//            .addOnFailureListener { exception ->
//                // Handle error jika terjadi kegagalan dalam mengambil data
//            }
//    }
}



