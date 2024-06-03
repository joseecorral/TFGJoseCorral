package com.josecorral.trabajofingradojosecorral.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.integration.android.IntentIntegrator
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.FragmentQrBinding

class QRFragment : Fragment() {

    private lateinit var binding: FragmentQrBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentQrBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startQRScanner()
    }

    private fun startQRScanner() {
        IntentIntegrator.forSupportFragment(this).initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                desbloquearProducto(result.contents)
            } else {
                Toast.makeText(requireContext(), "Escaneo cancelado", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun desbloquearProducto(productId: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("productos").document(productId)
            .update("bloqueado", false)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Producto desbloqueado exitosamente", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_QRFragment_to_homeFragment)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al desbloquear producto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}