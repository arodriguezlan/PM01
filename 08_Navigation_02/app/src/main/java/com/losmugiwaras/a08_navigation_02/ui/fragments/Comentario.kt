package com.losmugiwaras.a08_navigation_02.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.losmugiwaras.a08_navigation_02.R
import com.losmugiwaras.a08_navigation_02.ui.fragments.Comentarios
import com.losmugiwaras.a08_navigation_02.ui.adapters.ComentarioAdapter

class ComentariosFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ComentarioAdapter
    private val comentarioList = mutableListOf<Comentarios>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comentarios, container, false)

        // Mostrar información del proyecto
        displayProjectInfo(view)

        // Configuración de RecyclerView, Adapter, etc.
        recyclerView = view.findViewById(R.id.recyclerViewComentarios)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ComentarioAdapter(comentarioList)
        recyclerView.adapter = adapter

        view.findViewById<Button>(R.id.btnAgregarComentario).setOnClickListener {
            showPopup()
        }

        fetchComentarios()
        return view
    }


    private fun fetchComentarios() {
        db.collection("Comentarios")
            .orderBy("fecha")
            .get()
            .addOnSuccessListener { snapshot ->
                comentarioList.clear()
                snapshot.forEach { document ->
                    comentarioList.add(
                        Comentarios(
                            document.getString("nombre") ?: "",
                            document.getString("comentario") ?: "",
                            document.getLong("rating")?.toInt() ?: 0,
                            document.getLong("fecha") ?: 0
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }
    }

    private fun showPopup() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_comentario, null)
        val editNombre = dialogView.findViewById<EditText>(R.id.editTextNombre)
        val editComentario = dialogView.findViewById<EditText>(R.id.editTextComentario)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val comentario = Comentarios(
                    editNombre.text.toString(),
                    editComentario.text.toString(),
                    3,
                    System.currentTimeMillis()
                )
                db.collection("Comentarios").add(comentario).addOnSuccessListener {
                    fetchComentarios()
                }
            }
            .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun displayProjectInfo(view: View) {
        view.findViewById<TextView>(R.id.txtProyectoTitulo).text = "Sistema de Comentarios"
        view.findViewById<TextView>(R.id.txtProyectoDescripcion).text = "Esta aplicación permite agregar, visualizar y calificar comentarios."
    }
}
