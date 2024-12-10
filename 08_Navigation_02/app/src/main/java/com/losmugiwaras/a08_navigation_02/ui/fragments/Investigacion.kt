package com.losmugiwaras.a08_navigation_02.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.losmugiwaras.a08_navigation_02.R

class Investigacion: AppCompatActivity() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var edtConclusions: EditText
    private lateinit var edtRecommendations: EditText
    private lateinit var spinnerArea: Spinner
    private lateinit var spinnerCiclo: Spinner
    private lateinit var btnSubmit: Button

    private val firestoreDB = FirebaseFirestore.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proyecto)

        edtTitle = findViewById(R.id.edtTitle)
        edtDescription = findViewById(R.id.edtDescription)
        edtConclusions = findViewById(R.id.edtConclusions)
        edtRecommendations = findViewById(R.id.edtRecommendations)
        spinnerArea = findViewById(R.id.spinnerArea)
        spinnerCiclo = findViewById(R.id.spinnerCiclo)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            saveInvestigacionToFirestore()
        }
    }

    private fun saveInvestigacionToFirestore() {
        val title = edtTitle.text.toString()
        val description = edtDescription.text.toString()
        val conclusions = edtConclusions.text.toString()
        val recommendations = edtRecommendations.text.toString()
        val area = spinnerArea.selectedItem.toString()
        val ciclo = spinnerCiclo.selectedItem.toString()

        if (title.isEmpty() || description.isEmpty() || conclusions.isEmpty() || recommendations.isEmpty()) {
            Toast.makeText(this, "Todos los campos deben estar llenos", Toast.LENGTH_SHORT).show()
            return
        }

        val data = hashMapOf(
            "title" to title,
            "description" to description,
            "conclusions" to conclusions,
            "recommendations" to recommendations,
            "area" to area,
            "ciclo" to ciclo
        )

        firestoreDB.collection("investigaciones")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Formulario enviado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar el formulario", Toast.LENGTH_SHORT).show()
            }
    }
}
