package com.losmugiwaras.a08_navigation_02.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.losmugiwaras.a08_navigation_02.R
import com.losmugiwaras.a08_navigation_02.ui.fragments.Comentarios

class ComentarioAdapter(private val comentarioList: List<Comentarios>) :
    RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    class ComentarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.txtNombre)
        val txtComentario: TextView = view.findViewById(R.id.txtComentario)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val txtFecha: TextView = view.findViewById(R.id.txtFecha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comentario, parent, false)
        return ComentarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarioList[position]
        holder.txtNombre.text = comentario.nombre
        holder.txtComentario.text = comentario.comentario
        holder.txtRating.text = "Calificación: ${comentario.rating}"
        holder.txtFecha.text = "Fecha: ${comentario.fecha}"
    }

    override fun getItemCount(): Int {
        return comentarioList.size
    }
}
