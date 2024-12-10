package com.losmugiwaras.a08_navigation_02.ui.seccion

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.losmugiwaras.a08_navigation_02.R
import java.util.Date
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher


class LoginFragment : Fragment() {

    private var auth = FirebaseAuth.getInstance()
    private var db = FirebaseFirestore.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<android.content.Intent>

    private lateinit var btnAutenticar: Button
    private lateinit var txtEmail: EditText
    private lateinit var txtContra: EditText
    private lateinit var txtRegister: TextView
    private lateinit var txtGoogle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_login, container, false)

        btnAutenticar = view.findViewById(R.id.btnAutenticar)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtContra = view.findViewById(R.id.txtContra)
        txtRegister = view.findViewById(R.id.txtRegister)
        txtGoogle = view.findViewById(R.id.txtGoogle)

        configureGoogleSignIn()
        setupGoogleLauncher()

        txtRegister.setOnClickListener {
            goToSignup()
        }

        btnAutenticar.setOnClickListener {
            loginWithEmail()
        }

        txtGoogle.setOnClickListener {
            signInWithGoogle()
        }

        return view
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(requireContext().getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun setupGoogleLauncher() {
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task?.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    showAlert("Error", "Error al iniciar sesión con Google")
                }
            }
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    saveLoginInfoToFirestore()
                } else {
                    showAlert("Error", "Error al autenticar con Google")
                }
            }
    }

    private fun saveLoginInfoToFirestore() {
        val dt = Date()
        val user = hashMapOf("ultAcceso" to dt.toString())

        auth.currentUser?.uid?.let { uid ->
            db.collection("datosUsuarios")
                .whereEqualTo("idemp", uid).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection("datosUsuarios").document(document.id)
                            .update(user as Map<String, Any>)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Error al actualizar los datos del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    private fun loginWithEmail() {
        val email = txtEmail.text.toString().trim()
        val password = txtContra.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Por favor ingresa email y contraseña")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                } else {
                    showAlert("Error", "Error en el inicio de sesión")
                }
            }
    }

    private fun goToSignup() {
        Toast.makeText(requireContext(), "Ir a Registro", Toast.LENGTH_SHORT).show()
    }

    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .create()
            .show()
    }
}
