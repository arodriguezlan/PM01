package com.losmugiwaras.a08_navigation_02.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.losmugiwaras.a08_navigation_02.R
import com.losmugiwaras.a08_navigation_02.ui.models.Research

class ResearchListFragment : Fragment() {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var researchAdapter: ResearchAdapter
    private val researchList = mutableListOf<Research>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_research_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Referencias a vistas
        val recyclerViewResearch = view.findViewById<RecyclerView>(R.id.recyclerViewResearch)
        val spinnerTopic = view.findViewById<Spinner>(R.id.spinnerTopic)
        val spinnerAcademicGrade = view.findViewById<Spinner>(R.id.spinnerAcademicGrade)
        val btnApplyFilters = view.findViewById<Button>(R.id.btnApplyFilters)
        val btnResetFilters = view.findViewById<Button>(R.id.btnResetFilters)

        // Initialize RecyclerView
        researchAdapter = ResearchAdapter(researchList)
        recyclerViewResearch.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = researchAdapter
        }

        // Populate Spinners
        setupSpinners(spinnerTopic, spinnerAcademicGrade)

        // Fetch initial research data
        fetchResearchData()

        // Set up Reset Button
        btnResetFilters.setOnClickListener {
            spinnerTopic.setSelection(0)
            spinnerAcademicGrade.setSelection(0)
            fetchResearchData() // Reset data without filters
        }

        // Apply Filters
        btnApplyFilters.setOnClickListener {
            applyFilters(spinnerTopic, spinnerAcademicGrade)
        }
    }

    private fun fetchResearchData() {
        firestore.collection("research").get().addOnSuccessListener { querySnapshot ->
            researchList.clear()
            for (document in querySnapshot) {
                val research = document.toObject(Research::class.java)
                researchList.add(research)
            }
            researchAdapter.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }

    private fun setupSpinners(spinnerTopic: Spinner, spinnerAcademicGrade: Spinner) {
        val topics = listOf("All Topics", "Science", "Technology", "Mathematics", "Literature")
        val academicGrades = listOf("All Grades", "Undergraduate", "Postgraduate", "Doctorate")

        setupSpinner(spinnerTopic, topics)
        setupSpinner(spinnerAcademicGrade, academicGrades)
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun applyFilters(spinnerTopic: Spinner, spinnerAcademicGrade: Spinner) {
        val selectedTopic = spinnerTopic.selectedItem.toString()
        val selectedGrade = spinnerAcademicGrade.selectedItem.toString()

        firestore.collection("research").get().addOnSuccessListener { querySnapshot ->
            researchList.clear()
            for (document in querySnapshot) {
                val research = document.toObject(Research::class.java)
                if ((selectedTopic == "All Topics" || research.topic == selectedTopic) &&
                    (selectedGrade == "All Grades" || research.academicGrade == selectedGrade)
                ) {
                    researchList.add(research)
                }
            }
            researchAdapter.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }
}
