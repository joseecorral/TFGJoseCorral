package com.josecorral.trabajofingradojosecorral.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.josecorral.trabajofingradojosecorral.databinding.FragmentFirstBinding
import com.josecorral.trabajofingradojosecorral.model.data.AppDatabase
import com.josecorral.trabajofingradojosecorral.model.data.Producto
import com.josecorral.trabajofingradojosecorral.view.product.ProductoAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Home : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    private lateinit var productoAdapter: ProductoAdapter
    private val productos = mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productoAdapter = ProductoAdapter(productos) { producto ->
            agregarProductoAlCarrito(producto)
        }
        binding.recyclerViewProductos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productoAdapter
        }

        obtenerProductosDesdeFirestore()
    }

    private fun obtenerProductosDesdeFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("productos")
            .get()
            .addOnSuccessListener { result ->
                productos.clear()
                for (document in result) {
                    val producto = document.toObject(Producto::class.java)
                    productos.add(producto)
                }
                productoAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun agregarProductoAlCarrito(producto: Producto) {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            db.productoDao().insertarProducto(producto)
            withContext(Dispatchers.Main) {
                Toast.makeText(requireContext(), "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
                // Aquí no navegamos automáticamente al PedidoFragment
            }
        }
    }
}