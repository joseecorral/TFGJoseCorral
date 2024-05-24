package com.josecorral.trabajofingradojosecorral.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.FragmentRegistroBinding
import com.josecorral.trabajofingradojosecorral.model.FirebaseAuthManager
import com.josecorral.trabajofingradojosecorral.viewmodels.RegistroViewModel
import com.josecorral.trabajofingradojosecorral.viewmodels.RegistroViewModelFactory

class RegistroFragment : Fragment() {

    private lateinit var binding: FragmentRegistroBinding
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private val viewModel: RegistroViewModel by viewModels {
        RegistroViewModelFactory(FirebaseAuthManager())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistroBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
// Puedes manejar el estado del usuario aquí si es necesario

        }
    }
    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authStateListener)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bntRegistro.setOnClickListener {
            val email = binding.correo.text.toString().trim()
            val password = binding.contra.editText?.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Correo y contraseña son requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.signUp(email, password).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    saveUserToFirestore(userId, email)
                    findNavController().navigate(R.id.action_registro_to_validarmail)
                } else {
                    val errorMessage = task.exception?.message ?: "Error desconocido al registrarse"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveUserToFirestore(userId: String, email: String) {
        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "email" to email,
            "role" to "user" // Por defecto, asignar rol de usuario normal
        )
        db.collection("users").document(userId).set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Usuario guardado correctamente en Firestore")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al guardar el usuario en Firestore", e)
            }
    }
}