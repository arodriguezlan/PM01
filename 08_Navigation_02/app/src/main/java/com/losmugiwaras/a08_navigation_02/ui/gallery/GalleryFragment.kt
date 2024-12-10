package com.losmugiwaras.a08_navigation_02.ui.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.losmugiwaras.a08_navigation_02.R

class GalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var galleryAdapter: GalleryAdapter
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val galleryList = mutableListOf<GalleryItem>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_gallery, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewGallery)
        recyclerView.layoutManager = LinearLayoutManager(context)

        galleryAdapter = GalleryAdapter(galleryList)
        recyclerView.adapter = galleryAdapter

        fetchDataFromFirestore()

        return view
    }

    private fun fetchDataFromFirestore() {
        firestoreDB.collection("investigaciones")
            .get()
            .addOnSuccessListener { result ->
                galleryList.clear()
                for (document in result) {
                    val item = document.toObject(GalleryItem::class.java)
                    galleryList.add(item)
                }
                galleryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al obtener datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
