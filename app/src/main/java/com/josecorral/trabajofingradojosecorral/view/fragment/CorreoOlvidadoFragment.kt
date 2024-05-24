package com.josecorral.trabajofingradojosecorral.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.FragmentCorreoOlvidadoBinding
import com.josecorral.trabajofingradojosecorral.viewmodels.ForgotPasswordViewModel


class CorreoOlvidadoFragment : Fragment() {

    private lateinit var binding: FragmentCorreoOlvidadoBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCorreoOlvidadoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bntContinuar.setOnClickListener {
            val email = binding.restablecerCorreo.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Ingrese su correo electrónico",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Correo de restablecimiento enviado",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val errorMessage =
                        task.exception?.message ?: "Error al enviar el correo de restablecimiento"
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
            binding.cruzRegistro.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}