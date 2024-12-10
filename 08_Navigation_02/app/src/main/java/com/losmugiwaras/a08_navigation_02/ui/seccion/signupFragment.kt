package com.losmugiwaras.a08_navigation_02.ui.seccion

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.losmugiwaras.a08_navigation_02.R
import java.util.Date


class SignupFragment : Fragment() {

    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()

    private lateinit var txtRNombre: EditText
    private lateinit var txtREmail: EditText
    private lateinit var txtRContra: EditText
    private lateinit var txtRreContra: EditText
    private lateinit var btnRegistrarU: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_signup, container, false)

        txtRNombre = view.findViewById(R.id.txtRNombre)
        txtREmail = view.findViewById(R.id.txtREmail)
        txtRContra = view.findViewById(R.id.txtRContra)
        txtRreContra = view.findViewById(R.id.txtRreContra)
        btnRegistrarU = view.findViewById(R.id.btnRegistrarU)

        btnRegistrarU.setOnClickListener {
            registrarUsuario()
        }

        return view
    }

    private fun registrarUsuario() {
        val nombre = txtRNombre.text.toString()
        val email = txtREmail.text.toString()
        val contra = txtRContra.text.toString()
        val reContra = txtRreContra.text.toString()

        if (nombre.isEmpty() || email.isEmpty() || contra.isEmpty() || reContra.isEmpty()) {
            Toast.makeText(requireContext(), "Favor de llenar todos los campos", Toast.LENGTH_SHORT).show()
        } else {
            if (contra == reContra) {
                auth.createUserWithEmailAndPassword(email, contra)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            val dt: Date = Date()
                            val user = hashMapOf(
                                "idemp" to task.result?.user?.uid,
                                "usuario" to nombre,
                                "email" to email,
                                "ultAcceso" to dt.toString()
                            )
                            db.collection("datosUsuarios")
                                .add(user)
                                .addOnSuccessListener {
                                    val prefe = requireContext().getSharedPreferences("appData", Context.MODE_PRIVATE)
                                    val editor = prefe.edit()
                                    editor.putString("email", email)
                                    editor.putString("contra", contra)
                                    editor.apply()

                                    Toast.makeText(requireContext(), "Usuario registrado correctamente", Toast.LENGTH_SHORT).show()
                                    requireActivity().finish() // Terminamos la navegación aquí
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(requireContext(), "Error al registrar usuario", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
