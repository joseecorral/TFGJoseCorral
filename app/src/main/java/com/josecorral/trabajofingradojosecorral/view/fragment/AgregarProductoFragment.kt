package com.josecorral.trabajofingradojosecorral.view.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.josecorral.trabajofingradojosecorral.R
import com.josecorral.trabajofingradojosecorral.databinding.FragmentAgregarProductoBinding
import com.josecorral.trabajofingradojosecorral.model.data.Producto
import java.util.UUID

class AgregarProductoFragment : Fragment() {

private lateinit var binding: FragmentAgregarProductoBinding
private var imageUri: Uri? = null

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
): View? {
    binding = FragmentAgregarProductoBinding.inflate(inflater, container, false)
    return binding.root
}

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding.btnSeleccionarImagen.setOnClickListener {
        seleccionarImagen()
    }

    binding.btnAgregarProducto.setOnClickListener {
        agregarProducto()
    }
}

private fun seleccionarImagen() {
    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        type = "image/*"
    }
    startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), 100)
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
        imageUri = data?.data
        binding.ivImagenProducto.setImageURI(imageUri)
    }
}

private fun agregarProducto() {
    val nombreProducto = binding.etNombreProducto.text.toString().trim()
    val precioProducto = binding.etPrecioProducto.text.toString().trim()

    if (nombreProducto.isEmpty() || precioProducto.isEmpty()|| imageUri == null) {
        Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        return
    }

    val storageRef = FirebaseStorage.getInstance().reference.child("productos/${UUID.randomUUID()}")
    storageRef.putFile(imageUri!!).addOnSuccessListener { taskSnapshot ->
        taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
            val producto = Producto(
                id = UUID.randomUUID().toString(),
                nombre = nombreProducto,
                precio = precioProducto.toFloat(),
                imagenUrl = uri.toString(),
                bloqueado = false
            )

            FirebaseFirestore.getInstance().collection("productos")
                .add(producto)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Producto agregado exitosamente", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_agregarProductoFragment_to_homeFragment)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al agregar producto: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }.addOnFailureListener { e ->
        Toast.makeText(requireContext(), "Error al subir imagen: ${e.message}", Toast.LENGTH_SHORT).show()
    }
    }
}