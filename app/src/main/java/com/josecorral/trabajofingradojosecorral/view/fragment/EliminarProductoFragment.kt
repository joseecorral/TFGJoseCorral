package com.josecorral.trabajofingradojosecorral.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.FragmentEliminarProductoBinding

class EliminarProductoFragment : Fragment() {

    private lateinit var binding: FragmentEliminarProductoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEliminarProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEliminar.setOnClickListener {
            eliminarProducto()
        }
    }

    private fun eliminarProducto() {
        val nombreProducto = binding.nombreProducto.text.toString().trim()

        if (nombreProducto.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, completa el campo", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        db.collection("productos")
            .whereEqualTo("nombre", nombreProducto)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Toast.makeText(requireContext(), "Producto no encontrado", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                for (document in documents) {
                    db.collection("productos").document(document.id)
                        .delete()
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_eliminarProductoFragment_to_homeFragment)
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Error al eliminar producto: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al buscar producto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}