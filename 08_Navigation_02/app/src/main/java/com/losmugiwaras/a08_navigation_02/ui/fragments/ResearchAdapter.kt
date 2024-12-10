package com.losmugiwaras.a08_navigation_02.ui.fragments
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.losmugiwaras.a08_navigation_02.ui.models.Research
import com.losmugiwaras.a08_navigation_02.R

class ResearchAdapter(
    private val researchList: List<Research>
) : RecyclerView.Adapter<ResearchAdapter.ResearchViewHolder>() {

    // Inflar el layout para cada elemento
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_research, parent, false)
        return ResearchViewHolder(view)
    }

    // Asignar datos al ViewHolder
    override fun onBindViewHolder(holder: ResearchViewHolder, position: Int) {
        val researchItem = researchList[position]
        holder.bind(researchItem)
    }

    // Cantidad de elementos en la lista
    override fun getItemCount(): Int = researchList.size

    // Clase ViewHolder para optimizar las vistas del RecyclerView
    inner class ResearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        private val topicTextView: TextView = itemView.findViewById(R.id.textViewTopic)
        private val emailTextView: TextView = itemView.findViewById(R.id.textViewEmail)
        private val gradeTextView: TextView = itemView.findViewById(R.id.textViewGrade)

        fun bind(research: Research) {
            titleTextView.text = research.title
            topicTextView.text = research.topic
            emailTextView.text = research.authorEmail
            gradeTextView.text = research.academicGrade
        }
    }
}

