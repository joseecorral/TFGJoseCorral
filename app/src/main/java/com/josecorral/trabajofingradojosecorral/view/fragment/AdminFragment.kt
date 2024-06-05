package com.josecorral.trabajofingradojosecorral.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.FragmentAdminBinding

class AdminFragment : Fragment() {

    private lateinit var binding: FragmentAdminBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bntAgregarProducto.setOnClickListener {
            // Lógica para agregar un producto
            findNavController().navigate(R.id.action_adminFragment_to_agregarProductoFragment)
        }

        binding.bntEliminarProducto.setOnClickListener {
            // Lógica para eliminar un producto
            findNavController().navigate(R.id.action_adminFragment_to_eliminarProductoFragment)
        }
    }
}