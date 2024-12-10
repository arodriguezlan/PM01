package com.losmugiwaras.a08_navigation_02.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.losmugiwaras.a08_navigation_02.R

class Proyecto : Fragment() {

    private lateinit var edtTitle: EditText
    private lateinit var edtDescription: EditText
    private lateinit var edtConclusions: EditText
    private lateinit var edtRecommendations: EditText
    private lateinit var spinnerArea: Spinner
    private lateinit var spinnerCiclo: Spinner

    private lateinit var edtCommentPdf: EditText
    private lateinit var btnSelectPdf: Button
    private lateinit var btnSelectImage: Button
    private lateinit var btnSubmit: Button

    private lateinit var imageView: ImageView

    private var selectedPdfUri: Uri? = null
    private var selectedImageUri: Uri? = null

    private val firestoreDB = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_proyecto, container, false)

        // Vinculación de vistas
        edtTitle = view.findViewById(R.id.edtTitle)
        edtDescription = view.findViewById(R.id.edtDescription)
        edtConclusions = view.findViewById(R.id.edtConclusions)
        edtRecommendations = view.findViewById(R.id.edtRecommendations)
        spinnerArea = view.findViewById(R.id.spinnerArea)
        spinnerCiclo = view.findViewById(R.id.spinnerCiclo)

        edtCommentPdf = view.findViewById(R.id.edtCommentPdf)
        btnSelectPdf = view.findViewById(R.id.btnSelectPdf)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        imageView = view.findViewById(R.id.imageView)

        btnSelectPdf.setOnClickListener { openPdfSelector() }
        btnSelectImage.setOnClickListener { openImageSelector() }
        btnSubmit.setOnClickListener { submitData() }

        return view
    }

    private fun openPdfSelector() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
        }
        startActivityForResult(intent, 1001)
    }

    private fun openImageSelector() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        startActivityForResult(intent, 1002)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                1001 -> { // Selección PDF
                    selectedPdfUri = data.data
                    Toast.makeText(requireContext(), "PDF seleccionado", Toast.LENGTH_SHORT).show()
                }
                1002 -> { // Selección de Imagen
                    selectedImageUri = data.data
                    imageView.setImageURI(selectedImageUri)
                    Toast.makeText(requireContext(), "Imagen seleccionada", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun submitData() {
        val title = edtTitle.text.toString()
        val description = edtDescription.text.toString()
        val conclusions = edtConclusions.text.toString()
        val recommendations = edtRecommendations.text.toString()
        val area = spinnerArea.selectedItem.toString()
        val ciclo = spinnerCiclo.selectedItem.toString()
        val pdfComment = edtCommentPdf.text.toString()

        if (title.isEmpty() || description.isEmpty() || conclusions.isEmpty() || recommendations.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos del formulario", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedPdfUri == null || selectedImageUri == null) {
            Toast.makeText(requireContext(), "Selecciona los archivos PDF e Imagen", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = storage.reference

        val pdfRef = storageRef.child("archivos/${System.currentTimeMillis()}_pdf.pdf")
        val imageRef = storageRef.child("archivos/${System.currentTimeMillis()}_image.jpg")

        pdfRef.putFile(selectedPdfUri!!).addOnSuccessListener {
            pdfRef.downloadUrl.addOnSuccessListener { pdfUrl ->
                imageRef.putFile(selectedImageUri!!).addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                        saveDataToFirestore(
                            pdfUrl.toString(),
                            imageUrl.toString(),
                            title,
                            description,
                            conclusions,
                            recommendations,
                            area,
                            ciclo,
                            pdfComment
                        )
                    }
                }
            }
        }
    }

    private fun saveDataToFirestore(
        pdfUrl: String,
        imageUrl: String,
        title: String,
        description: String,
        conclusions: String,
        recommendations: String,
        area: String,
        ciclo: String,
        pdfComment: String
    ) {
        val data = hashMapOf(
            "pdfUrl" to pdfUrl,
            "imageUrl" to imageUrl,
            "title" to title,
            "description" to description,
            "conclusions" to conclusions,
            "recommendations" to recommendations,
            "area" to area,
            "ciclo" to ciclo,
            "pdfComment" to pdfComment
        )

        firestoreDB.collection("investigaciones")
            .add(data)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error al guardar los datos", Toast.LENGTH_SHORT).show()
            }
    }
}
