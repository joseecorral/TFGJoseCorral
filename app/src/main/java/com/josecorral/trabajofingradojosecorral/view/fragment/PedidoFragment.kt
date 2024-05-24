package com.josecorral.trabajofingradojosecorral.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.josecorral.trabajofingradojosecorral.databinding.FragmentPedidoBinding
import com.josecorral.trabajofingradojosecorral.model.data.AppDatabase
import com.josecorral.trabajofingradojosecorral.model.data.Producto
import com.josecorral.trabajofingradojosecorral.view.product.ProductoAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PedidoFragment : Fragment() {

    private lateinit var binding: FragmentPedidoBinding
    private lateinit var productoAdapter: ProductoAdapter
    private val productos = mutableListOf<Producto>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPedidoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productoAdapter = ProductoAdapter(productos) { producto ->
            // Maneja clics en los productos si es necesario
        }
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productoAdapter
        }

        cargarProductosDelCarrito()
    }

    private fun cargarProductosDelCarrito() {
        val db = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            val productosCarrito = db.productoDao().obtenerTodosLosProductos()
            withContext(Dispatchers.Main) {
                productos.clear()
                productos.addAll(productosCarrito)
                productoAdapter.notifyDataSetChanged()
            }
        }
    }
}