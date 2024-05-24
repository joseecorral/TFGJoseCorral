package com.josecorral.trabajofingradojosecorral.view.fragment

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.FragmentCorreoEnviadoBinding


class CorreoEnviadoFragment : Fragment() {

    private lateinit var binding: FragmentCorreoEnviadoBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentCorreoEnviadoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Crear un SpannableString para aplicar el subrayado al TextView
        val content = SpannableString(getString(R.string.direccion_correo_NOEnviado))
        content.setSpan(UnderlineSpan(), 0, content.length, 0)

        // Aplicar el SpannableString al TextView en tu diseño (asegúrate de tener un TextView con el id textViewLink)
        binding.noRecibido.text = content

        // Hacer el TextView cliclable
        binding.noRecibido.setOnClickListener {
            onLinkClick(it)
        }
        binding.cruzEnviado.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onLinkClick(view: View) {
        //reenviar correo, no recibido
    }
}