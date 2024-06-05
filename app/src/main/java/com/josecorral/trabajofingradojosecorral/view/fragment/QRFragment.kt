package com.josecorral.trabajofingradojosecorral.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import com.josecorral.trabajofingradojosecorral.databinding.FragmentQrBinding

class QRFragment : Fragment() {

    private lateinit var binding: FragmentQrBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Iniciar el escaneo del QR cuando se cargue el fragmento
        iniciarEscaneoQR()
    }

    private fun iniciarEscaneoQR() {
        IntentIntegrator.forSupportFragment(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show()
            } else {
                desbloquearProducto(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun desbloquearProducto(productId: String) {
        val db = FirebaseFirestore.getInstance()
        val productoRef = db.collection("productos").document(productId)

        productoRef.update("bloqueado", false)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Producto desbloqueado", Toast.LENGTH_SHORT).show()
                // Regresar al fragmento anterior o realizar alguna acción
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error al desbloquear el producto", Toast.LENGTH_SHORT).show()
            }
    }
}
